package doggytalents.gametest;

import static doggytalents.gametest.LiveGameplayGameTests.require;

import doggytalents.DoggyTalents;
import doggytalents.gametest.LiveGameplayGameTests.Arena;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;

public final class DefenseCompletionGameTests {
    private DefenseCompletionGameTests() { }

    /** COMPLETE-DEFENSE: a live primed TNT explosion, including the max-level exclusion hook. */
    public static void explosion(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(1, 1);
        var control = a.dog(owner, 5, 8, false);
        var maximum = a.dog(owner, 11, 8, false);
        maximum.setTalentLevel(DoggyTalents.SHOCK_ABSORBER.get(), 5);
        var tnt = EntityType.TNT.create(h.getLevel(), EntitySpawnReason.LOAD);
        tnt.setPos(a.pos(8, 8));
        tnt.setFuse(10);
        a.spawn(tnt);
        h.startSequence().thenWaitUntil(() -> require(h, !tnt.isAlive(), "TNT did not detonate"))
            .thenExecute(() -> {
                require(h, control.getHealth() < control.getMaxHealth(), "control did not receive explosion damage");
                require(h, maximum.getHealth() == maximum.getMaxHealth(), "max Shock Absorber did not negate explosion");
                a.close();
            }).thenSucceed();
    }

    /** COMPLETE-DEFENSE: damage dispatched through the entity, not talent helper calls. */
    public static void fallAndShock(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        var attacker = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
        attacker.setPos(a.pos(1, 1));
        attacker.setNoAi(true);
        a.spawn(attacker);
        double[] resistance = {0.25, 0.4, 0.6, 0.75, 0.9, 1};
        for (int level = 0; level <= 5; ++level) {
            var dog = a.dog(owner, 2 + level * 2, 3, false);
            if (level > 0) dog.setTalentLevel(DoggyTalents.PILLOW_PAW.get(), level);
            float before = dog.getHealth();
            dog.causeFallDamage(18, 1, dog.damageSources().fall());
            require(h, Math.abs(dog.getHealth() - (before - (level == 5 ? 0 : 15 - level * 3))) < 0.01,
                "Pillow Paw fall boundary level=" + level + " health=" + dog.getHealth());
            require(h, !dog.canTrample(h.getLevel(), Blocks.FARMLAND.defaultBlockState(),
                h.absolutePos(new BlockPos(2, 0, 3)), 18), "inherited no-trampling rule changed");
            var shock = a.dog(owner, 2 + level * 2, 6, false);
            if (level > 0) shock.setTalentLevel(DoggyTalents.SHOCK_ABSORBER.get(), level);
            require(h, Math.abs(shock.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) - resistance[level]) < 0.001,
                "knockback resistance level=" + level);
            before = shock.getHealth();
            shock.hurtServer(h.getLevel(), shock.damageSources().sonicBoom(attacker), 10);
            float expected = level == 0 ? 10 : level * 2;
            require(h, Math.abs(shock.getHealth() - before + expected) < 0.01,
                "inherited sonic damage level=" + level + " health=" + shock.getHealth());
            shock.setTalentLevel(DoggyTalents.SHOCK_ABSORBER.get(), 0);
            require(h, shock.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) == 0.25, "resistance not reset");
        }
        a.close();
        h.succeed();
    }

    /** COMPLETE-DEFENSE / COMPLETE-COMBAT: attack fire boundary and max fire/freeze immunity. */
    public static void hellAndCreeper(GameTestHelper h) {
        var a = new Arena(h);
        var owner = a.owner(8, 8);
        for (int level = 3; level <= 5; ++level) {
            var dog = a.dog(owner, level * 2, 4, false);
            dog.setTalentLevel(DoggyTalents.HELL_HOUND.get(), level);
            var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
            cow.setPos(dog.position().add(1, 0, 0));
            cow.setNoAi(true);
            a.spawn(cow);
            dog.doHurtTarget(h.getLevel(), cow);
            require(h, level == 3 ? !cow.isOnFire() : cow.getRemainingFireTicks() == (level == 5 ? 300 : 80),
                "Hell Hound attack fire level=" + level + " ticks=" + cow.getRemainingFireTicks());
            if (level == 5) {
                float health = dog.getHealth();
                dog.hurtServer(h.getLevel(), dog.damageSources().inFire(), 8);
                dog.hurtServer(h.getLevel(), dog.damageSources().freeze(), 8);
                require(h, dog.getHealth() == health, "Hell Hound immunity");
            }
        }
        for (int level : new int[]{4, 5}) {
            var dog = a.dog(owner, level * 2, 10, false);
            dog.setTalentLevel(DoggyTalents.CREEPER_SWEEPER.get(), level);
            var creeper = EntityType.CREEPER.create(h.getLevel(), EntitySpawnReason.LOAD);
            creeper.setPos(dog.position().add(1, 0, 0));
            creeper.setNoAi(true);
            a.spawn(creeper);
            dog.doHurtTarget(h.getLevel(), creeper);
            require(h, (creeper.getHealth() == 0) == (level == 5), "Creeper Sweeper kill boundary");
        }
        a.close();
        h.succeed();
    }
}
