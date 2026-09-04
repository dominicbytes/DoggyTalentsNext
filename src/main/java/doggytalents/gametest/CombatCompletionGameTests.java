package doggytalents.gametest;

import doggytalents.DoggyTalents;
import doggytalents.common.talent.RoaringGaleTalent;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import static doggytalents.gametest.LiveGameplayGameTests.require;

public final class CombatCompletionGameTests {
    private CombatCompletionGameTests() {}

    /** COMPLETE-COMBAT: Treat Bag launches gunpowder, dog consumes it and completes its explosion action. */
    public static void ookami(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(5, 8);
        var dog = a.dog(owner, 6, 8, true);
        dog.setLevel(new doggytalents.api.feature.DogLevel(60, 1));
        dog.setHealth(dog.getMaxHealth());
        dog.setTalentLevel(DoggyTalents.OOKAMIKAZE.get(), 1);
        var talent = dog.getTalent(DoggyTalents.OOKAMIKAZE.get(), doggytalents.common.talent.OokamiKazeTalent.class).orElseThrow();
        var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
        cow.setNoAi(true); cow.setPos(a.pos(7, 8)); a.spawn(cow);
        float health = cow.getHealth();
        var bag = new net.minecraft.world.item.ItemStack(doggytalents.DoggyItems.TREAT_BAG.get());
        new doggytalents.common.inventory.TreatBagItemHandler(bag).setStackInSlot(0,
            new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GUNPOWDER, 2));
        owner.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, bag);
        owner.setYRot(-90); owner.setXRot(0);
        h.startSequence().thenIdle(3).thenExecute(() -> {
            bag.getItem().use(h.getLevel(), owner, net.minecraft.world.InteractionHand.MAIN_HAND);
            require(h, new doggytalents.common.inventory.TreatBagItemHandler(bag).getStackInSlot(0).getCount() == 1,
                "Treat Bag did not spend one gunpowder");
            h.getLevel().getEntitiesOfClass(doggytalents.common.entity.misc.DogGunpowderProjectile.class, h.getBounds())
                .forEach(p -> p.setDeltaMovement(new net.minecraft.world.phys.Vec3(0.1, 0, 0.2)));
        }).thenWaitUntil(() -> require(h, cow.getHealth() < health, "gunpowder action did not explode"))
            .thenExecute(() -> {
                require(h, !talent.canExplode() && dog.getHealth() == dog.getMaxHealth(), "explosion cooldown/self safety failed");
                require(h, h.getBlockState(new net.minecraft.core.BlockPos(6, 0, 8)).is(net.minecraft.world.level.block.Blocks.STONE),
                    "Ookami destroyed terrain");
                require(h, h.getLevel().getEntitiesOfClass(doggytalents.common.entity.misc.DogGunpowderProjectile.class, h.getBounds()).isEmpty(),
                    "caught gunpowder projectile remained live");
            }).thenIdle(201).thenExecute(() -> {
                require(h, talent.canExplode(), "Ookami cooldown did not expire through normal ticks");
                a.close();
            }).thenSucceed();
    }

    /** COMPLETE-COMBAT: real damage dispatch and real death/drop event, not direct talent callbacks. */
    public static void criticalAndLoot(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 8, false);
        dog.setTalentLevel(DoggyTalents.BLACK_PELT.get(), 5);
        var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
        cow.setNoAi(true);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(100);
        cow.setPos(a.pos(7, 8));
        a.spawn(cow);
        double attack = dog.getAttributeValue(Attributes.ATTACK_DAMAGE);
        dog.doHurtTarget(h.getLevel(), cow);
        require(h, cow.getHealth() == 100 - attack * 2, "max Black Pelt did not double actual attack damage");
        require(h, dog.getAttributeValue(Attributes.ATTACK_DAMAGE) == attack, "critical left transient damage modifier");
        dog.setTalentLevel(DoggyTalents.HUNTER_DOG.get(), 5);
        int[] drops = {-1, -1};
        Consumer<LivingDropsEvent> before = event -> {
            if (event.getEntity() == cow) drops[0] = event.getDrops().stream().mapToInt(e -> e.getItem().getCount()).sum();
        };
        Consumer<LivingDropsEvent> after = event -> {
            if (event.getEntity() == cow) drops[1] = event.getDrops().stream().mapToInt(e -> e.getItem().getCount()).sum();
        };
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, before);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, after);
        try {
            dog.getRandom().setSeed(0);
            cow.hurtServer(h.getLevel(), cow.damageSources().mobAttack(dog), 1000);
            require(h, cow.isDeadOrDying() && drops[0] > 0 && drops[1] == drops[0] * 2,
                "Hunter did not duplicate actual death loot: " + drops[0] + "/" + drops[1]);
        } finally {
            NeoForge.EVENT_BUS.unregister(before);
            NeoForge.EVENT_BUS.unregister(after);
            a.close();
        }
        h.succeed();
    }

    /** COMPLETE-EXTENDED: each level's real roar hit, effect expiry and hit/miss cooldown. */
    public static void roar(GameTestHelper h, int level) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 8, false);
        dog.setTalentLevel(DoggyTalents.ROARING_GALE.get(), level);
        var enemy = EntityType.SILVERFISH.create(h.getLevel(), EntitySpawnReason.LOAD);
        enemy.setNoAi(true);
        enemy.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        enemy.setHealth(100);
        enemy.setPos(a.pos(7, 8));
        a.spawn(enemy);
        int expected = new int[]{160, 130, 100, 70, 40}[level - 1];
        require(h, RoaringGaleTalent.roar(List.of(dog), h.getLevel(), owner).orElse(0) == expected,
            "hit cooldown differs at level " + level);
        require(h, enemy.getHealth() == 100 - (level == 5 ? 10 : level) && enemy.hasEffect(MobEffects.GLOWING),
            "roar damage/effect differs at level " + level);
        require(h, RoaringGaleTalent.roar(List.of(dog), h.getLevel(), owner).isEmpty(), "roar cooldown bypass");
        h.startSequence().thenIdle(Math.max(expected, 80)).thenExecute(() -> {
            require(h, !enemy.hasEffect(MobEffects.GLOWING) && !enemy.hasEffect(MobEffects.SLOWNESS), "roar debuffs did not expire");
            enemy.discard();
            require(h, RoaringGaleTalent.roar(List.of(dog), h.getLevel(), owner).orElse(0) == expected / 2,
                "expired roar did not use half cooldown on miss");
        }).thenIdle(expected / 2).thenExecute(() -> {
            require(h, RoaringGaleTalent.roar(List.of(dog), h.getLevel(), owner).isPresent(), "miss cooldown did not expire");
            a.close();
        }).thenSucceed();
    }

    /** COMPLETE-EXTENDED: blocked hit followed by an expired cooldown, plus Pest range/max damage. */
    public static void guardAndPest(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 2, 8, false);
        dog.setTalentLevel(DoggyTalents.GUARD_DOG.get(), 5);
        var cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.LOAD);
        cow.setNoAi(true); cow.setPos(a.pos(3, 8)); a.spawn(cow);
        long candidate = 0;
        while (net.minecraft.util.RandomSource.create(candidate).nextInt(12) >= 6) ++candidate;
        final long seed = candidate;
        dog.getRandom().setSeed(seed);
        dog.hurtServer(h.getLevel(), dog.damageSources().mobAttack(cow), 4);
        require(h, dog.getHealth() == dog.getMaxHealth(), "Guard did not block seeded first hit");
        var pest = EntityType.SILVERFISH.create(h.getLevel(), EntitySpawnReason.LOAD);
        pest.setNoAi(true); pest.setPos(a.pos(12, 8)); a.spawn(pest);
        dog.setTalentLevel(DoggyTalents.PEST_FIGHTER.get(), 1);
        h.startSequence().thenIdle(30).thenExecute(() -> {
            dog.getRandom().setSeed(seed);
            dog.hurtServer(h.getLevel(), dog.damageSources().mobAttack(cow), 4);
            require(h, dog.getHealth() == dog.getMaxHealth(), "Guard did not block after cooldown expiry");
            require(h, pest.getHealth() == 8, "level 1 Pest exceeded its radius");
            dog.setTalentLevel(DoggyTalents.PEST_FIGHTER.get(), 5);
        }).thenWaitUntil(() -> require(h, pest.getHealth() < 8, "max Pest did not reach distant target"))
            .thenExecute(() -> {
                require(h, pest.getHealth() == 6, "max Pest damage differs");
                a.close();
            }).thenSucceed();
    }
}
