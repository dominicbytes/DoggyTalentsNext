package doggytalents.gametest;

import doggytalents.DoggyTalents;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import static doggytalents.gametest.LiveGameplayGameTests.require;

public final class WaterCompletionGameTests {
    private WaterCompletionGameTests() {}

    /** COMPLETE-TRAVEL: submerged dogs tick normally; max-level rider receives the real effect. */
    public static void submerged(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.rider(8, 8);
        var lower = a.dog(owner, 4, 8, false);
        var maximum = a.dog(owner, 9, 8, false);
        lower.setTalentLevel(DoggyTalents.SWIMMER_DOG.get(), 4);
        maximum.setTalentLevel(DoggyTalents.SWIMMER_DOG.get(), 5);
        maximum.setTalentLevel(DoggyTalents.WOLF_MOUNT.get(), 5);
        for (int x = 2; x <= 13; ++x) for (int z = 5; z <= 11; ++z)
            for (int y = 1; y <= 4; ++y) h.setBlock(new BlockPos(x, y, z), Blocks.WATER);
        lower.setAirSupply(0);
        maximum.setAirSupply(0);
        owner.tickCount = 100;
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        maximum.mobInteract(owner, InteractionHand.MAIN_HAND);
        maximum.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, owner.getVehicle() == maximum, "water fixture could not mount");
        // Server connection normally invokes doTick; this offline rider has no network driver.
        h.onEachTick(owner::doTick);
        h.startSequence().thenIdle(160).thenExecute(() -> {
            require(h, lower.isInWater() && maximum.isInWater(), "submersion fixture escaped water");
            require(h, lower.getHealth() < lower.getMaxHealth(), "level 4 unexpectedly breathed indefinitely");
            require(h, maximum.getHealth() == maximum.getMaxHealth(), "level 5 took drowning damage");
            require(h, owner.getVehicle() == maximum && owner.hasEffect(MobEffects.CONDUIT_POWER),
                "max Swimmer did not maintain rider water vision");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-TRAVEL: real follow navigation crosses a water trench and a raised step. */
    public static void terrainFollow(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(14, 8);
        var dog = a.dog(owner, 3, 8, true);
        dog.setTalentLevel(DoggyTalents.SWIMMER_DOG.get(), 5);
        for (int x = 1; x < 16; ++x) for (int y = 1; y <= 3; ++y) {
            h.setBlock(new BlockPos(x, y, 6), Blocks.STONE);
            h.setBlock(new BlockPos(x, y, 10), Blocks.STONE);
        }
        for (int x = 6; x <= 9; ++x) for (int z = 7; z <= 9; ++z) {
            h.setBlock(new BlockPos(x, -1, z), Blocks.STONE);
            h.setBlock(new BlockPos(x, 0, z), Blocks.WATER);
            h.setBlock(new BlockPos(x, 1, z), Blocks.WATER);
        }
        for (int z = 7; z <= 9; ++z) h.setBlock(new BlockPos(11, 1, z), Blocks.STONE);
        boolean[] water = {false};
        h.onEachTick(() -> { if (dog.isInWater()) water[0] = true; });
        h.startSequence().thenWaitUntil(() -> require(h, dog.distanceToSqr(owner) < 16,
            "dog did not navigate water/step corridor: pos=" + dog.position() + ",owner=" + owner.position()
                + ",water=" + dog.isInWater() + ",ground=" + dog.onGround() + ",nav=" + dog.getNavigation().getClass().getSimpleName()
                + ",path=" + dog.getNavigation().getPath() + ",following=" + dog.isDogFollowingSomeone()))
            .thenExecute(() -> {
                require(h, water[0] && dog.getHealth() == dog.getMaxHealth(), "follow bypassed water or took damage");
                a.close();
            }).thenSucceed();
    }
}
