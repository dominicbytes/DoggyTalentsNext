package doggytalents.common.data;

import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import doggytalents.common.entity.Dog;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public final class DTLootModifierGameTests {
    private static final int ATTEMPTS = 512;

    private DTLootModifierGameTests() {
    }

    /** LOOT-01: registered global modifiers produce both progression ingredients. */
    public static void riceAndSoyDrops(GameTestHelper helper) {
        require(helper, producesRice(helper),
            "short grass never produced rice through the registered global loot modifier");
        require(helper, producesSoy(helper),
            "a dog-killed zombie never produced soy through the registered global loot modifier");
        helper.succeed();
    }

    private static boolean producesRice(GameTestHelper helper) {
        var level = helper.getLevel();
        var params = new LootParams.Builder(level)
            .withParameter(LootContextParams.BLOCK_STATE, Blocks.SHORT_GRASS.defaultBlockState())
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO)))
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
            .create(LootContextParamSets.BLOCK);
        var context = new LootContext.Builder(params)
            .withOptionalRandomSeed(0xD06L)
            .create(Optional.empty());
        var lootTable = Blocks.SHORT_GRASS.getLootTable().orElseThrow().identifier();

        for (int i = 0; i < ATTEMPTS; ++i) {
            var drops = CommonHooks.modifyLoot(lootTable, new ObjectArrayList<>(), context);
            if (drops.stream().anyMatch(stack -> stack.is(DoggyItems.RICE_GRAINS.get()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean producesSoy(GameTestHelper helper) {
        var level = helper.getLevel();
        Dog dog = DoggyEntityTypes.DOG.get().create(level, EntitySpawnReason.LOAD);
        var zombie = EntityType.ZOMBIE.create(level, EntitySpawnReason.LOAD);
        require(helper, dog != null && zombie != null, "loot test entities could not be created");
        var params = new LootParams.Builder(level)
            .withParameter(LootContextParams.THIS_ENTITY, zombie)
            .withParameter(LootContextParams.ORIGIN, zombie.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().mobAttack(dog))
            .withParameter(LootContextParams.ATTACKING_ENTITY, dog)
            .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, dog)
            .create(LootContextParamSets.ENTITY);
        var context = new LootContext.Builder(params)
            .withOptionalRandomSeed(0x501L)
            .create(Optional.empty());
        var lootTable = EntityType.ZOMBIE.getDefaultLootTable().orElseThrow().identifier();

        for (int i = 0; i < ATTEMPTS; ++i) {
            var drops = CommonHooks.modifyLoot(lootTable, new ObjectArrayList<>(), context);
            if (drops.stream().anyMatch(stack -> stack.is(DoggyItems.SOY_BEANS.get())
                    && stack.getCount() >= 1 && stack.getCount() <= 3)) {
                return true;
            }
        }
        return false;
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            helper.fail(message);
        }
    }
}
