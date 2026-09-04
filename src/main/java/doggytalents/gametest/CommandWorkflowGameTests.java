package doggytalents.gametest;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import doggytalents.api.feature.DogMode;
import doggytalents.common.command.DoggyCommands;
import doggytalents.common.config.ConfigHandler;
import doggytalents.common.entity.Dog;
import doggytalents.common.item.WhistleItem.WhistleMode;
import doggytalents.common.network.DTNNetworkHandler.NetworkEvent.Context;
import doggytalents.common.network.packet.CanineTrackerPackets;
import doggytalents.common.network.packet.WhistleUsePacket;
import doggytalents.common.network.packet.data.CanineTrackerData.StartLocatingData;
import doggytalents.common.network.packet.data.WhistleUseData;
import doggytalents.common.storage.DogLocationStorage;
import doggytalents.common.storage.DogRespawnStorage;
import doggytalents.common.util.ItemUtil;
import doggytalents.common.util.NBTUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public final class CommandWorkflowGameTests {
    private CommandWorkflowGameTests() {
    }

    /** REVIEW-COMMAND-01-LOCATE: execute real selectors, feedback and rejection paths. */
    public static void locate(GameTestHelper helper) {
        var player = player(helper, "LocateOwner");
        var dog = dog(helper, player, "Locate Pup", 2);
        var duplicate = dog(helper, player, "Different Pup", 3);
        var storage = DogLocationStorage.get(helper.getLevel());
        storage.getOrCreateData(dog).update(dog);
        var source = source(player);
        String byUuid = "dog locate byuuid LocateOwner " + dog.getUUID();
        try {
            require(helper, execute(source, byUuid) == 1, "UUID locate failed");
            require(helper, player.messages.size() == 1
                && player.messages.get(0).contains("Locate Pup")
                && player.messages.get(0).contains("minecraft:overworld")
                && player.messages.get(0).contains(Integer.toString(dog.blockPosition().getX())),
                "locate feedback omitted the dog's name, position or dimension");
            require(helper, execute(source, "dog locate byname LocateOwner \"Locate Pup\"") == 1,
                "quoted-name locate failed");
            rejects(helper, source, "dog locate byuuid LocateOwner bad-uuid", DoggyCommands.BAD_UUID_STRING);
            rejects(helper, source, "dog locate byuuid LocateOwner " + UUID.randomUUID(), DoggyCommands.NOTFOUND_EXCEPTION);
            rejects(helper, source, "dog locate byname UnknownOwner \"Locate Pup\"", DoggyCommands.NOTFOUND_EXCEPTION);
            rejects(helper, source, "dog locate byname LocateOwner Missing", DoggyCommands.NOTFOUND_EXCEPTION);
            duplicate.setDogCustomName(Component.literal("Locate Pup"));
            storage.getOrCreateData(duplicate).update(duplicate);
            rejects(helper, source, "dog locate byname LocateOwner \"Locate Pup\"", DoggyCommands.AMBIGUOUS_NAME_EXCEPTION);
            require(helper, execute(source, "dog locate byname LocateOwner \"Locate Pup\" " + dog.getUUID()) == 1,
                "UUID did not disambiguate a duplicate dog name");
            rejects(helper, source.withPermission(permission -> false), byUuid, null);
            rejects(helper, helper.getLevel().getServer().createCommandSourceStack(), byUuid,
                CommandSourceStack.ERROR_NOT_PLAYER);
            require(helper, storage.getData(dog.getUUID()) != null, "locating consumed a location record");
        } finally {
            storage.remove(dog.getUUID());
            storage.remove(duplicate.getUUID());
            dog.discard();
            duplicate.discard();
            DogRespawnStorage.get(helper.getLevel()).remove(dog.getUUID());
            DogRespawnStorage.get(helper.getLevel()).remove(duplicate.getUUID());
            player.discard();
        }
        helper.succeed();
    }

    /** REVIEW-COMMAND-01-REVIVE: commands create a dog and consume exactly its stored record. */
    public static void revive(GameTestHelper helper) {
        var player = player(helper, "ReviveOwner");
        var dog = dog(helper, player, "Revive Pup", 2);
        var other = dog(helper, player, "Other Pup", 3);
        var storage = DogRespawnStorage.get(helper.getLevel());
        storage.putData(dog);
        storage.putData(other);
        UUID firstId = dog.getUUID();
        UUID secondId = other.getUUID();
        dog.discard();
        other.discard();
        var source = source(player);
        String byUuid = "dog revive byuuid ReviveOwner " + firstId;
        try {
            rejects(helper, source.withPermission(permission -> false), byUuid, null);
            require(helper, storage.getData(firstId) != null && helper.getLevel().getEntity(firstId) == null,
                "denied revival mutated stored or live state");
            rejects(helper, source, "dog revive byuuid ReviveOwner invalid", DoggyCommands.BAD_UUID_STRING);
            rejects(helper, source, "dog revive byname UnknownOwner \"Revive Pup\"", DoggyCommands.NOTFOUND_EXCEPTION);
            rejects(helper, source, "dog revive byname ReviveOwner Missing", DoggyCommands.NOTFOUND_EXCEPTION);
            rejects(helper, helper.getLevel().getServer().createCommandSourceStack(), byUuid,
                CommandSourceStack.ERROR_NOT_PLAYER);
            require(helper, execute(source, byUuid) == 1, "UUID revive failed");
            assertRevived(helper, player, firstId, "Revive Pup");
            require(helper, storage.getData(firstId) == null && storage.getData(secondId) != null,
                "revive did not consume only its target record");
            rejects(helper, source, byUuid, DoggyCommands.NOTFOUND_EXCEPTION);
            require(helper, execute(source, "dog revive byname ReviveOwner \"Other Pup\"") == 1,
                "name revive failed");
            assertRevived(helper, player, secondId, "Other Pup");
            require(helper, storage.getData(secondId) == null, "name revive retained its consumed record");
            // Re-store same-name dogs to exercise ambiguity without spawning a duplicate.
            Dog first = (Dog) helper.getLevel().getEntity(firstId);
            Dog second = (Dog) helper.getLevel().getEntity(secondId);
            second.setDogCustomName(Component.literal("Revive Pup"));
            storage.putData(first);
            storage.putData(second);
            rejects(helper, source, "dog revive byname ReviveOwner \"Revive Pup\"", DoggyCommands.AMBIGUOUS_NAME_EXCEPTION);
            require(helper, storage.getData(firstId) != null && storage.getData(secondId) != null,
                "ambiguous revival consumed a record");
        } finally {
            for (UUID id : List.of(firstId, secondId)) {
                var entity = helper.getLevel().getEntity(id);
                if (entity != null) entity.discard();
                storage.remove(id);
                DogLocationStorage.get(helper.getLevel()).remove(id);
            }
            player.discard();
        }
        helper.succeed();
    }

    /** REVIEW-COMMAND-01-TRACKER: selection writes authoritative data, invalid targets do not. */
    public static void tracker(GameTestHelper helper) {
        var player = player(helper, "TrackerOwner");
        var dog = dog(helper, player, "Tracked Pup", 2);
        var stranger = dog(helper, player, "Other Owner", 3);
        stranger.migrateOwner(UUID.randomUUID());
        var storage = DogLocationStorage.get(helper.getLevel());
        storage.getOrCreateData(dog).update(dog);
        storage.getOrCreateData(stranger).update(stranger);
        var context = context(player);
        var select = new CanineTrackerPackets.StartLocatingPacket();
        boolean allowAny = ConfigHandler.SERVER.ALLOW_TRACK_ANY_DOG.get();
        try {
            var tracker = new ItemStack(DoggyItems.CREATIVE_CANINE_TRACKER.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, tracker);
            select.handle(new StartLocatingData(dog.getUUID()), () -> context);
            var tag = ItemUtil.getTag(tracker);
            require(helper, dog.getUUID().equals(NBTUtil.getUniqueId(tag, "uuid"))
                && "Tracked Pup".equals(tag.getStringOr("name", ""))
                && tag.getIntOr("posX", 0) == dog.blockPosition().getX()
                && tag.getIntOr("posY", 0) == dog.blockPosition().getY()
                && tag.getIntOr("posZ", 0) == dog.blockPosition().getZ(), "tracker selection lost authoritative target data");
            var selected = tag.copy();
            var update = new CanineTrackerPackets.RequestPosUpdatePacket();
            player.payloads.clear();
            update.handle(new doggytalents.common.network.packet.data.CanineTrackerData.RequestPosUpdateData(
                dog.getUUID(), dog.blockPosition().offset(10, 0, 0)), () -> context);
            require(helper, player.payloads.size() == 1, "stale tracker coordinates did not produce a correction");
            player.payloads.clear();
            update.handle(new doggytalents.common.network.packet.data.CanineTrackerData.RequestPosUpdateData(
                dog.getUUID(), dog.blockPosition()), () -> context);
            update.handle(new doggytalents.common.network.packet.data.CanineTrackerData.RequestPosUpdateData(
                stranger.getUUID(), BlockPos.ZERO), () -> context);
            update.handle(new doggytalents.common.network.packet.data.CanineTrackerData.RequestPosUpdateData(
                UUID.randomUUID(), BlockPos.ZERO), () -> context);
            require(helper, player.payloads.isEmpty(), "unchanged, foreign or missing target produced a position update");
            select.handle(new StartLocatingData(UUID.randomUUID()), () -> context);
            require(helper, selected.equals(ItemUtil.getTag(tracker)), "missing tracker target changed selection");
            select.handle(new StartLocatingData(stranger.getUUID()), () -> context);
            require(helper, selected.equals(ItemUtil.getTag(tracker)), "tracker selected another owner's dog");
            var record = storage.getData(dog.getUUID());
            var stored = record.write(new CompoundTag());
            var otherDimension = stored.copy();
            otherDimension.putString("dimension", "minecraft:the_nether");
            record.read(otherDimension);
            DoggyItems.CREATIVE_CANINE_TRACKER.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            require(helper, !ItemUtil.hasTag(tracker), "tracker use did not clear selection");
            select.handle(new StartLocatingData(dog.getUUID()), () -> context);
            require(helper, !ItemUtil.hasTag(tracker), "cross-dimension selection changed the tracker");
            record.read(stored);
            ConfigHandler.SERVER.ALLOW_TRACK_ANY_DOG.set(false);
            var ordinaryTracker = new ItemStack(DoggyItems.CANINE_TRACKER.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, ordinaryTracker);
            select.handle(new StartLocatingData(dog.getUUID()), () -> context);
            require(helper, !ItemUtil.hasTag(ordinaryTracker), "ordinary tracker selected a dog without locator eligibility");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
            select.handle(new StartLocatingData(dog.getUUID()), () -> context);
            require(helper, !ItemUtil.hasTag(player.getMainHandItem()), "tracker request wrote to a non-tracker item");
        } finally {
            ConfigHandler.SERVER.ALLOW_TRACK_ANY_DOG.set(allowAny);
            storage.remove(dog.getUUID());
            storage.remove(stranger.getUUID());
            dog.discard();
            stranger.discard();
            DogRespawnStorage.get(helper.getLevel()).remove(dog.getUUID());
            DogRespawnStorage.get(helper.getLevel()).remove(stranger.getUUID());
            player.discard();
        }
        helper.succeed();
    }

    /** REVIEW-COMMAND-01-WHISTLE: held-item and hotkey paths change only eligible dogs. */
    public static void whistle(GameTestHelper helper) {
        var player = player(helper, "WhistleOwner");
        var dog = dog(helper, player, "Whistle Pup", 2);
        var stranger = dog(helper, player, "Stranger", 3);
        stranger.migrateOwner(UUID.randomUUID());
        var distant = dog(helper, player, "Distant", 4);
        distant.setPos(player.position().add(150, 0, 0));
        var whistle = new ItemStack(DoggyItems.WHISTLE.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, whistle);
        var context = context(player);
        var hotkey = new WhistleUsePacket();
        try {
            var data = new CompoundTag();
            data.putByte("mode", (byte) WhistleMode.STAY.getIndex());
            ItemUtil.putTag(whistle, data);
            DoggyItems.WHISTLE.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            require(helper, dog.isOrderedToSit() && !stranger.isOrderedToSit() && !distant.isOrderedToSit(),
                "stay whistle ignored ownership or range: owned=" + dog.isOrderedToSit()
                    + ", stranger=" + stranger.isOrderedToSit() + ", distant=" + distant.isOrderedToSit());
            hotkey.handle(new WhistleUseData(WhistleMode.STAND.getIndex()), () -> context);
            require(helper, dog.isOrderedToSit(), "hotkey bypassed the whistle cooldown");
            clearCooldown(player);
            hotkey.handle(new WhistleUseData(-1), () -> context);
            hotkey.handle(new WhistleUseData(Integer.MAX_VALUE), () -> context);
            require(helper, dog.isOrderedToSit(), "invalid hotkey mode changed dog state");
            hotkey.handle(new WhistleUseData(WhistleMode.STAND.getIndex()), () -> context);
            require(helper, !dog.isOrderedToSit(), "stand hotkey did not stand the dog");
            clearCooldown(player);
            dog.setDogOnDuty(false);
            data.putBoolean("dog_on_duty_only", true);
            ItemUtil.putTag(whistle, data);
            hotkey.handle(new WhistleUseData(WhistleMode.STAY.getIndex()), () -> context);
            require(helper, !dog.isOrderedToSit(), "duty-only whistle affected an off-duty dog");
            clearCooldown(player);
            dog.setDogOnDuty(true);
            hotkey.handle(new WhistleUseData(WhistleMode.STAY.getIndex()), () -> context);
            require(helper, dog.isOrderedToSit(), "duty-only whistle ignored an on-duty dog");
            clearCooldown(player);
            var target = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.LOAD);
            dog.setTarget(target);
            hotkey.handle(new WhistleUseData(WhistleMode.STOP_ATTACKING.getIndex()), () -> context);
            require(helper, dog.getTarget() == null, "stop-attacking whistle retained the target");
            clearCooldown(player);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            hotkey.handle(new WhistleUseData(WhistleMode.STAND.getIndex()), () -> context);
            require(helper, dog.isOrderedToSit(), "hotkey worked without a held whistle");
        } finally {
            for (Dog entry : List.of(dog, stranger, distant)) {
                DogLocationStorage.get(helper.getLevel()).remove(entry.getUUID());
                entry.discard();
                DogRespawnStorage.get(helper.getLevel()).remove(entry.getUUID());
            }
            player.discard();
        }
        helper.succeed();
    }

    private static void clearCooldown(RecordingPlayer player) {
        for (int i = 0; i < 50; ++i) player.getCooldowns().tick();
    }

    private static void assertRevived(GameTestHelper helper, RecordingPlayer owner, UUID id, String name) {
        var entity = helper.getLevel().getEntity(id);
        require(helper, entity instanceof Dog, "revive did not create its dog UUID");
        var dog = (Dog) entity;
        require(helper, dog.isOwnedBy(owner) && name.equals(dog.getName().getString())
            && dog.isAlive() && !dog.isDefeated() && dog.isOrderedToSit()
            && dog.blockPosition().equals(owner.blockPosition().above()), "revived dog state or position differs");
        require(helper, owner.messages.stream().anyMatch(message -> message.contains(name)),
            "revive omitted success feedback");
    }

    private static int execute(CommandSourceStack source, String command) {
        try {
            return source.getServer().getCommands().getDispatcher().execute(command, source);
        } catch (CommandSyntaxException exception) {
            throw new IllegalStateException(command, exception);
        }
    }

    private static void rejects(GameTestHelper helper, CommandSourceStack source, String command,
            CommandExceptionType expected) {
        try {
            source.getServer().getCommands().getDispatcher().execute(command, source);
            helper.fail("command unexpectedly accepted: " + command);
        } catch (CommandSyntaxException exception) {
            require(helper, expected == null || exception.getType() == expected,
                "wrong failure for " + command + ": " + exception.getMessage());
        }
    }

    private static CommandSourceStack source(RecordingPlayer player) {
        return player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.OWNER);
    }

    private static Context context(RecordingPlayer player) {
        return new Context(new ServerPayloadContext(player.connection,
            Identifier.fromNamespaceAndPath("doggytalents", "gametest")));
    }

    private static RecordingPlayer player(GameTestHelper helper, String name) {
        var player = new RecordingPlayer(helper.getLevel(), name);
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(1, 2, 1))));
        helper.getLevel().addNewPlayer(player);
        return player;
    }

    private static Dog dog(GameTestHelper helper, RecordingPlayer owner, String name, int x) {
        Dog dog = DoggyEntityTypes.DOG.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, dog != null, "dog could not be created");
        dog.tame(owner);
        dog.setDogCustomName(Component.literal(name));
        dog.setMode(DogMode.DOCILE);
        dog.setNoAi(true);
        dog.setOrderedToSit(false);
        dog.setPos(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(x, 2, 2))));
        helper.getLevel().addFreshEntity(dog);
        return dog;
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) helper.fail(message);
    }

    private static final class RecordingPlayer extends FakePlayer {
        private final List<String> messages = new ArrayList<>();
        private final List<net.minecraft.network.protocol.common.custom.CustomPacketPayload> payloads = new ArrayList<>();

        private RecordingPlayer(ServerLevel level, String name) {
            super(level, new GameProfile(UUID.randomUUID(), name));
            // Capture outbound server outcomes only; this does not simulate a client or handshake.
            this.connection = new net.minecraft.server.network.ServerGamePacketListenerImpl(
                level.getServer(), connection.getConnection(), this,
                net.minecraft.server.network.CommonListenerCookie.createInitial(getGameProfile(), false)) {
                @Override
                public void send(net.minecraft.network.protocol.Packet<?> packet) {
                    if (packet instanceof net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket custom
                            && custom.payload().type().id().equals(Identifier.fromNamespaceAndPath("doggytalents", "payload_channel"))) {
                        payloads.add(custom.payload());
                    }
                }
            };
        }

        @Override
        public void sendSystemMessage(Component message, boolean actionBar) {
            messages.add(message.getString());
        }
    }
}
