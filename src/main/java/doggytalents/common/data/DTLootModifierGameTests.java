package doggytalents.common.data;

import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
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
        require(helper, producesRice(helper, Blocks.SHORT_GRASS, ItemStack.EMPTY),
            "short grass never produced rice through the registered global loot modifier");
        for (var type : new EntityType<?>[] {
                EntityType.ZOMBIE, EntityType.CREEPER, EntityType.SKELETON, EntityType.SPIDER }) {
            require(helper, producesSoy(helper, type, true),
                "a dog-killed " + type + " never produced soy through the registered global loot modifier");
        }
        // LOOT-02: modifiers must not leak into excluded tools, tables, victims, or attackers.
        require(helper, !producesRice(helper, Blocks.SHORT_GRASS, new ItemStack(Items.SHEARS)),
            "sheared grass incorrectly produced rice");
        require(helper, !producesRice(helper, Blocks.STONE, ItemStack.EMPTY),
            "a different block loot table incorrectly produced rice");
        require(helper, !producesSoy(helper, EntityType.COW, true),
            "a dog-killed passive mob incorrectly produced soy");
        require(helper, !producesSoy(helper, EntityType.ZOMBIE, false),
            "a non-dog kill incorrectly produced soy");
        helper.succeed();
    }

    private static boolean producesRice(GameTestHelper helper, Block block, ItemStack tool) {
        var level = helper.getLevel();
        var params = new LootParams.Builder(level)
            .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO)))
            .withParameter(LootContextParams.TOOL, tool)
            .create(LootContextParamSets.BLOCK);
        var context = new LootContext.Builder(params)
            .withOptionalRandomSeed(0xD06L)
            .create(Optional.empty());
        var lootTable = block.getLootTable().orElseThrow().identifier();

        for (int i = 0; i < ATTEMPTS; ++i) {
            var drops = CommonHooks.modifyLoot(lootTable, new ObjectArrayList<>(), context);
            if (drops.stream().anyMatch(stack -> stack.is(DoggyItems.RICE_GRAINS.get()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean producesSoy(GameTestHelper helper, EntityType<?> victimType, boolean dogKill) {
        var level = helper.getLevel();
        LivingEntity attacker = dogKill
            ? DoggyEntityTypes.DOG.get().create(level, EntitySpawnReason.LOAD)
            : EntityType.COW.create(level, EntitySpawnReason.LOAD);
        var victim = victimType.create(level, EntitySpawnReason.LOAD);
        require(helper, attacker != null && victim != null, "loot test entities could not be created");
        var params = new LootParams.Builder(level)
            .withParameter(LootContextParams.THIS_ENTITY, victim)
            .withParameter(LootContextParams.ORIGIN, victim.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().mobAttack(attacker))
            .withParameter(LootContextParams.ATTACKING_ENTITY, attacker)
            .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, attacker)
            .create(LootContextParamSets.ENTITY);
        var context = new LootContext.Builder(params)
            .withOptionalRandomSeed(0x501L)
            .create(Optional.empty());
        var lootTable = victimType.getDefaultLootTable().orElseThrow().identifier();

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
