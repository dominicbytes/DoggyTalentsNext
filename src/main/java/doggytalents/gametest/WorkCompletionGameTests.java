package doggytalents.gametest;

import doggytalents.DoggyItems;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DogSize;
import doggytalents.common.entity.DogSleepOnManager;
import doggytalents.common.talent.BedDogTalent;
import doggytalents.common.item.ScentTreatItem;
import doggytalents.common.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.ClockAdjustment;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import static doggytalents.gametest.LiveGameplayGameTests.require;

public final class WorkCompletionGameTests {
    private WorkCompletionGameTests() {}

    /** COMPLETE-WORK: real request/readiness and sleep event dispatch; no direct talent completion hook. */
    public static void bed(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var stranger = a.owner(12, 12);
        var dog = a.dog(owner, 7, 8, true);
        dog.setDogSize(DogSize.MODERATO);
        dog.setOrderedToSit(true);
        dog.setTalentLevel(DoggyTalents.BED_DOG.get(), 1);
        var manager = DogSleepOnManager.getServer(h.getLevel());
        long time = h.getLevel().getDefaultClockTime();
        h.setTime(18000);
        h.startSequence().thenIdle(3).thenExecute(() -> {
            dog.setDogHunger(59);
            require(h, !manager.setOrRequestSleepOn(dog, owner).ok(), "low hunger allowed sleeping");
            dog.setDogHunger(100);
            require(h, manager.setOrRequestSleepOn(dog, owner).ok(), "valid sleep request rejected");
        }).thenWaitUntil(() -> require(h, dog.sleepOnManager.isSleepOnReady(), "dog did not become ready through its sleep goal"))
            .thenExecute(() -> {
                try {
                    require(h, manager.setOrRequestSleepOn(dog, owner).ok() && owner.isSleeping(), "ready dog did not start player sleep");
                    require(h, manager.getSleepingOnDog(owner).orElse(null) == dog, "sleep mapping missing");
                    require(h, manager.getSleepingOnDog(stranger).isEmpty(), "unrelated player acquired sleeping dog");
                    float hunger = dog.getDogHunger();
                    NeoForge.EVENT_BUS.post(new SleepFinishedTimeEvent(h.getLevel(), new ClockAdjustment.Relative(6000)));
                    var talent = dog.getTalent(DoggyTalents.BED_DOG.get(), BedDogTalent.class).orElseThrow();
                    require(h, Math.abs(dog.getDogHunger() - hunger + 40) < 0.01 && talent.isOnCooldown(dog),
                        "sleep event did not charge 40 hunger and start cooldown");
                    require(h, !talent.allowDetrain(dog) && !manager.setOrRequestSleepOn(dog, owner).ok(), "sleep cooldown bypassed");
                    h.setTime(h.getLevel().getDefaultClockTime() + 120000);
                    require(h, !talent.isOnCooldown(dog) && talent.allowDetrain(dog), "five-day sleep deadline did not expire");
                } finally {
                    manager.stopPlayerSleepOn(dog);
                    owner.stopSleeping();
                    h.setTime(time);
                    a.close();
                }
            }).thenSucceed();
    }

    /** COMPLETE-WORK: configure scent via interaction, detect real blocks and enter pointing state. */
    public static void sniffer(GameTestHelper h) {
        var a = new LiveGameplayGameTests.Arena(h);
        var owner = a.owner(8, 8);
        var dog = a.dog(owner, 6, 8, true);
        dog.setTalentLevel(DoggyTalents.SNIFFER_DOG.get(), 1);
        for (int x = 1; x < 15; ++x) for (int z = 1; z < 15; ++z)
            h.setBlock(new BlockPos(x, 0, z), Blocks.DIAMOND_ORE);
        var scent = new ItemStack(DoggyItems.SCENT_TREAT.get(), 2);
        var tag = new CompoundTag();
        tag.putString(ScentTreatItem.SCENT_BLOCK_ID, "minecraft:diamond_ore");
        ItemUtil.putTag(scent, tag);
        owner.setItemInHand(InteractionHand.MAIN_HAND, scent);
        dog.mobInteract(owner, InteractionHand.MAIN_HAND);
        require(h, scent.getCount() == 1, "scent interaction did not consume one treat");
        require(h, h.getLevel().getEntitiesOfClass(ItemEntity.class, h.getBounds()).stream()
            .anyMatch(e -> e.getItem().is(DoggyItems.DROOL_SCENT_TREAT.get())
                && ItemUtil.getTag(e.getItem()).equals(tag)), "drooled treat lost scent data");
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        h.onEachTick(() -> {
            // The owner follows the dog's attention request instead of standing out of reach after it backs up.
            owner.setPos(dog.position().add(2, 0, 0));
            var direction = dog.getEyePosition().subtract(owner.getEyePosition());
            owner.setYRot((float) Math.toDegrees(Math.atan2(-direction.x, direction.z)));
            owner.setYHeadRot(owner.getYRot());
            owner.setXRot((float) -Math.toDegrees(Math.atan2(direction.y, direction.horizontalDistance())));
        });
        h.startSequence().thenWaitUntil(() -> require(h, dog.getAnim().name().startsWith("SNIFFER_DOG_POINT"),
            "Sniffer did not point: anim=" + dog.getAnim() + ",pose=" + dog.getDogPose() + ",pos=" + dog.position()
                + ",owner=" + owner.position() + ",looking=" + doggytalents.common.util.DogUtil.checkIfOwnerIsLooking(dog, owner)
                + ",los=" + dog.getSensing().hasLineOfSight(owner)))
            .thenExecute(a::close).thenSucceed();
    }
}
