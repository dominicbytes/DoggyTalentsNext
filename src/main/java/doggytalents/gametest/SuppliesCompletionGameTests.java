package doggytalents.gametest;

import static doggytalents.gametest.LiveGameplayGameTests.require;

import doggytalents.DoggyTalents;
import doggytalents.common.talent.DoggyTorchTalent;
import doggytalents.common.talent.PackPuppyTalent;
import doggytalents.common.talent.doggy_tools.DoggyToolsTalent;
import doggytalents.gametest.LiveGameplayGameTests.Arena;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class SuppliesCompletionGameTests {
    private SuppliesCompletionGameTests() { }

    /** COMPLETE-SUPPLIES: overflowing collection conserves inventory plus item-entity totals. */
    public static void packOverflow(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 4, 4, false);
        dog.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 3);
        var inv = dog.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow().inventory();
        for (int i = 0; i < inv.getSlots(); ++i) inv.setStackInSlot(i, new ItemStack(Items.DIAMOND, 64));
        var dropped = a.item(4, 4, new ItemStack(Items.DIAMOND, 7), false);
        h.startSequence().thenIdle(25).thenExecute(() -> {
            require(h, dropped.isAlive() && dropped.getItem().getCount() == 7, "full pack lost drops");
            inv.setStackInSlot(0, new ItemStack(Items.DIAMOND, 60));
        }).thenIdle(25).thenExecute(() -> {
            require(h, inv.getStackInSlot(0).getCount() == 64 && dropped.isAlive() && dropped.getItem().getCount() == 3,
                "partial pickup did not conserve 67 diamonds");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-SUPPLIES: real sharing action consumes food and does not feed another owner's dog. */
    public static void packSharing(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var other = a.owner(13, 13);
        var feeder = a.dog(owner, 4, 4, true);
        feeder.setOrderedToSit(true);
        feeder.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 4);
        var inv = feeder.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow().inventory();
        inv.setStackInSlot(0, new ItemStack(Items.COOKED_BEEF, 4));
        var hungry = a.dog(owner, 8, 4, true);
        hungry.setDogHunger(10);
        var stranger = a.dog(other, 5, 5, true);
        stranger.setDogHunger(10);
        h.startSequence().thenWaitUntil(() -> require(h, hungry.getDogHunger() >= 80, "pack did not feed hungry dog"))
        .thenExecute(() -> {
            require(h, inv.getStackInSlot(0).getCount() < 4, "feeding created food");
            require(h, stranger.getDogHunger() <= 10, "pack fed an unrelated dog");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-SUPPLIES: a dark sealed room tests torch consumption and disabled placement. */
    public static void torch(GameTestHelper h) {
        var a = new Arena(h);
        for (int x = 2; x <= 12; ++x) for (int z = 2; z <= 12; ++z)
            for (int y = 1; y <= 5; ++y)
                if (x == 2 || x == 12 || z == 2 || z == 12 || y == 5)
                    h.setBlock(new BlockPos(x, y, z), Blocks.STONE);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 6, false);
        dog.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 1);
        dog.setTalentLevel(DoggyTalents.DOGGY_TORCH.get(), 4);
        var inv = dog.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow().inventory();
        inv.setStackInSlot(0, new ItemStack(Items.TORCH, 2));
        var pos = new BlockPos(6, 1, 6);
        h.startSequence().thenWaitUntil(() -> require(h, h.getBlockState(pos).is(Blocks.TORCH), "torch not placed"))
        .thenExecute(() -> {
            require(h, inv.getStackInSlot(0).getCount() == 1, "torch placement consumption");
            dog.getTalent(DoggyTalents.DOGGY_TORCH.get(), DoggyTorchTalent.class).orElseThrow().setPlacingTorch(false);
            h.setBlock(pos, Blocks.AIR);
        }).thenIdle(30).thenExecute(() -> {
            require(h, h.getBlockState(pos).isAir() && inv.getStackInSlot(0).getCount() == 1, "disabled torch placed");
            inv.setStackInSlot(0, ItemStack.EMPTY);
            dog.setTalentLevel(DoggyTalents.DOGGY_TORCH.get(), 5);
            dog.getTalent(DoggyTalents.DOGGY_TORCH.get(), DoggyTorchTalent.class).orElseThrow().setPlacingTorch(true);
        }).thenWaitUntil(() -> require(h, h.getBlockState(pos).is(Blocks.TORCH), "max torch required inventory"))
        .thenExecute(a::close).thenSucceed();
    }

    /** COMPLETE-SUPPLIES: XP repair threshold and armor removal drop the actual equipped item. */
    public static void armor(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(12, 12);
        var dog = a.dog(owner, 4, 4, false);
        dog.setTalentLevel(DoggyTalents.DOGGY_ARMOR.get(), 2);
        var helmet = new ItemStack(Items.DIAMOND_HELMET);
        helmet.setDamageValue(50);
        helmet.enchant(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING), 1);
        dog.setItemSlot(EquipmentSlot.HEAD, helmet);
        var p = dog.position();
        var orb = new ExperienceOrb(h.getLevel(), p.x, p.y, p.z, 5);
        a.spawn(orb);
        h.startSequence().thenIdle(15).thenExecute(() -> {
            require(h, helmet.getDamageValue() == 50 && orb.isAlive(), "level 2 repaired armor");
            dog.setTalentLevel(DoggyTalents.DOGGY_ARMOR.get(), 3);
        }).thenWaitUntil(() -> require(h, helmet.getDamageValue() < 50 && !orb.isAlive(), "XP did not repair armor"))
        .thenExecute(() -> dog.setTalentLevel(DoggyTalents.DOGGY_ARMOR.get(), 0))
        .thenExecute(() -> {
            var drops = h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds(), e -> e.getItem().is(Items.DIAMOND_HELMET));
            require(h, dog.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && drops.size() == 1, "armor removal lost/duplicated helmet");
            drops.forEach(ItemEntity::discard);
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-WORK: autonomous hoe selection harvests and replants a mature crop. */
    public static void farming(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 5, 5, true);
        dog.setTalentLevel(DoggyTalents.DOGGY_TOOLS.get(), 2);
        var tools = dog.getTalent(DoggyTalents.DOGGY_TOOLS.get(), DoggyToolsTalent.class).orElseThrow().getTools();
        tools.setStackInSlot(0, new ItemStack(Items.STONE_HOE));
        tools.setStackInSlot(1, new ItemStack(Items.WHEAT_SEEDS));
        var farm = new BlockPos(6, 0, 5);
        var crop = farm.above();
        h.setBlock(farm, Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7));
        h.setBlock(crop, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7));
        h.startSequence().thenWaitUntil(() -> require(h,
            !h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds(), e -> e.getItem().is(Items.WHEAT)).isEmpty(), "dog did not harvest wheat"))
        .thenWaitUntil(() -> require(h, h.getBlockState(crop).is(Blocks.WHEAT) && h.getBlockState(crop).getValue(CropBlock.AGE) == 0,
            "dog did not replant crop"))
        .thenExecute(() -> {
            require(h, tools.getStackInSlot(1).getCount() == 1, "inherited reusable seed sample changed");
            h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds()).forEach(ItemEntity::discard);
            a.close();
        }).thenSucceed();
    }
}
