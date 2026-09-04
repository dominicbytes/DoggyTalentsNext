package doggytalents.gametest;

import doggytalents.DoggyAccessories;
import doggytalents.DoggyBlocks;
import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.api.DoggyTalentsAPI;
import doggytalents.api.feature.DogMode;
import doggytalents.api.registry.TalentInstance;
import doggytalents.common.entity.Dog;
import doggytalents.common.item.CanineTrackerItem;
import doggytalents.common.storage.DogRespawnStorage;
import doggytalents.common.talent.BedDogTalent;
import doggytalents.common.talent.BlackPeltTalent;
import doggytalents.common.talent.PoisonFangTalent;
import doggytalents.common.talent.QuickHealerTalent;
import doggytalents.common.talent.ShockAbsorberTalent;
import doggytalents.common.talent.SwimmerDogTalent;
import doggytalents.common.talent.WolfMountTalent;
import doggytalents.common.util.ItemUtil;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class GameplayParityGameTests {
    private static final int EXPECTED_TALENT_COUNT = 33;

    private GameplayParityGameTests() {
    }

    /** GAME-01-TALENT-CATALOG: every registered talent constructs and round-trips. */
    public static void talentCatalog(GameTestHelper helper) {
        Dog dog = createDog(helper);
        var registry = DoggyTalentsAPI.TALENTS.get();
        require(helper, registry.size() == EXPECTED_TALENT_COUNT,
            "expected " + EXPECTED_TALENT_COUNT + " talents, found " + registry.size());

        var ids = new HashSet<String>();
        for (var talent : registry) {
            var id = registry.getKey(talent);
            require(helper, id != null, "registered talent had no identifier");
            require(helper, ids.add(id.toString()), "duplicate talent identifier: " + id);

            int level = Math.min(3, talent.getMaxLevel());
            TalentInstance source = talent.getDefault(level);
            var tag = new CompoundTag();
            source.writeInstance(dog, tag);
            var loaded = TalentInstance.readInstance(dog, tag);
            require(helper, loaded.isPresent(), "talent did not deserialize: " + id);
            require(helper, loaded.get().getTalent() == talent, "talent identity changed: " + id);
            require(helper, loaded.get().level() == level, "talent level changed: " + id);
        }
        helper.succeed();
    }

    /** GAME-01-COMBAT: offensive and defensive talents retain their level-five effects. */
    public static void combatTalents(GameTestHelper helper) {
        Dog dog = createDog(helper);
        var blackPelt = new BlackPeltTalent(DoggyTalents.BLACK_PELT.get(), 5);
        var damage = blackPelt.createPeltModifier(dog,
            net.minecraft.resources.Identifier.fromNamespaceAndPath("doggytalents", "gametest_damage"));
        require(helper, damage != null && Double.compare(damage.amount(), 7D) == 0,
            "black pelt did not provide its level-five damage bonus");

        var shockAbsorber = new ShockAbsorberTalent(DoggyTalents.SHOCK_ABSORBER.get(), 5);
        require(helper, Double.compare(shockAbsorber.getKnockbackResist(), 1D) == 0,
            "shock absorber did not provide full knockback resistance");
        require(helper, Float.compare(shockAbsorber.getExplosionResist(), 1F) == 0,
            "shock absorber did not provide full explosion resistance");
        require(helper, shockAbsorber.negateExplosion(dog).consumesAction(),
            "max shock absorber did not negate an explosion");

        var target = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, target != null, "living target entity type returned null");
        helper.getLevel().addFreshEntity(target);
        new PoisonFangTalent(DoggyTalents.POISON_FANG.get(), 5)
            .doInitialAttackEffects(dog, target);
        require(helper, target.hasEffect(MobEffects.POISON),
            "poison fang did not poison an attacked living target");
        helper.succeed();
    }

    /** GAME-01-MOVEMENT-MOUNT: riding and water-riding interactions work server-side. */
    public static void movementAndMount(GameTestHelper helper) {
        Dog dog = createDog(helper);
        var rider = helper.makeMockPlayer(GameType.SURVIVAL);
        dog.tame(rider);
        helper.getLevel().addFreshEntity(dog);
        require(helper, dog.setTalentLevel(DoggyTalents.WOLF_MOUNT.get(), 5).consumesAction(),
            "wolf mount could not be trained");
        var mount = dog.getTalent(DoggyTalents.WOLF_MOUNT.get(), WolfMountTalent.class)
            .orElseThrow();

        rider.tickCount = 10;
        require(helper, mount.processInteract(dog, helper.getLevel(), rider, InteractionHand.MAIN_HAND)
            == InteractionResult.PASS, "the first mount click was not armed");
        require(helper, mount.processInteract(dog, helper.getLevel(), rider, InteractionHand.MAIN_HAND)
            .consumesAction(), "the second mount click was not accepted");
        require(helper, rider.getVehicle() == dog && dog.hasPassenger(rider),
            "the owner did not mount the trained dog");

        var swimmer = new SwimmerDogTalent(DoggyTalents.SWIMMER_DOG.get(), 2);
        require(helper, swimmer.canBeRiddenInWater(dog).consumesAction(),
            "level-two swimmer dog could not be ridden in water");
        rider.stopRiding();
        helper.succeed();
    }

    /** GAME-01-CARE: healing and dog-bed cooldown/hunger rules remain functional. */
    public static void careTalents(GameTestHelper helper) {
        Dog dog = createDog(helper);
        dog.setInSittingPose(true);
        var quickHealer = new QuickHealerTalent(DoggyTalents.QUICK_HEALER.get(), 5);
        var healing = quickHealer.healingTick(dog, 2);
        require(helper, healing.getResult().consumesAction() && healing.getObject() == 20,
            "seated max-level quick healer did not multiply healing by ten");

        dog.setDogHunger(100);
        var bedDog = new BedDogTalent(DoggyTalents.BED_DOG.get(), 1);
        require(helper, BedDogTalent.isSleepCondition(dog, bedDog).ok(),
            "eligible bed dog was rejected before sleeping");
        bedDog.onSuccessfulSleep(dog);
        require(helper, Float.compare(dog.getDogHunger(), 60F) == 0,
            "bed dog did not consume the level-one hunger cost");
        require(helper, bedDog.isOnCooldown(dog) && bedDog.getCooldownDaysLeft(dog) == 5,
            "bed dog did not enter its five-day cooldown");
        require(helper, !BedDogTalent.isSleepCondition(dog, bedDog).ok(),
            "bed dog could immediately be used again");
        helper.succeed();
    }

    /** GAME-01-COMMANDS: operator locate and revive command trees are registered. */
    public static void commandTree(GameTestHelper helper) {
        var root = helper.getLevel().getServer().getCommands().getDispatcher().getRoot();
        var dog = root.getChild("dog");
        require(helper, dog != null, "the /dog command was not registered");
        var locate = dog.getChild("locate");
        var revive = dog.getChild("revive");
        require(helper, locate != null && locate.getChild("byuuid") != null
            && locate.getChild("byname") != null, "the /dog locate command tree is incomplete");
        require(helper, revive != null && revive.getChild("byuuid") != null
            && revive.getChild("byname") != null, "the /dog revive command tree is incomplete");
        helper.succeed();
    }

    /** GAME-01-INCAP-RESPAWN: incapacitation bounds and stored-dog revival work. */
    public static void incapacitationAndRespawn(GameTestHelper helper) {
        Dog source = createDog(helper);
        var owner = helper.makeMockPlayer(GameType.SURVIVAL);
        source.tame(owner);
        source.setDogCustomName(Component.literal("Parity Pup"));
        source.setMode(DogMode.INJURED);
        source.setDogIncapValue(Integer.MAX_VALUE);
        require(helper, source.isDefeated(), "injured dog was not marked defeated");
        require(helper, source.getDogIncapValue() == source.getMaxDogIncapVal(),
            "incapacitation value was not clamped to its maximum");
        require(helper, !source.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20)),
            "incapacitated dog accepted a potion effect");

        source.setMode(DogMode.DOCILE);
        source.setDogIncapValue(-1);
        require(helper, source.getDogIncapValue() == 0,
            "incapacitation value was not clamped to zero");
        var storage = DogRespawnStorage.get(helper.getLevel());
        var sourceId = source.getUUID();
        var stored = storage.putData(source);
        require(helper, stored != null, "dog could not be stored for respawn");
        Dog revived = stored.respawn(helper.getLevel(), owner, helper.absolutePos(BlockPos.ZERO));
        storage.remove(sourceId);
        require(helper, revived != null && revived.isAlive() && !revived.isDefeated(),
            "stored dog did not revive into a healthy state");
        require(helper, owner.getUUID().equals(revived.getOwnerUUID()),
            "revived dog lost its persisted owner UUID");
        require(helper, "Parity Pup".equals(revived.getName().getString()),
            "revived dog lost its name");
        helper.succeed();
    }

    /** GAME-01-INTERACTIONS: accessories, tracker data, and dog bath use are functional. */
    public static void accessoriesTrackerAndBath(GameTestHelper helper) {
        Dog dog = createDog(helper);
        require(helper, dog.addAccessory(DoggyAccessories.DYEABLE_COLLAR.get().create(0x224466)),
            "first collar accessory was rejected");
        require(helper, !dog.addAccessory(DoggyAccessories.DYEABLE_COLLAR.get().create(0x6688aa)),
            "a second collar bypassed the accessory type limit");
        require(helper, dog.removeAccessories().size() == 1 && dog.getAccessories().isEmpty(),
            "accessory removal did not return and clear the equipped collar");

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var tracker = new ItemStack(DoggyItems.CANINE_TRACKER.get());
        var trackerData = new CompoundTag();
        trackerData.putString("name", "Tracked Pup");
        trackerData.putInt("locateColor", 0xff336699);
        ItemUtil.putTag(tracker, trackerData);
        player.setItemInHand(InteractionHand.MAIN_HAND, tracker);
        require(helper, ItemUtil.hasTag(tracker), "tracker test data was not attached");
        require(helper, DoggyItems.CANINE_TRACKER.get() instanceof CanineTrackerItem,
            "registered canine tracker has the wrong item type");
        DoggyItems.CANINE_TRACKER.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        require(helper, !ItemUtil.hasTag(tracker), "server-side tracker use did not clear its target");

        var bathPos = helper.absolutePos(new BlockPos(1, 1, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));
        var hit = new BlockHitResult(Vec3.atCenterOf(bathPos), Direction.UP, bathPos, false);
        var bathResult = DoggyBlocks.DOG_BATH.get().useItemOn(player.getMainHandItem(),
            DoggyBlocks.DOG_BATH.get().defaultBlockState(), helper.getLevel(), bathPos, player,
            InteractionHand.MAIN_HAND, hit);
        require(helper, bathResult.consumesAction(), "dog bath rejected a glass bottle");
        require(helper, player.getMainHandItem().is(Items.POTION),
            "dog bath did not fill the glass bottle with water");
        helper.succeed();
    }

    private static Dog createDog(GameTestHelper helper) {
        Dog dog = DoggyEntityTypes.DOG.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, dog != null, "dog entity type returned null");
        return dog;
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            helper.fail(message);
        }
    }
}
