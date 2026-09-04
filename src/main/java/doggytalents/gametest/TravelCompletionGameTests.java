package doggytalents.gametest;

import static doggytalents.gametest.LiveGameplayGameTests.require;

import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DogLevel;
import doggytalents.common.talent.FlyingFurballTalent;
import doggytalents.common.talent.MobRetrieverTalent;
import doggytalents.common.util.ItemUtil;
import doggytalents.gametest.LiveGameplayGameTests.Arena;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;

public final class TravelCompletionGameTests {
    private TravelCompletionGameTests() { }

    /** COMPLETE-TRAVEL: normal retrieval navigation, return and sit-to-drop. */
    public static void retrieve(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(2, 8);
        var dog = a.dog(owner, 3, 8, true);
        var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
        cow.setPos(a.pos(13, 8));
        cow.setNoAi(true);
        a.spawn(cow);
        dog.setTalentLevel(DoggyTalents.MOB_RETRIEVER.get(), 4);
        var talent = dog.getTalent(DoggyTalents.MOB_RETRIEVER.get(), MobRetrieverTalent.class).orElseThrow();
        talent.setTarget(dog, cow);
        h.startSequence().thenIdle(30).thenExecute(() -> {
            require(h, !cow.isPassenger(), "level 4 carried oversized cow");
            dog.setTalentLevel(DoggyTalents.MOB_RETRIEVER.get(), 5);
            talent.setTarget(dog, cow);
        }).thenWaitUntil(() -> require(h, cow.getVehicle() == dog, "cow not picked up"))
        .thenWaitUntil(() -> require(h, dog.distanceToSqr(owner) <= 10, "retrieved cow not returned to owner"))
        .thenExecute(() -> dog.setOrderedToSit(true))
        .thenWaitUntil(() -> require(h, !cow.isPassenger(), "sit did not drop retrieved cow"))
        .thenExecute(() -> {
            require(h, cow.isAlive() && cow.distanceToSqr(owner) < 25, "retrieval lost target");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-TRAVEL: empty-hand double interaction mounts; hunger ejects the rider. */
    public static void mount(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.rider(8, 8);
        var dog = a.dog(owner, 7, 8, true);
        dog.setTalentLevel(DoggyTalents.WOLF_MOUNT.get(), 5);
        owner.tickCount = 100;
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, owner.getVehicle() == dog, "double interaction did not mount dog");
        h.startSequence().thenIdle(20).thenExecute(() -> {
            require(h, owner.getVehicle() == dog, "healthy mount ejected rider");
            dog.setDogHunger(0);
        }).thenIdle(2).thenExecute(() -> {
            require(h, !owner.isPassenger(), "exhausted mount did not eject rider");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-TRAVEL: flight uses normal ticks and restoring the option restores gravity/navigation. */
    public static void flying(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 5, 8, true);
        dog.setLevel(new DogLevel(60, 1));
        dog.setHealth(dog.getMaxHealth());
        dog.setTalentLevel(DoggyTalents.FLYING_FURBALL.get(), 5);
        double gravity = dog.getAttributeValue(Attributes.GRAVITY);
        dog.setPos(dog.position().add(0, 4, 0));
        h.startSequence().thenWaitUntil(() -> require(h, dog.isDogFlying(), "airborne dog did not start flying"))
        .thenExecute(() -> {
            require(h, dog.getAttributeValue(Attributes.GRAVITY) < gravity, "glide did not reduce gravity");
            dog.getTalent(DoggyTalents.FLYING_FURBALL.get(), FlyingFurballTalent.class).orElseThrow().setAllowFlying(false);
        }).thenWaitUntil(() -> require(h, dog.onGround() && !dog.isDogFlying(), "disabled flying did not land"))
        .thenExecute(() -> {
            require(h, dog.getAttributeValue(Attributes.GRAVITY) == gravity && !dog.isNoGravity(), "flight left gravity changed");
            require(h, dog.getHealth() == dog.getMaxHealth(), "Flying Furball fall immunity failed");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-SOCIAL: whistle mode causes a real animal to walk toward the owner. */
    public static void shepherd(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(2, 8);
        var dog = a.dog(owner, 8, 8, true);
        dog.setTalentLevel(DoggyTalents.SHEPHERD_DOG.get(), 1);
        var whistle = new ItemStack(DoggyItems.WHISTLE.get());
        var tag = new CompoundTag();
        tag.putInt("mode", 4);
        ItemUtil.putTag(whistle, tag);
        owner.setItemInHand(InteractionHand.MAIN_HAND, whistle);
        var sheep = EntityType.SHEEP.create(h.getLevel(), EntitySpawnReason.LOAD);
        sheep.setPos(a.pos(12, 8));
        a.spawn(sheep);
        h.startSequence().thenWaitUntil(() -> require(h, sheep.distanceToSqr(owner) < 25,
            "shepherd did not bring sheep to owner"))
        .thenExecute(() -> {
            require(h, sheep.position().distanceToSqr(a.pos(12, 8)) > 16, "sheep did not move materially");
            owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }).thenIdle(5).thenExecute(a::close).thenSucceed();
    }

    /** COMPLETE-TRAVEL: a fenced corridor forces following through a closed gate. */
    public static void gate(GameTestHelper h) {
        var a = new Arena(h);
        for (int x = 0; x < 16; ++x) {
            h.setBlock(new BlockPos(x, 1, 6), Blocks.STONE);
            h.setBlock(new BlockPos(x, 2, 6), Blocks.STONE);
            h.setBlock(new BlockPos(x, 1, 10), Blocks.STONE);
            h.setBlock(new BlockPos(x, 2, 10), Blocks.STONE);
        }
        for (int z = 7; z <= 9; ++z) {
            h.setBlock(new BlockPos(8, 1, z), Blocks.OAK_FENCE);
        }
        var gate = new BlockPos(8, 1, 8);
        h.setBlock(gate, Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.EAST));
        var owner = a.owner(14, 8);
        var dog = a.dog(owner, 3, 8, true);
        dog.setTalentLevel(DoggyTalents.GATE_PASSER.get(), 1);
        h.startSequence().thenWaitUntil(() -> require(h, h.getBlockState(gate).getValue(FenceGateBlock.OPEN),
            "dog did not open gate"))
        .thenWaitUntil(() -> require(h, dog.getX() > a.pos(9, 8).x, "dog did not cross gate"))
        .thenWaitUntil(() -> require(h, !h.getBlockState(gate).getValue(FenceGateBlock.OPEN), "dog left gate open"))
        .thenExecute(a::close).thenSucceed();
    }
}
