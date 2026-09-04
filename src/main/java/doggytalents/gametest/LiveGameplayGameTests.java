package doggytalents.gametest;

import com.mojang.authlib.GameProfile;
import doggytalents.DoggyEntityTypes;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DogMode;
import doggytalents.common.entity.Dog;
import doggytalents.common.storage.DogLocationStorage;
import doggytalents.common.storage.DogRespawnStorage;
import doggytalents.common.talent.PackPuppyTalent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

/** World-outcome tests: normal entity ticks run between the scheduled assertions. */
public final class LiveGameplayGameTests {
    private LiveGameplayGameTests() {
    }

    /** GAMEPLAY-LIVE-FOLLOW */
    public static void follow(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(14, 8);
        var dog = arena.dog(owner, 2, 8, true);
        helper.startSequence().thenIdle(100).thenExecute(() -> {
            require(helper, dog.distanceToSqr(owner) < 16, "dog did not follow owner: " + dog.position());
            dog.setOrderedToSit(true);
            owner.setPos(arena.pos(1, 8));
        }).thenIdle(20).thenExecute(() -> {
            require(helper, dog.isInSittingPose(), "sit order did not reach sitting pose");
            arena.position = dog.position();
        }).thenIdle(60).thenExecute(() -> {
            require(helper, dog.position().distanceToSqr(arena.position) < 0.5, "sitting dog continued following");
            dog.setOrderedToSit(false);
        }).thenIdle(100).thenExecute(() -> {
            require(helper, dog.distanceToSqr(owner) < 16, "standing dog did not resume following");
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-COMBAT */
    public static void combat(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(2, 2);
        var dog = arena.dog(owner, 3, 5, true);
        dog.setMode(DogMode.AGGRESIVE);
        dog.setTalentLevel(DoggyTalents.POISON_FANG.get(), 3);
        var target = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.LOAD);
        target.setNoAi(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        target.setHealth(100);
        target.setPos(arena.pos(10, 5));
        arena.spawn(target);
        dog.setTarget(target);
        helper.startSequence().thenWaitUntil(() -> {
            require(helper, target.getHealth() < 100, "AI attack has not damaged target");
            require(helper, target.hasEffect(MobEffects.POISON), "actual attack did not apply Poison Fang");
        }).thenExecute(() -> {
            require(helper, dog.position().distanceToSqr(arena.pos(3, 5)) > 1, "attack did not involve approach movement");
            require(helper, !dog.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), "level 3 poison immunity failed");
            dog.setTalentLevel(DoggyTalents.POISON_FANG.get(), 2);
            require(helper, dog.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), "level 2 unexpectedly immune to poison");
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-FIRE */
    public static void fireAvoidance(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 8, 8, true);
        helper.setBlock(new BlockPos(8, 0, 8), Blocks.MAGMA_BLOCK);
        helper.startSequence().thenIdle(100).thenExecute(() -> {
            require(helper, dog.isAlive() && !dog.isDefeated(), "dog did not survive fire avoidance");
            require(helper, dog.position().distanceToSqr(arena.pos(8, 8)) > 2,
                "dog did not move away from burning surface: " + dog.position());
            require(helper, !helper.getLevel().getBlockState(dog.blockPosition().below()).is(Blocks.MAGMA_BLOCK),
                "dog remains on burning surface");
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-PACK */
    public static void packPickup(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 4, 4, false);
        dog.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 2);
        var item = arena.item(4, 4, new ItemStack(Items.DIAMOND, 7), false);
        helper.startSequence().thenIdle(30).thenExecute(() -> {
            require(helper, item.isAlive() && count(dog) == 0, "level 2 collected items");
            dog.setTalentLevel(DoggyTalents.PACK_PUPPY.get(), 3);
        }).thenIdle(30).thenExecute(() -> {
            require(helper, !item.isAlive() && count(dog) == 7, "level 3 did not collect exactly seven diamonds");
            arena.item(4, 4, new ItemStack(Items.DIAMOND, 3), true);
        }).thenIdle(30).thenExecute(() -> {
            require(helper, count(dog) == 7, "pickup delay was ignored");
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-FISH: completed real wet/dry cycles, not direct talent callbacks. */
    public static void fishing(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dogs = new ArrayList<Dog>();
        for (int x : new int[]{3, 6, 9, 12}) {
            var dog = arena.dog(owner, x, 4, true);
            dog.setOrderedToSit(true);
            dog.setTalentLevel(DoggyTalents.FISHER_DOG.get(), 5);
            dog.getRandom().setSeed(x);
            helper.setBlock(new BlockPos(x, 1, 4), Blocks.WATER);
            dogs.add(dog);
        }
        // Pin only the random success branch near shake completion; physics and dispatch remain live.
        helper.onEachTick(() -> {
            for (var dog : dogs) {
                if (dog.getDogClassicalShakeAnim(0) >= 1.8F) dog.getRandom().setSeed(0);
            }
        });
        helper.startSequence().thenIdle(10).thenExecute(() -> {
            for (var dog : dogs) {
                require(helper, dog.isInWater(), "fishing fixture did not wet dog");
                helper.setBlock(helper.relativePos(dog.blockPosition()), Blocks.AIR);
                dog.setPos(dog.getX(), arena.pos(1, 1).y, arena.pos(1, 10).z);
            }
        }).thenWaitUntil(() -> {
            var loot = helper.getLevel().getEntitiesOfClass(ItemEntity.class, helper.getBounds());
            require(helper, loot.stream().anyMatch(e -> e.getItem().is(net.minecraft.tags.ItemTags.FISHES)),
                "water shakes produced no fish: " + dogs.stream().map(d -> "ticks=" + d.tickCount + ",pose=" + d.getDogPose() + ",shake=" + d.getDogClassicalShakeAnim(0)).toList());
        }).thenExecute(() -> {
            helper.getLevel().getEntitiesOfClass(ItemEntity.class, helper.getBounds()).forEach(Entity::discard);
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-HEAL */
    public static void healing(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var level4 = arena.dog(owner, 3, 3, true);
        var level5 = arena.dog(owner, 12, 3, true);
        for (var dog : List.of(level4, level5)) {
            dog.setOrderedToSit(true);
            dog.setHealth(20);
        }
        level4.setTalentLevel(DoggyTalents.QUICK_HEALER.get(), 4);
        level5.setTalentLevel(DoggyTalents.QUICK_HEALER.get(), 5);
        helper.startSequence().thenIdle(80).thenExecute(() -> {
            require(helper, level5.getHealth() >= 22 && level4.getHealth() == 20,
                "level 5 seated healing boundary failed: " + level4.getHealth() + "/" + level5.getHealth()
                    + ",ticks=" + level5.tickCount + ",sit=" + level5.isInSittingPose() + ",level=" + level5.getDogLevel(DoggyTalents.QUICK_HEALER));
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-RESCUE */
    public static void rescue(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(10, 8);
        owner.setHealth(7);
        var dog = arena.dog(owner, 3, 8, true);
        dog.setTalentLevel(DoggyTalents.RESCUE_DOG.get(), 5);
        helper.startSequence().thenWaitUntil(() ->
            require(helper, owner.getHealth() >= 14, "rescue action has not healed owner")
        ).thenExecute(() -> {
            require(helper, dog.distanceToSqr(owner) <= 6, "rescue did not approach owner");
            require(helper, dog.getDogHunger() <= 92 && dog.getDogHunger() > 91,
                "rescue did not spend accepted eight hunger: " + dog.getDogHunger());
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-PEST */
    public static void pest(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 4, 4, false);
        dog.setTalentLevel(DoggyTalents.PEST_FIGHTER.get(), 1);
        var target = EntityType.SILVERFISH.create(helper.getLevel(), EntitySpawnReason.LOAD);
        target.setNoAi(true);
        target.setPos(arena.pos(5, 4));
        arena.spawn(target);
        float health = target.getHealth();
        helper.startSequence().thenWaitUntil(() ->
            require(helper, target.getHealth() < health, "Pest Fighter has not damaged nearby silverfish")
        ).thenExecute(arena::close).thenSucceed();
    }

    /** GAMEPLAY-LIVE-ROAR */
    public static void roar(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 4, 4, false);
        dog.setTalentLevel(DoggyTalents.ROARING_GALE.get(), 1);
        var target = EntityType.SILVERFISH.create(helper.getLevel(), EntitySpawnReason.LOAD);
        target.setNoAi(true);
        target.setPos(arena.pos(5, 4));
        arena.spawn(target);
        float health = target.getHealth();
        var cooldown = doggytalents.common.talent.RoaringGaleTalent.roar(List.of(dog), helper.getLevel(), owner);
        require(helper, target.getHealth() == health - 1 && target.hasEffect(MobEffects.SLOWNESS)
            && target.hasEffect(MobEffects.GLOWING), "roar did not damage and debuff enemy");
        require(helper, cooldown.orElse(0) == 160, "accepted roar cooldown changed");
        require(helper, doggytalents.common.talent.RoaringGaleTalent.roar(List.of(dog), helper.getLevel(), owner).isEmpty(),
            "second roar bypassed cooldown");
        arena.close();
        helper.succeed();
    }

    /** GAMEPLAY-LIVE-INCAP */
    public static void incapacitation(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 8, 7, true);
        dog.hurtServer(helper.getLevel(), dog.damageSources().generic(), 1000);
        require(helper, dog.isDefeated() && dog.isAlive() && dog.getHealth() == 1,
            "lethal ordinary damage did not incapacitate an owned dog");
        dog.hurtServer(helper.getLevel(), dog.damageSources().generic(), 1000);
        helper.startSequence().thenIdle(10).thenExecute(() -> {
            require(helper, dog.isAlive() && dog.isDefeated(), "further damage killed an incapacitated dog");
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TOTEM_OF_UNDYING, 2));
            dog.mobInteract(owner, InteractionHand.MAIN_HAND);
            require(helper, owner.getMainHandItem().getCount() == 1, "recovery did not consume one totem");
        }).thenIdle(5).thenExecute(() -> {
            require(helper, dog.isAlive() && !dog.isDefeated() && dog.getHealth() == dog.getMaxHealth()
                && dog.getDogHunger() > 0 && dog.isOrderedToSit(), "totem recovery did not restore healthy state");
            arena.close();
        }).thenSucceed();
    }

    /** GAMEPLAY-LIVE-BOUNDARIES: actual training, damage dispatch and cooldown. */
    public static void talentBoundaries(GameTestHelper helper) {
        var arena = new Arena(helper);
        var owner = arena.owner(8, 8);
        var dog = arena.dog(owner, 4, 4, false);
        double damage = dog.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double speed = dog.getAttributeValue(Attributes.MOVEMENT_SPEED);
        for (int level = 1; level <= 5; ++level) {
            dog.setTalentLevel(DoggyTalents.BLACK_PELT.get(), level);
            dog.setTalentLevel(DoggyTalents.DOGGY_DASH.get(), level);
            require(helper, Math.abs(dog.getAttributeValue(Attributes.ATTACK_DAMAGE) - damage - (level == 5 ? 7 : level)) < 0.00001,
                "Black Pelt attribute mismatch at " + level);
            require(helper, Math.abs(dog.getAttributeValue(Attributes.MOVEMENT_SPEED) - speed - 0.03 * level - (level == 5 ? 0.04 : 0)) < 0.00001,
                "Doggy Dash attribute mismatch at " + level);
        }
        dog.setTalentLevel(DoggyTalents.BLACK_PELT.get(), 0);
        dog.setTalentLevel(DoggyTalents.DOGGY_DASH.get(), 0);
        require(helper, dog.getAttributeValue(Attributes.ATTACK_DAMAGE) == damage
            && dog.getAttributeValue(Attributes.MOVEMENT_SPEED) == speed, "removed talent left its modifier");
        dog.setTalentLevel(DoggyTalents.GUARD_DOG.get(), 5);
        var cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.LOAD);
        cow.setPos(arena.pos(6, 4));
        cow.setNoAi(true);
        arena.spawn(cow);
        long seed = 0;
        while (net.minecraft.util.RandomSource.create(seed).nextInt(12) >= 6) ++seed;
        dog.getRandom().setSeed(seed);
        float health = dog.getHealth();
        dog.hurtServer(helper.getLevel(), dog.damageSources().mobAttack(cow), 4);
        require(helper, dog.getHealth() == health, "seeded Guard Dog block failed");
        dog.hurtServer(helper.getLevel(), dog.damageSources().mobAttack(cow), 4);
        require(helper, dog.getHealth() < health, "Guard Dog ignored cooldown");
        arena.close();
        helper.succeed();
    }

    private static int count(Dog dog) {
        var inventory = dog.getTalent(DoggyTalents.PACK_PUPPY.get(), PackPuppyTalent.class).orElseThrow().inventory();
        int count = 0;
        for (int slot = 0; slot < inventory.getSlots(); ++slot) {
            if (inventory.getStackInSlot(slot).is(Items.DIAMOND)) count += inventory.getStackInSlot(slot).getCount();
        }
        return count;
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) helper.fail(message);
    }

    private static final class Arena {
        private final GameTestHelper helper;
        private final List<Entity> entities = new ArrayList<>();
        private Vec3 position;

        private Arena(GameTestHelper helper) { this.helper = helper; }

        private Vec3 pos(int x, int z) { return Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(x, 1, z))); }

        private FakePlayer owner(int x, int z) {
            var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "GameplayOwner"));
            player.setPos(pos(x, z));
            helper.getLevel().addNewPlayer(player);
            entities.add(player);
            return player;
        }

        private Dog dog(FakePlayer owner, int x, int z, boolean ai) {
            var dog = DoggyEntityTypes.DOG.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
            dog.tame(owner);
            dog.setMode(DogMode.DOCILE);
            dog.setOrderedToSit(false);
            dog.setDogHunger(100);
            dog.setNoAi(!ai);
            dog.setPos(pos(x, z));
            dog.getRandom().setSeed(0);
            spawn(dog);
            return dog;
        }

        private void spawn(Entity entity) { helper.getLevel().addFreshEntity(entity); entities.add(entity); }

        private ItemEntity item(int x, int z, ItemStack stack, boolean delayed) {
            var pos = pos(x, z);
            var item = new ItemEntity(helper.getLevel(), pos.x, pos.y, pos.z, stack);
            item.setDeltaMovement(Vec3.ZERO);
            if (delayed) item.setNeverPickUp(); else item.setNoPickUpDelay();
            spawn(item);
            return item;
        }

        private void close() {
            for (var entity : entities) {
                entity.discard();
                if (entity instanceof Dog) {
                    DogLocationStorage.get(helper.getLevel()).remove(entity.getUUID());
                    DogRespawnStorage.get(helper.getLevel()).remove(entity.getUUID());
                }
            }
        }
    }
}
