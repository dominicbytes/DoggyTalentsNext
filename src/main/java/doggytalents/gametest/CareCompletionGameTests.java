package doggytalents.gametest;

import static doggytalents.gametest.LiveGameplayGameTests.require;

import doggytalents.DoggyTalents;
import doggytalents.gametest.LiveGameplayGameTests.Arena;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CareCompletionGameTests {
    /** COMPLETE-EXTENDED: rejected low budget/foreign owner, controlled 40-tick rescue cooldown. */
    public static void rescueBudget(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var stranger = a.owner(9, 8);
        owner.setHealth(7); stranger.setHealth(7);
        var branchRandom = new net.minecraft.world.level.levelgen.LegacyRandomSource(0) {
            @Override public int nextInt(int bound) { return bound == 3 ? 2 : super.nextInt(bound); }
        };
        var dog = new doggytalents.common.entity.Dog(doggytalents.DoggyEntityTypes.DOG.get(), h.getLevel()) {
            @Override public net.minecraft.util.RandomSource getRandom() { return branchRandom == null ? super.getRandom() : branchRandom; }
        };
        a.prepareDog(dog, owner, 7, 8, true);
        dog.setTalentLevel(DoggyTalents.RESCUE_DOG.get(), 5);
        dog.setDogHunger(17);
        h.startSequence().thenIdle(40).thenExecute(() -> {
            require(h, owner.getHealth() == 7 && stranger.getHealth() == 7, "rescue bypassed hunger budget");
            dog.setDogHunger(100);
        }).thenWaitUntil(() -> require(h, owner.getHealth() >= 14, "affordable rescue did not heal owner"))
            .thenExecute(() -> {
                require(h, stranger.getHealth() == 7 && dog.getDogHunger() > 91 && dog.getDogHunger() <= 92,
                    "rescue ownership/cost mismatch");
                owner.setHealth(7);
            }).thenIdle(20).thenExecute(() -> require(h, owner.getHealth() == 7, "rescue bypassed 40-tick cooldown"))
            .thenWaitUntil(() -> require(h, owner.getHealth() >= 14, "rescue cooldown never expired"))
            .thenExecute(a::close).thenSucceed();
    }
    private CareCompletionGameTests() { }

    /** COMPLETE-CARE: public feeding interactions preserve the accepted food thresholds. */
    public static void foodAndCure(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 7, 8, false);
        for (int level = 1; level <= 5; ++level) {
            dog.setTalentLevel(DoggyTalents.HAPPY_EATER.get(), level);
            for (var food : new net.minecraft.world.item.Item[]{Items.COD, Items.ROTTEN_FLESH}) {
                dog.setDogHunger(20);
                float hungerBefore = dog.getDogHunger();
                owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(food, 2));
                dog.mobInteract(owner, InteractionHand.MAIN_HAND);
                boolean accepted = level >= (food == Items.COD ? 2 : 3);
                require(h, owner.getMainHandItem().getCount() == (accepted ? 1 : 2),
                    "food consumption boundary " + food + " level " + level);
                require(h, (dog.getDogHunger() > hungerBefore) == accepted, "food hunger outcome " + level);
            }
        }
        dog.setTalentLevel(DoggyTalents.HAPPY_EATER.get(), 0);
        dog.setTalentLevel(DoggyTalents.POISON_FANG.get(), 5);
        owner.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SPIDER_EYE, 2));
        dog.setDogHunger(29);
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, owner.hasEffect(MobEffects.POISON) && owner.getMainHandItem().getCount() == 2,
            "underfed cure consumed item or cured owner");
        dog.setDogHunger(100);
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, !owner.hasEffect(MobEffects.POISON) && owner.getMainHandItem().getCount() == 1
            && dog.getDogHunger() == 70, "Poison Fang cure cost/outcome");
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, owner.getMainHandItem().getCount() == 1, "healthy owner consumed a second cure");
        a.close();
        h.succeed();
    }

    /** COMPLETE-CARE: natural approach/absorption followed by actual attack transfer. */
    public static void chemi(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(10, 8);
        var dog = a.dog(owner, 3, 8, true);
        dog.setTalentLevel(DoggyTalents.CHEMI_CANINE.get(), 5);
        owner.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2000));
        dog.setDogHunger(19);
        h.startSequence().thenIdle(40).thenExecute(() -> {
            require(h, owner.hasEffect(MobEffects.WEAKNESS), "underfed Chemi Canine absorbed effect");
            dog.setDogHunger(100);
        }).thenWaitUntil(() -> require(h, !owner.hasEffect(MobEffects.WEAKNESS), "effect not absorbed"))
        .thenExecute(() -> {
            require(h, dog.getDogHunger() <= 90 && dog.getDogHunger() > 89, "Chemi hunger cost");
            var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
            cow.setPos(dog.position().add(1, 0, 0));
            cow.setNoAi(true);
            a.spawn(cow);
            dog.doHurtTarget(h.getLevel(), cow);
            require(h, cow.hasEffect(MobEffects.WEAKNESS), "absorbed effect not transferred on attack");
            require(h, !dog.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100)), "max Chemi immunity");
            dog.setTalentLevel(DoggyTalents.CHEMI_CANINE.get(), 4);
            require(h, dog.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100)), "level 4 incorrectly immune");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-CARE: refill interaction and automatic extinguishing action. */
    public static void waterHolder(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(10, 8);
        var dog = a.dog(owner, 3, 8, true);
        dog.setTalentLevel(DoggyTalents.WATER_HOLDER.get(), 4);
        owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, owner.getMainHandItem().is(Items.BUCKET), "water refill did not return bucket");
        var state = new CompoundTag();
        dog.getTalent(DoggyTalents.WATER_HOLDER.get()).orElseThrow().writeToNBT(dog, state);
        require(h, state.getIntOr("DTwaterUnitLeft", -1) == 17, "level 4 water capacity");
        dog.setTalentLevel(DoggyTalents.WATER_HOLDER.get(), 5);
        owner.setOnGround(true);
        owner.setRemainingFireTicks(1000);
        h.startSequence().thenWaitUntil(() -> require(h, !owner.isOnFire(), "owner not extinguished"))
            .thenExecute(() -> {
                require(h, dog.distanceToSqr(owner) < 16, "extinguishing did not approach owner");
                a.close();
            }).thenSucceed();
    }

    /** COMPLETE-SOCIAL: reputation is created by normal ticks and not repeated during cooldown. */
    public static void puppyEyes(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 5, 5, false);
        dog.setTalentLevel(DoggyTalents.PUPPY_EYES.get(), 1);
        var villager = EntityType.VILLAGER.create(h.getLevel(), EntitySpawnReason.LOAD);
        villager.setPos(a.pos(6, 5));
        villager.setNoAi(true);
        a.spawn(villager);
        h.startSequence().thenIdle(45).thenExecute(() -> {
            require(h, villager.getGossips().getReputation(owner.getUUID(), t -> t == GossipType.MINOR_POSITIVE) == 20,
                "Puppy Eyes did not grant reputation");
        }).thenIdle(120).thenExecute(() -> {
            require(h, villager.getGossips().getReputation(owner.getUUID(), t -> t == GossipType.MINOR_POSITIVE) == 20,
                "Puppy Eyes bypassed cooldown");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-CARE: actual roll animation state must precede early extinguishing. */
    public static void fireDrill(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 8, true);
        dog.setTalentLevel(DoggyTalents.FIRE_DRILL.get(), 1);
        dog.setRemainingFireTicks(1000);
        h.startSequence().thenWaitUntil(() -> require(h,
            dog.getAnim() == doggytalents.api.anim.DogAnimation.STOP_DROP_ROLL, "roll not started"))
        .thenWaitUntil(() -> require(h, !dog.isOnFire(), "roll did not extinguish dog"))
        .thenExecute(() -> {
            require(h, dog.tickCount < 300 && dog.isAlive() && !dog.isDefeated(), "roll did not rescue dog promptly");
        }).thenWaitUntil(() -> require(h, dog.getAnim() != doggytalents.api.anim.DogAnimation.STOP_DROP_ROLL,
            "roll did not finish"))
        .thenExecute(a::close).thenSucceed();
    }
}
