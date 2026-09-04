package doggytalents.gametest;

import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.common.talent.PackPuppyTalent;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import static doggytalents.gametest.LiveGameplayGameTests.require;

public final class RecoveryCompletionGameTests {
    private RecoveryCompletionGameTests() {}

    /** COMPLETE-RECOVERY: actual defeat and eight spaced interactions, then natural recovery ticks. */
    public static void bandages(GameTestHelper h) { recover(h, false); }
    public static void wagyu(GameTestHelper h) { recover(h, true); }

    private static void recover(GameTestHelper h, boolean starved) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 7, 8, true);
        dog.hurtServer(h.getLevel(), starved ? dog.damageSources().starve() : dog.damageSources().generic(), 1000);
        require(h, dog.isDefeated(), "damage did not incapacitate dog");
        // End-stage injury fixture; do not spend 72,000 ticks waiting for the whole three-day timer.
        dog.setDogIncapValue(5);
        var item = starved ? DoggyItems.GOLDEN_A_FIVE_WAGYU.get() : DoggyItems.BANDAID.get();
        owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 10));
        var sequence = h.startSequence().thenIdle(2);
        for (int i = 0; i < 8; ++i) {
            int remaining = 9 - i;
            sequence.thenExecute(() -> {
                dog.mobInteract(owner, InteractionHand.MAIN_HAND);
                require(h, owner.getMainHandItem().getCount() == remaining, "recovery item not consumed exactly once");
                dog.mobInteract(owner, InteractionHand.MAIN_HAND);
                require(h, owner.getMainHandItem().getCount() == remaining, "repeat bypassed bandage cooldown");
            }).thenIdle(11);
        }
        sequence.thenWaitUntil(() -> require(h, !dog.isDefeated(), "bandaged dog has not recovered"))
            .thenExecute(() -> {
                require(h, dog.getHealth() == dog.getMaxHealth() && dog.getDogHunger() > 99,
                    "recovery did not restore health/hunger");
                dog.mobInteract(owner, InteractionHand.MAIN_HAND);
                // Wagyu deliberately remains an always-edible buff food after recovery.
                require(h, owner.getMainHandItem().getCount() == (starved ? 1 : 2), "healthy recovery-item behavior changed");
                a.close();
            }).thenSucceed();
    }

    /** COMPLETE-SOAK / COMPLETE-RECOVERY: 6,000 normal server ticks, including passive recovery. */
    public static void soak(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 8, true);
        dog.setOrderedToSit(true);
        dog.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 3);
        var inventory = dog.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow().inventory();
        inventory.setStackInSlot(0, new ItemStack(Items.DIAMOND, 17));
        var recovering = a.dog(owner, 10, 8, true);
        recovering.hurtServer(h.getLevel(), recovering.damageSources().generic(), 1000);
        recovering.setDogIncapValue(1);
        h.startSequence().thenIdle(1100).thenExecute(() -> {
            require(h, !recovering.isDefeated() && recovering.getHealth() == recovering.getMaxHealth(),
                "passive recovery did not complete through ordinary ticks");
        }).thenIdle(4900).thenExecute(() -> {
            require(h, dog.isAlive() && !dog.isDefeated() && dog.getHealth() == dog.getMaxHealth(), "soak lost healthy dog");
            require(h, dog.getDogHunger() > 50 && dog.getDogHunger() <= 100, "soak hunger outside bounded range");
            require(h, inventory.getStackInSlot(0).getCount() == 17 && dog.distanceToSqr(owner) < 16,
                "soak changed stored items or sitting location");
            require(h, recovering.isAlive() && !recovering.isDefeated(), "recovered dog regressed during soak");
            a.close();
        }).thenSucceed();
    }
}
