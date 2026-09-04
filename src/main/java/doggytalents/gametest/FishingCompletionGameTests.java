package doggytalents.gametest;

import doggytalents.DoggyTalents;
import java.util.ArrayList;
import doggytalents.common.entity.Dog;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import static doggytalents.gametest.LiveGameplayGameTests.require;

public final class FishingCompletionGameTests {
    private FishingCompletionGameTests() {}

    /** COMPLETE-EXTENDED: branch-controlled random source, ordinary water/shake/loot/smelting dispatch. */
    public static void fishing(GameTestHelper h, boolean success, boolean treasure, boolean cook) {
        command(h, "random reset minecraft:gameplay/fishing/fish 0 true true");
        command(h, "random reset minecraft:gameplay/fishing/treasure 0 true true");
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dogs = new ArrayList<Dog>();
        // Several cookable loot draws exercise vanilla's fish table, including unsmeltable fish.
        for (int x : new int[]{3, 6, 9, 12}) {
            var dog = a.fishingDog(owner, x, 4, success ? 0 : 14, treasure ? 0 : 0.5F);
            dog.setOrderedToSit(true);
            dog.setTalentLevel(DoggyTalents.FISHER_DOG.get(), 5);
            if (cook) dog.setTalentLevel(DoggyTalents.HELL_HOUND.get(), 5);
            h.setBlock(new BlockPos(x, 1, 4), Blocks.WATER);
            dogs.add(dog);
        }
        boolean[] shaking = {false};
        h.onEachTick(() -> {
            if (dogs.stream().anyMatch(d -> d.getDogClassicalShakeAnim(0) > 1)) shaking[0] = true;
        });
        h.startSequence().thenIdle(10).thenExecute(() -> {
            for (var dog : dogs) {
                require(h, dog.isInWater(), "fish fixture not wet");
                h.setBlock(h.relativePos(dog.blockPosition()), Blocks.AIR);
                dog.setPos(dog.getX(), a.pos(1, 1).y, a.pos(1, 10).z);
            }
        }).thenIdle(150).thenExecute(() -> {
            require(h, shaking[0] && dogs.stream().noneMatch(Dog::isDogSoaked), "fish fixture did not complete a wet/dry cycle");
            var loot = h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds());
            if (!success) require(h, loot.isEmpty(), "failed fishing roll produced loot");
            else if (treasure) require(h, !loot.isEmpty() && loot.stream().noneMatch(e -> e.getItem().is(net.minecraft.tags.ItemTags.FISHES)),
                "treasure branch produced no treasure");
            else if (cook) {
                require(h, loot.stream().anyMatch(e -> e.getItem().is(Items.COOKED_COD) || e.getItem().is(Items.COOKED_SALMON)),
                    "Hell Hound did not cook fishing loot");
                require(h, loot.stream().noneMatch(e -> e.getItem().is(Items.COD) || e.getItem().is(Items.SALMON)),
                    "cookable loot remained raw");
            }
            loot.forEach(Entity::discard);
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-EXTENDED: actual rainfall wets the dog but does not satisfy water-block fishing. */
    public static void rain(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.fishingDog(owner, 6, 8, 0, 0.5F);
        dog.setOrderedToSit(true);
        dog.setTalentLevel(DoggyTalents.FISHER_DOG.get(), 5);
        h.setBiome(net.minecraft.world.level.biome.Biomes.PLAINS);
        // GameTest's enclosing barrier roof is motion-blocking even though canSeeSky returns true.
        for (int x = 0; x < 16; ++x) for (int z = 0; z < 16; ++z)
            h.setBlock(new BlockPos(x, 6, z), Blocks.AIR);
        command(h, "weather rain 6000");
        h.getLevel().setRainLevel(1);
        h.startSequence().thenWaitUntil(() -> require(h, dog.isDogSoaked() && !dog.isInWater(),
            "rain did not wet dog: sky=" + h.getLevel().canSeeSky(dog.blockPosition()) + ",raining=" + h.getLevel().isRaining()
                + ",precip=" + h.getLevel().precipitationAt(dog.blockPosition()) + ",pos=" + dog.blockPosition()
                + ",height=" + h.getLevel().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, dog.blockPosition())
                + ",biome=" + h.getLevel().getBiome(dog.blockPosition()).unwrapKey()))
            .thenExecute(() -> command(h, "weather clear 6000"))
            .thenIdle(200).thenExecute(() -> {
                require(h, !dog.isDogSoaked(), "rain-wet dog did not dry");
                require(h, h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds()).isEmpty(), "rain created fishing loot");
                a.close();
            }).thenSucceed();
    }

    private static void command(GameTestHelper h, String command) {
        try {
            h.getLevel().getServer().getCommands().getDispatcher().execute(command,
                h.getLevel().getServer().createCommandSourceStack());
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
