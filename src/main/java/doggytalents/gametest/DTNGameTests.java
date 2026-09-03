package doggytalents.gametest;

import doggytalents.DoggyAccessories;
import doggytalents.DoggyBlocks;
import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DogGender;
import doggytalents.api.feature.DogLevel;
import doggytalents.api.feature.DogMode;
import doggytalents.common.block.DogBedMaterialManager;
import doggytalents.common.block.tileentity.DogBedTileEntity;
import doggytalents.common.block.tileentity.FoodBowlTileEntity;
import doggytalents.common.block.tileentity.RiceMillBlockEntity;
import doggytalents.common.entity.Dog;
import doggytalents.common.talent.PackPuppyTalent;
import doggytalents.common.talent.doggy_tools.DoggyToolsTalent;
import doggytalents.common.util.ItemUtil;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class DTNGameTests {
    private static final UUID DOG_UUID = UUID.fromString("81b99c9b-ecfa-4c3c-8e6a-2f986814c731");
    private static final UUID OWNER_UUID = UUID.fromString("41f54ff5-d7c2-44b2-bf31-f43ce49800ca");
    private static final UUID BLOCK_ENTITY_DOG_UUID =
        UUID.fromString("296a1a85-f0ce-4d21-87ce-ccf2bfaac896");

    private DTNGameTests() {
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
