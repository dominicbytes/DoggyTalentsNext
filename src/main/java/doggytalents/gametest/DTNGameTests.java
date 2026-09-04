package doggytalents.gametest;

import doggytalents.DoggyAccessories;
import doggytalents.DoggyBlocks;
import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DogGender;
import doggytalents.api.feature.DogLevel;
import doggytalents.api.feature.DogMode;
import doggytalents.api.feature.DogSize;
import doggytalents.api.inferface.DTNItemStackHandler;
import doggytalents.common.block.DogBedMaterialManager;
import doggytalents.common.block.tileentity.DogBedTileEntity;
import doggytalents.common.block.tileentity.FoodBowlTileEntity;
import doggytalents.common.block.tileentity.RiceMillBlockEntity;
import doggytalents.common.entity.BoostingFoodHandler;
import doggytalents.common.entity.Dog;
import doggytalents.common.entity.DogGroupsManager;
import doggytalents.common.entity.MeatFoodHandler;
import doggytalents.common.entity.stats.StatsTracker;
import doggytalents.common.entity.texture.DogSkinData;
import doggytalents.common.event.EventHandler;
import doggytalents.common.inventory.TreatBagItemHandler;
import doggytalents.common.item.TreatBagItem;
import doggytalents.common.item.WhistleItem;
import doggytalents.common.talent.PackPuppyTalent;
import doggytalents.common.talent.doggy_tools.DoggyToolsTalent;
import doggytalents.common.util.InventoryUtil;
import doggytalents.common.util.ItemUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class DTNGameTests {
    private static final UUID DOG_UUID = UUID.fromString("81b99c9b-ecfa-4c3c-8e6a-2f986814c731");
    private static final UUID OWNER_UUID = UUID.fromString("41f54ff5-d7c2-44b2-bf31-f43ce49800ca");
    private static final UUID BLOCK_ENTITY_DOG_UUID =
        UUID.fromString("296a1a85-f0ce-4d21-87ce-ccf2bfaac896");
    private static final String LEGACY_DOG_FIXTURE =
        "/data/doggytalents/gametest/fixtures/dog-1.21.1-oracle.snbt";
    private static final String LEGACY_DOG_FIXTURE_SHA256 =
        "da18b522d67f6fe1bee143ae833f3f4d7a14f7f3b521d44cfcab83741180d88b";

    private DTNGameTests() {
    }

    /** GAMEPLAY-FOOD-01: registered vanilla foods preserve hunger, effects, and stack rules. */
    public static void gameplayFood01Consumption(GameTestHelper helper) {
        Dog dog = createDog(helper);
        helper.getLevel().addFreshEntity(dog);
        dog.setDogHunger(20);

        var beef = new ItemStack(Items.COOKED_BEEF, 2);
        var meatHandler = new MeatFoodHandler();
        require(helper, meatHandler.canConsume(dog, beef, null),
            "cooked beef was not accepted as dog food");
        require(helper, meatHandler.consume(dog, beef, null).consumesAction(),
            "cooked beef consumption did not succeed");
        require(helper, Float.compare(dog.getDogHunger(), 60) == 0,
            "cooked beef did not add five times its nutrition to hunger");
        require(helper, beef.getCount() == 1, "cooked beef was not consumed exactly once");

        dog.setDogHunger(dog.getMaxHunger());
        var rejectedBeef = new ItemStack(Items.COOKED_BEEF);
        require(helper, !meatHandler.consume(dog, rejectedBeef, null).consumesAction(),
            "a full dog consumed ordinary meat");
        require(helper, rejectedBeef.getCount() == 1, "rejected meat was still consumed");

        dog.setDogHunger(20);
        require(helper, dog.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1)),
            "a normal dog rejected a directly applied beneficial effect");
        dog.removeEffect(MobEffects.REGENERATION);
        var goldenApple = new ItemStack(Items.GOLDEN_APPLE);
        require(helper, new BoostingFoodHandler().consume(dog, goldenApple, null).consumesAction(),
            "golden apple consumption did not succeed");
        require(helper, Float.compare(dog.getDogHunger(), 40) == 0,
            "golden apple nutrition was not converted to dog hunger");
        require(helper, goldenApple.isEmpty(), "golden apple was not consumed exactly once");
        require(helper, dog.hasEffect(MobEffects.REGENERATION),
            "golden apple regeneration effect was not applied");
        require(helper, dog.hasEffect(MobEffects.ABSORPTION),
            "golden apple absorption effect was not applied");
        helper.succeed();
    }

    /** GAMEPLAY-TRAINING-01: a training treat converts an owned wolf without losing identity. */
    public static void gameplayTraining01WolfConversion(GameTestHelper helper) {
        var level = helper.getLevel();
        var trainer = helper.makeMockPlayer(GameType.SURVIVAL);
        var treatStack = new ItemStack(DoggyItems.TRAINING_TREAT.get(), 2);
        trainer.setItemInHand(InteractionHand.MAIN_HAND, treatStack);

        Wolf wolf = EntityType.WOLF.create(level, EntitySpawnReason.BREEDING);
        require(helper, wolf != null, "vanilla wolf creation failed");
        var wolfPos = helper.absolutePos(BlockPos.ZERO);
        wolf.setPos(wolfPos.getX() + 0.5, wolfPos.getY(), wolfPos.getZ() + 0.5);
        wolf.tame(trainer);
        wolf.setCustomName(Component.literal("Training One"));
        var wolfId = wolf.getUUID();
        level.addFreshEntity(wolf);

        require(helper, wolf.isOwnedBy(trainer), "test wolf was not owned by the trainer");
        require(helper, trainer.getMainHandItem().is(DoggyItems.TRAINING_TREAT.get()),
            "trainer was not holding the training treat");
        EventHandler.checkAndTrainWolf(trainer, wolf, 35, 50);

        require(helper, treatStack.getCount() == 1, "training did not consume exactly one treat");
        require(helper, !wolf.isAlive(), "the converted vanilla wolf remained alive");
        var dogs = helper.getEntities(DoggyEntityTypes.DOG.get());
        require(helper, dogs.size() == 1, "training did not create exactly one DTN dog");
        var trainedDog = dogs.getFirst();
        require(helper, wolfId.equals(trainedDog.getUUID()), "training did not preserve the wolf UUID");
        require(helper, trainer.getUUID().equals(trainedDog.getOwnerUUID()),
            "training did not preserve ownership");
        require(helper, trainedDog.getCustomName() != null
            && "Training One".equals(trainedDog.getCustomName().getString()),
            "training did not preserve the wolf name");
        helper.succeed();
    }

    /** GAMEPLAY-WHISTLE-01: legacy custom data resolves valid modes and safely defaults corrupt values. */
    public static void gameplayWhistle01CustomDataCompatibility(GameTestHelper helper) {
        var whistle = DoggyItems.WHISTLE.get();
        var stack = new ItemStack(whistle);
        var standDescription = whistle.getDescriptionId() + "." + WhistleItem.WhistleMode.STAND.getIndex();

        require(helper, standDescription.equals(whistle.getDescriptionId(stack)),
            "a whistle without custom data did not default to stand mode");

        ItemUtil.modifyTag(stack, tag -> {
            tag.putByte("mode", (byte) WhistleItem.WhistleMode.DUTY_WHISTLE.getIndex());
            tag.putBoolean("dog_on_duty_only", true);
        });
        require(helper,
            (whistle.getDescriptionId() + "." + WhistleItem.WhistleMode.DUTY_WHISTLE.getIndex())
                .equals(whistle.getDescriptionId(stack)),
            "a valid legacy whistle mode was not preserved");
        require(helper, WhistleItem.isDogOnDutyOnly(stack),
            "the legacy duty-only setting was not preserved");

        ItemUtil.modifyTag(stack, tag -> tag.putByte("mode", (byte) -1));
        require(helper, standDescription.equals(whistle.getDescriptionId(stack)),
            "a negative legacy whistle mode did not safely default to stand mode");

        ItemUtil.modifyTag(stack, tag -> tag.putByte("mode", Byte.MAX_VALUE));
        require(helper, standDescription.equals(whistle.getDescriptionId(stack)),
            "an oversized legacy whistle mode did not safely default to stand mode");
        helper.succeed();
    }

    /** ITEM-DATA-01: legacy nested item data keeps compound type checks and clear semantics. */
    public static void itemData01CustomDataCompatibility(GameTestHelper helper) {
        var stack = new ItemStack(Items.STICK);
        var nested = new CompoundTag();
        nested.putString("name", "Rin");
        var root = new CompoundTag();
        root.put("doggytalents", nested);
        ItemUtil.putTag(stack, root);

        require(helper, ItemUtil.hasTag(stack), "custom data was not attached to the item");
        var decoded = ItemUtil.getTagElement(stack, "doggytalents");
        require(helper, decoded != null && "Rin".equals(decoded.getStringOr("name", "")),
            "nested custom data did not round-trip");

        root.putString("doggytalents", "not a compound");
        ItemUtil.putTag(stack, root);
        require(helper, ItemUtil.getTagElement(stack, "doggytalents") == null,
            "wrongly typed nested custom data was accepted as an empty compound");

        ItemUtil.clearTag(stack);
        require(helper, !ItemUtil.hasTag(stack), "custom data remained after clearing the item");
        require(helper, ItemUtil.getTag(stack).isEmpty(), "cleared custom data did not read as empty");
        helper.succeed();
    }

    /** ITEM-HANDLER-01: modern transactions retain the mod's established stack-oriented behavior. */
    public static void itemHandler01TransactionalStorage(GameTestHelper helper) {
        var handler = new DTNItemStackHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 4));

        try (var transaction = Transaction.openRoot()) {
            require(helper, handler.insert(0, ItemResource.of(Items.STONE), 5, transaction) == 5,
                "transactional insertion rejected a compatible stack");
        }
        require(helper, handler.getStackInSlot(0).getCount() == 4,
            "aborted transaction changed stored items");

        try (var transaction = Transaction.openRoot()) {
            require(helper, handler.insert(0, ItemResource.of(Items.STONE), 5, transaction) == 5,
                "committed insertion rejected a compatible stack");
            transaction.commit();
        }
        require(helper, handler.getStackInSlot(0).getCount() == 9,
            "committed transaction did not update stored items");
        handler.setStackInSlot(1, new ItemStack(Items.STONE, 90));
        require(helper, handler.extractItem(1, 80, false).getCount() == 64
            && handler.getStackInSlot(1).getCount() == 26,
            "stack-oriented extraction did not retain the legacy per-call item limit");

        var diamondsOnly = new DTNItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(Items.DIAMOND);
            }
        };
        var rejected = diamondsOnly.insertItem(0, new ItemStack(Items.STONE, 3), false);
        require(helper, rejected.getCount() == 3 && diamondsOnly.getStackInSlot(0).isEmpty(),
            "legacy insertion bypassed slot validation");
        require(helper, diamondsOnly.insertItem(0, new ItemStack(Items.DIAMOND, 3), false).isEmpty()
            && diamondsOnly.getStackInSlot(0).getCount() == 3,
            "legacy insertion did not commit an accepted stack");

        var bagStack = new ItemStack(DoggyItems.TREAT_BAG.get());
        var bagHandler = new TreatBagItemHandler(bagStack);
        require(helper, bagHandler.insertItem(0, new ItemStack(Items.COOKED_BEEF, 2), false).isEmpty(),
            "treat bag rejected valid food");
        requireStack(helper, TreatBagItem.inventory(bagStack).getFirst(), Items.COOKED_BEEF, 2,
            "treat bag committed contents");

        var mergeTarget = new DTNItemStackHandler(2);
        mergeTarget.setStackInSlot(1, new ItemStack(Items.STONE, 60));
        require(helper, InventoryUtil.addItem(mergeTarget, new ItemStack(Items.STONE, 6)).isEmpty(),
            "resource insertion returned an unexpected remainder");
        require(helper, mergeTarget.getStackInSlot(1).getCount() == 64
            && mergeTarget.getStackInSlot(0).getCount() == 2,
            "resource insertion did not preserve merge-before-empty-slot ordering");

        var refusingSource = new DTNItemStackHandler(1) {
            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
                return 0;
            }
        };
        refusingSource.setStackInSlot(0, new ItemStack(Items.DIAMOND, 2));
        var rollbackTarget = new DTNItemStackHandler(1);
        boolean rejectedTransfer = false;
        try {
            InventoryUtil.transferStacks(refusingSource, rollbackTarget);
        } catch (IllegalStateException expected) {
            rejectedTransfer = true;
        }
        require(helper, rejectedTransfer, "inconsistent source extraction was not rejected");
        require(helper, rollbackTarget.getStackInSlot(0).isEmpty()
            && refusingSource.getStackInSlot(0).getCount() == 2,
            "failed transfer did not roll back both inventories");
        helper.succeed();
    }

    /** SAVE-01: a real DTN dog retains identity and core state through 26.1 entity serialization. */
    public static void save01DogCoreRoundTrip(GameTestHelper helper) {
        var level = helper.getLevel();
        Dog source = createDog(helper);
        source.setUUID(DOG_UUID);
        source.setOwnerUUID(OWNER_UUID);
        source.setTame(true, true);
        source.setDogCustomName(Component.literal("Save One"));
        source.setGender(DogGender.FEMALE);
        source.setMode(DogMode.PATROL);
        source.setDogHunger(43.5F);
        source.setDogIncapValue(117);
        source.setLevel(new DogLevel(37, 9));
        source.setWillObeyOthers(true);
        source.setCanPlayersAttack(true);
        source.setRegardTeamPlayers(true);
        source.setForceSit(true);
        source.setDogAutoMount(true);
        source.setCrossOriginTp(true);
        source.setPatrolTargetLock(true);
        source.setHideArmor(true);
        source.setDogOnDuty(true);
        source.setBedPos(level.dimension(), new BlockPos(4, 2, 6));
        source.setBowlPos(level.dimension(), new BlockPos(7, 2, 3));

        Dog loaded = roundTrip(helper, source);

        require(helper, DOG_UUID.equals(loaded.getUUID()), "dog UUID was not preserved");
        require(helper, OWNER_UUID.equals(loaded.getOwnerUUID()), "owner UUID was not preserved");
        require(helper, loaded.isTame(), "tame state was not preserved");
        require(helper, loaded.getCustomName() != null
            && "Save One".equals(loaded.getCustomName().getString()), "custom name was not preserved");
        require(helper, loaded.getGender() == DogGender.FEMALE, "gender was not preserved");
        require(helper, loaded.getMode() == DogMode.PATROL, "mode was not preserved");
        require(helper, Float.compare(loaded.getDogHunger(), 43.5F) == 0, "hunger was not preserved");
        require(helper, loaded.getDogIncapValue() == 117, "incapacitated value was not preserved");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.NORMAL) == 37,
            "normal level was not preserved");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.KAMI) == 9,
            "kami level was not preserved");
        require(helper, loaded.willObeyOthers(), "will-obey setting was not preserved");
        require(helper, loaded.canOwnerAttack(), "friendly-fire setting was not preserved");
        require(helper, loaded.regardTeamPlayers(), "team-player setting was not preserved");
        require(helper, loaded.forceSit(), "force-sit setting was not preserved");
        require(helper, loaded.dogAutoMount(), "auto-mount setting was not preserved");
        require(helper, loaded.crossOriginTp(), "cross-origin teleport setting was not preserved");
        require(helper, loaded.patrolTargetLock(), "patrol-target lock was not preserved");
        require(helper, loaded.hideArmor(), "hide-armor setting was not preserved");
        require(helper, loaded.dogOnDuty(), "on-duty setting was not preserved");
        require(helper, loaded.getBedPos(level.dimension()).filter(new BlockPos(4, 2, 6)::equals).isPresent(),
            "bed location was not preserved");
        require(helper, loaded.getBowlPos(level.dimension()).filter(new BlockPos(7, 2, 3)::equals).isPresent(),
            "bowl location was not preserved");
        helper.succeed();
    }

    /** SAVE-01: nested talent, inventory, accessory, and artifact state survives serialization. */
    public static void save01DogExtendedRoundTrip(GameTestHelper helper) {
        Dog source = createDog(helper);
        require(helper, source.setTalentLevel(DoggyTalents.HAPPY_EATER.get(), 3).consumesAction(),
            "failed to add happy eater talent");
        require(helper, source.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 5).consumesAction(),
            "failed to add pack puppy talent");
        require(helper, source.setTalentLevel(DoggyTalents.DOGGY_TOOLS.get(), 4).consumesAction(),
            "failed to add doggy tools talent");
        require(helper, source.setTalentLevel(DoggyTalents.DOGGY_ARMOR.get(), 1).consumesAction(),
            "failed to add doggy armor talent");

        PackPuppyTalent packPuppy = source.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class)
            .orElseThrow();
        packPuppy.inventory().setStackInSlot(0, new ItemStack(Items.DIAMOND, 7));
        packPuppy.inventory().setStackInSlot(12, new ItemStack(Items.COOKED_BEEF, 3));
        packPuppy.setRenderChest(false);
        packPuppy.setPickupItems(true);
        packPuppy.setOfferFood(false);
        packPuppy.setCollectKillLoot(true);

        DoggyToolsTalent doggyTools = source.getTalent(DoggyTalents.DOGGY_TOOLS.get(), DoggyToolsTalent.class)
            .orElseThrow();
        ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
        pickaxe.setDamageValue(37);
        doggyTools.getTools().setStackInSlot(0, pickaxe);
        doggyTools.getTools().setStackInSlot(3, new ItemStack(Items.BOW));
        doggyTools.setPickFirstTool(true);
        source.dogArmors().setArmorInSlot(new ItemStack(Items.IRON_BOOTS));

        require(helper, source.addAccessory(DoggyAccessories.DYEABLE_COLLAR.get().create(0x2468ac)),
            "failed to add dyeable collar");
        require(helper, source.addArtifact(DoggyItems.FEATHERED_MANTLE.get()),
            "failed to add feathered mantle artifact");

        Dog loaded = roundTrip(helper, source);

        require(helper, loaded.getDogLevel(DoggyTalents.HAPPY_EATER.get()) == 3,
            "plain talent level was not preserved");
        require(helper, loaded.getDogLevel(DoggyTalents.PACK_PUPPY.get()) == 5,
            "pack puppy talent level was not preserved");
        require(helper, loaded.getDogLevel(DoggyTalents.DOGGY_TOOLS.get()) == 4,
            "doggy tools talent level was not preserved");
        PackPuppyTalent loadedPackPuppy = loaded
            .getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow();
        requireStack(helper, loadedPackPuppy.inventory().getStackInSlot(0), Items.DIAMOND, 7,
            "pack puppy slot 0");
        requireStack(helper, loadedPackPuppy.inventory().getStackInSlot(12), Items.COOKED_BEEF, 3,
            "pack puppy slot 12");
        require(helper, !loadedPackPuppy.renderChest(), "pack puppy render option was not preserved");
        require(helper, loadedPackPuppy.pickupItems(), "pack puppy pickup option was not preserved");
        require(helper, !loadedPackPuppy.offerFood(), "pack puppy food option was not preserved");
        require(helper, loadedPackPuppy.collectKillLoot(), "pack puppy loot option was not preserved");

        DoggyToolsTalent loadedDoggyTools = loaded
            .getTalent(DoggyTalents.DOGGY_TOOLS.get(), DoggyToolsTalent.class).orElseThrow();
        requireStack(helper, loadedDoggyTools.getTools().getStackInSlot(0), Items.DIAMOND_PICKAXE, 1,
            "doggy tools slot 0");
        require(helper, loadedDoggyTools.getTools().getStackInSlot(0).getDamageValue() == 37,
            "doggy tools item damage was not preserved");
        requireStack(helper, loadedDoggyTools.getTools().getStackInSlot(3), Items.BOW, 1,
            "doggy tools slot 3");
        require(helper, loadedDoggyTools.pickFirstTool(), "doggy tools pick-first option was not preserved");
        requireStack(helper, loaded.dogArmors().getArmorFromSlot(net.minecraft.world.entity.EquipmentSlot.FEET),
            Items.IRON_BOOTS, 1, "dog armor feet slot");

        require(helper, loaded.getAccessories().size() == 1, "accessory count was not preserved");
        require(helper, loaded.getAccessory(DoggyAccessories.DYEABLE_COLLAR.get()).isPresent(),
            "dyeable collar was not preserved");
        ItemStack returnedCollar = loaded.getAccessories().getFirst().getReturnItem();
        require(helper, (ItemUtil.getDyeColorForStack(returnedCollar) & 0xffffff) == 0x2468ac,
            "dyeable collar color was not preserved");
        require(helper, loaded.getArtifactsList().size() == 1
            && loaded.getArtifactsList().getFirst() == DoggyItems.FEATHERED_MANTLE.get(),
            "feathered mantle artifact was not preserved");
        helper.succeed();
    }

    /** SAVE-01: stateful DTN block entities retain their established data through 26.1 serialization. */
    public static void save01BlockEntityRoundTrip(GameTestHelper helper) {
        var level = helper.getLevel();
        Dog dog = createDog(helper);
        dog.setUUID(BLOCK_ENTITY_DOG_UUID);
        dog.setDogCustomName(Component.literal("Block Entity Dog"));

        BlockPos bowlPos = new BlockPos(1, 1, 1);
        helper.setBlock(bowlPos, DoggyBlocks.FOOD_BOWL.get());
        FoodBowlTileEntity bowl = helper.getBlockEntity(bowlPos, FoodBowlTileEntity.class);
        bowl.setPlacer(dog);
        bowl.getInventory().setStackInSlot(0, new ItemStack(Items.COOKED_BEEF, 11));
        bowl.getInventory().setStackInSlot(4, new ItemStack(Items.COOKED_CHICKEN, 2));

        FoodBowlTileEntity loadedBowl = roundTripBlockEntity(helper, bowl, FoodBowlTileEntity.class);
        require(helper, BLOCK_ENTITY_DOG_UUID.equals(loadedBowl.getPlacerId()),
            "food bowl placer UUID was not preserved");
        requireStack(helper, loadedBowl.getInventory().getStackInSlot(0), Items.COOKED_BEEF, 11,
            "food bowl slot 0");
        requireStack(helper, loadedBowl.getInventory().getStackInSlot(4), Items.COOKED_CHICKEN, 2,
            "food bowl slot 4");

        Identifier casingId = Identifier.withDefaultNamespace("oak_planks");
        Identifier beddingId = Identifier.withDefaultNamespace("red_wool");
        BlockPos bedPos = new BlockPos(3, 1, 1);
        helper.setBlock(bedPos, DoggyBlocks.DOG_BED.get());
        DogBedTileEntity bed = helper.getBlockEntity(bedPos, DogBedTileEntity.class);
        bed.setCasing(DogBedMaterialManager.getCasing(casingId));
        bed.setBedding(DogBedMaterialManager.getBedding(beddingId));
        bed.setOwner(dog);
        bed.setBedName(Component.literal("Porch Bed"));

        DogBedTileEntity loadedBed = roundTripBlockEntity(helper, bed, DogBedTileEntity.class);
        loadedBed.setLevel(level);
        require(helper, casingId.equals(DogBedMaterialManager.getKey(loadedBed.getCasing())),
            "dog bed casing ID was not preserved");
        require(helper, beddingId.equals(DogBedMaterialManager.getKey(loadedBed.getBedding())),
            "dog bed bedding ID was not preserved");
        require(helper, BLOCK_ENTITY_DOG_UUID.equals(loadedBed.getOwnerUUID()),
            "dog bed owner UUID was not preserved");
        require(helper, loadedBed.getBedName() != null
            && "Porch Bed".equals(loadedBed.getBedName().getString()), "dog bed name was not preserved");
        require(helper, loadedBed.getOwnerName() != null
            && "Block Entity Dog".equals(loadedBed.getOwnerName().getString()),
            "dog bed cached owner name was not preserved");

        BlockPos millPos = new BlockPos(5, 1, 1);
        helper.setBlock(millPos, DoggyBlocks.RICE_MILL.get());
        RiceMillBlockEntity mill = helper.getBlockEntity(millPos, RiceMillBlockEntity.class);
        mill.getWorldlyContainer().setItem(0, new ItemStack(DoggyItems.RICE_GRAINS.get(), 9));
        mill.getWorldlyContainer().setItem(1, new ItemStack(Items.BOWL, 3));
        mill.getWorldlyContainer().setItem(2, new ItemStack(DoggyItems.UNCOOKED_RICE_BOWL.get(), 2));

        RiceMillBlockEntity loadedMill = roundTripBlockEntity(helper, mill, RiceMillBlockEntity.class);
        requireStack(helper, loadedMill.getWorldlyContainer().getItem(0), DoggyItems.RICE_GRAINS.get(), 9,
            "rice mill input slot");
        requireStack(helper, loadedMill.getWorldlyContainer().getItem(1), Items.BOWL, 3,
            "rice mill bowl slot");
        requireStack(helper, loadedMill.getWorldlyContainer().getItem(2),
            DoggyItems.UNCOOKED_RICE_BOWL.get(), 2, "rice mill output slot");
        helper.succeed();
    }

    /** SAVE-01: an in-progress rice-mill operation retains its progress through 26.1 serialization. */
    public static void save01RiceMillProgressRoundTrip(GameTestHelper helper) {
        BlockPos millPos = new BlockPos(5, 1, 1);
        helper.setBlock(millPos, DoggyBlocks.RICE_MILL.get());
        RiceMillBlockEntity mill = helper.getBlockEntity(millPos, RiceMillBlockEntity.class);
        mill.getWorldlyContainer().setItem(0, new ItemStack(DoggyItems.RICE_GRAINS.get(), 9));
        mill.getWorldlyContainer().setItem(1, new ItemStack(Items.BOWL, 3));

        var level = helper.getLevel();
        var fixture = mill.saveWithFullMetadata(level.registryAccess());
        fixture.putInt("grindingTime", 23);
        BlockEntity loadedFixture = BlockEntity.loadStatic(
            millPos, mill.getBlockState(), fixture, level.registryAccess());
        require(helper, loadedFixture instanceof RiceMillBlockEntity,
            "rice mill progress fixture could not be reconstructed");
        RiceMillBlockEntity loadedMill = (RiceMillBlockEntity) loadedFixture;
        int loadedProgress = new RiceMillBlockEntity.RiceMillSyncedData(loadedMill)
            .get(RiceMillBlockEntity.GRINDING_TIME_ID);
        require(helper, loadedProgress == 23, "rice mill grinding progress fixture was not read");

        RiceMillBlockEntity roundTrippedMill = roundTripBlockEntity(helper, loadedMill, RiceMillBlockEntity.class);
        int roundTrippedProgress = new RiceMillBlockEntity.RiceMillSyncedData(roundTrippedMill)
            .get(RiceMillBlockEntity.GRINDING_TIME_ID);
        require(helper, roundTrippedProgress == 23, "rice mill grinding progress was not preserved");
        helper.succeed();
    }

    /** SAVE-01: dog statistics and per-entity kill counts survive serialization. */
    public static void save01StatsTrackerRoundTrip(GameTestHelper helper) {
        Dog source = createDog(helper);
        var stats = source.getStatTracker();
        require(helper, stats.getTotalKillCount() == 0, "new stats tracker had kills");

        var zombie = EntityType.ZOMBIE.create(helper.getLevel(), EntitySpawnReason.LOAD);
        var skeleton = EntityType.SKELETON.create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, zombie != null && skeleton != null, "test kill entities could not be created");
        stats.incrementKillCount(zombie);
        stats.incrementKillCount(zombie);
        stats.incrementKillCount(skeleton);
        stats.increaseDamageDealt(12.75F);
        stats.increaseDistanceOnWater(11);
        stats.increaseDistanceInWater(22);
        stats.increaseDistanceSprint(33);
        stats.increaseDistanceSneaking(44);
        stats.increaseDistanceWalk(55);
        stats.increaseDistanceRidden(66);
        require(helper, stats.getTotalKillCount() == 3, "cached total kill count did not refresh");

        var loadedStats = roundTrip(helper, source).getStatTracker();
        require(helper, loadedStats.getKillCountFor(EntityType.ZOMBIE) == 2,
            "zombie kill count was not preserved");
        require(helper, loadedStats.getKillCountFor(EntityType.SKELETON) == 1,
            "skeleton kill count was not preserved");
        require(helper, loadedStats.getTotalKillCount() == 3, "total kill count was not preserved");
        require(helper, Float.compare(loadedStats.getDamageDealt(), 12.75F) == 0,
            "damage dealt was not preserved");
        require(helper, loadedStats.getDistanceOnWater() == 11, "distance on water was not preserved");
        require(helper, loadedStats.getDistanceInWater() == 22, "distance in water was not preserved");
        require(helper, loadedStats.getDistanceSprint() == 33, "sprinting distance was not preserved");
        require(helper, loadedStats.getDistanceSneaking() == 44, "sneaking distance was not preserved");
        require(helper, loadedStats.getDistanceWalk() == 55, "walking distance was not preserved");
        require(helper, loadedStats.getDistanceRidden() == 66, "ridden distance was not preserved");

        var creeper = EntityType.CREEPER.create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, creeper != null, "test stale kill entity could not be created");
        var replacementStats = new StatsTracker();
        replacementStats.incrementKillCount(creeper);
        require(helper, replacementStats.getTotalKillCount() == 1, "replacement tracker setup failed");
        var tag = new CompoundTag();
        loadedStats.writeAdditional(tag);
        replacementStats.readAdditional(tag);
        require(helper, replacementStats.getKillCountFor(EntityType.CREEPER) == 0,
            "NBT load retained a stale kill count");
        require(helper, replacementStats.getTotalKillCount() == 3,
            "NBT load did not refresh total kill count");
        replacementStats.clearAllStatsKill();
        require(helper, replacementStats.getTotalKillCount() == 0,
            "clearing kills did not refresh total kill count");

        var invalidEntry = new CompoundTag();
        invalidEntry.putString("type", "missing:entity");
        invalidEntry.putInt("count", 99);
        var invalidKills = new ListTag();
        invalidKills.add(invalidEntry);
        var invalidTag = new CompoundTag();
        invalidTag.put("entityKills", invalidKills);
        replacementStats.readAdditional(invalidTag);
        require(helper, replacementStats.getAllKillCount().isEmpty(),
            "unknown entity type created an invalid kill entry");
        helper.succeed();
    }

    /** SAVE-01: a frozen payload matching the 1.21.1 persistence contract loads in 26.1. */
    public static void save01LegacyDogFixtureUpgrade(GameTestHelper helper) {
        CompoundTag fixture = loadLegacyDogFixture(helper);
        Dog loaded = createDog(helper);
        loaded.readDTNAdditionalSavedData(fixture);
        var level = helper.getLevel();

        require(helper, loaded.getGender() == DogGender.FEMALE, "legacy gender was not loaded");
        require(helper, loaded.getMode() == DogMode.PATROL, "legacy mode was not loaded");
        require(helper, Float.compare(loaded.getDogHunger(), 43.5F) == 0, "legacy hunger was not loaded");
        require(helper, loaded.getDogIncapValue() == 117, "legacy incapacitated value was not loaded");
        require(helper, loaded.getOwnersName().filter(name -> "Legacy Owner".equals(name.getString())).isPresent(),
            "legacy owner name was not loaded");
        require(helper, loaded.getSkinData().getVersion() == DogSkinData.Version.VERSION_0
            && "legacy-skin-hash".equals(loaded.getSkinData().getHash()),
            "legacy custom-skin hash was not upgraded");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.NORMAL) == 37,
            "legacy normal level was not loaded");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.KAMI) == 9,
            "legacy dire level was not upgraded to kami level");
        require(helper, loaded.getDogSize() == DogSize.FORTE, "legacy dog size was not loaded");
        require(helper, loaded.getLowHealthStrategy() == Dog.LowHealthStrategy.STICK_TO_OWNER,
            "legacy low-health strategy was not loaded");
        require(helper, loaded.getCombatReturnStrategy() == Dog.CombatReturnStrategy.FAR,
            "legacy combat-return strategy was not loaded");
        require(helper, loaded.willObeyOthers() && loaded.canOwnerAttack() && loaded.regardTeamPlayers()
            && loaded.forceSit() && loaded.dogAutoMount() && loaded.crossOriginTp()
            && loaded.patrolTargetLock() && loaded.hideArmor() && loaded.dogOnDuty(),
            "one or more legacy behavior flags were not loaded");
        require(helper, loaded.getDogLevel(DoggyTalents.HAPPY_EATER.get()) == 3,
            "legacy talent was not loaded");
        require(helper, loaded.getAccessory(DoggyAccessories.DYEABLE_COLLAR.get()).isPresent(),
            "legacy accessory was not loaded");
        ItemStack collar = loaded.getAccessories().getFirst().getReturnItem();
        require(helper, (ItemUtil.getDyeColorForStack(collar) & 0xffffff) == 0x2468ac,
            "legacy accessory color was not loaded");
        require(helper, loaded.getArtifactsList().size() == 1
            && loaded.getArtifactsList().getFirst() == DoggyItems.FEATHERED_MANTLE.get(),
            "legacy artifact was not loaded");
        require(helper, loaded.getBedPos(level.dimension()).filter(new BlockPos(4, 2, 6)::equals).isPresent(),
            "legacy bed location was not loaded");
        require(helper, loaded.getBowlPos(level.dimension()).filter(new BlockPos(7, 2, 3)::equals).isPresent(),
            "legacy bowl location was not loaded");

        var stats = loaded.getStatTracker();
        require(helper, stats.getKillCountFor(EntityType.ZOMBIE) == 2
            && stats.getKillCountFor(EntityType.SKELETON) == 1 && stats.getTotalKillCount() == 3,
            "legacy entity kills were not loaded");
        require(helper, Float.compare(stats.getDamageDealt(), 12.75F) == 0
            && stats.getDistanceOnWater() == 11 && stats.getDistanceInWater() == 22
            && stats.getDistanceSprint() == 33 && stats.getDistanceSneaking() == 44
            && stats.getDistanceWalk() == 55 && stats.getDistanceRidden() == 66,
            "legacy aggregate statistics were not loaded");
        require(helper, loaded.getGroups().getGroupsReadOnly().contains(
            new DogGroupsManager.DogGroup("Legacy Pack", 0x2468ac)), "legacy dog group was not loaded");
        require(helper, loaded.hasHome() && new BlockPos(12, 3, -8).equals(loaded.getHomePosition())
            && loaded.getHomeRadius() == 19, "legacy wander center was not loaded");

        var canonical = new CompoundTag();
        loaded.addDTNAdditionalSavedData(canonical);
        require(helper, canonical.getIntOr("level_kami", 0) == 9 && !canonical.contains("level_dire"),
            "legacy dire level was not rewritten canonically");
        require(helper, canonical.contains("doggytalents_dog_skin") && !canonical.contains("customSkinHash"),
            "legacy custom skin was not rewritten canonically");
        require(helper, canonical.getCompoundOrEmpty("ownerDistanceManager")
            .getLongOr("lastWithOwnerTime", 0L) == 24000L,
            "legacy owner-distance state was not preserved");
        require(helper, canonical.getCompoundOrEmpty("dogPettingManager")
            .getLongOr("dog_last_pet_time", 0L) == 12000L,
            "legacy petting state was not preserved");
        helper.succeed();
    }

    private static CompoundTag loadLegacyDogFixture(GameTestHelper helper) {
        try (var stream = DTNGameTests.class.getResourceAsStream(LEGACY_DOG_FIXTURE)) {
            require(helper, stream != null, "legacy dog fixture resource was not packaged");
            byte[] bytes = stream.readAllBytes();
            String actualHash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            require(helper, LEGACY_DOG_FIXTURE_SHA256.equals(actualHash),
                "legacy dog fixture changed without an explicit compatibility review");
            return TagParser.parseCompoundFully(new String(bytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load frozen 1.21.1 dog fixture", e);
        }
    }

    private static Dog roundTrip(GameTestHelper helper, Dog source) {
        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        source.saveWithoutId(output);
        Dog loaded = createDog(helper);
        loaded.load(TagValueInput.create(
            ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult()));
        return loaded;
    }

    private static <T extends BlockEntity> T roundTripBlockEntity(
            GameTestHelper helper, T source, Class<T> type) {
        var level = helper.getLevel();
        var tag = source.saveWithFullMetadata(level.registryAccess());
        BlockEntity loaded = BlockEntity.loadStatic(
            source.getBlockPos(), source.getBlockState(), tag, level.registryAccess());
        require(helper, type.isInstance(loaded), type.getSimpleName() + " could not be reconstructed");
        return type.cast(loaded);
    }

    private static void requireStack(GameTestHelper helper, ItemStack stack, Item item, int count, String description) {
        require(helper, stack.is(item), description + " item was not preserved");
        require(helper, stack.getCount() == count, description + " count was not preserved");
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
