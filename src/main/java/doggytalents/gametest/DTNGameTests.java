package doggytalents.gametest;

import doggytalents.DoggyEntityTypes;
import doggytalents.api.feature.DogGender;
import doggytalents.api.feature.DogLevel;
import doggytalents.api.feature.DogMode;
import doggytalents.common.entity.Dog;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class DTNGameTests {
    private static final UUID DOG_UUID = UUID.fromString("81b99c9b-ecfa-4c3c-8e6a-2f986814c731");
    private static final UUID OWNER_UUID = UUID.fromString("41f54ff5-d7c2-44b2-bf31-f43ce49800ca");

    private DTNGameTests() {
    }

    /** SAVE-01: a real DTN dog retains identity and core state through 26.1 entity serialization. */
    public static void save01DogCoreRoundTrip(GameTestHelper helper) {
        var level = helper.getLevel();
        Dog source = createDog(helper);
        source.setUUID(DOG_UUID);
        source.setOwnerUUID(OWNER_UUID);
        source.setTame(true, true);
        source.setDogCustomName(Component.literal("Save One"));
        source.setGender(DogGender.FEMALE);
        source.setMode(DogMode.PATROL);
        source.setDogHunger(43.5F);
        source.setDogIncapValue(117);
        source.setLevel(new DogLevel(37, 9));
        source.setWillObeyOthers(true);
        source.setCanPlayersAttack(true);
        source.setRegardTeamPlayers(true);
        source.setForceSit(true);
        source.setDogAutoMount(true);
        source.setCrossOriginTp(true);
        source.setPatrolTargetLock(true);
        source.setHideArmor(true);
        source.setDogOnDuty(true);
        source.setBedPos(level.dimension(), new BlockPos(4, 2, 6));
        source.setBowlPos(level.dimension(), new BlockPos(7, 2, 3));

        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        source.saveWithoutId(output);

        Dog loaded = createDog(helper);
        loaded.load(TagValueInput.create(
            ProblemReporter.DISCARDING, level.registryAccess(), output.buildResult()));

        require(helper, DOG_UUID.equals(loaded.getUUID()), "dog UUID was not preserved");
        require(helper, OWNER_UUID.equals(loaded.getOwnerUUID()), "owner UUID was not preserved");
        require(helper, loaded.isTame(), "tame state was not preserved");
        require(helper, loaded.getCustomName() != null
            && "Save One".equals(loaded.getCustomName().getString()), "custom name was not preserved");
        require(helper, loaded.getGender() == DogGender.FEMALE, "gender was not preserved");
        require(helper, loaded.getMode() == DogMode.PATROL, "mode was not preserved");
        require(helper, Float.compare(loaded.getDogHunger(), 43.5F) == 0, "hunger was not preserved");
        require(helper, loaded.getDogIncapValue() == 117, "incapacitated value was not preserved");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.NORMAL) == 37,
            "normal level was not preserved");
        require(helper, loaded.getDogLevel().getLevel(DogLevel.Type.KAMI) == 9,
            "kami level was not preserved");
        require(helper, loaded.willObeyOthers(), "will-obey setting was not preserved");
        require(helper, loaded.canOwnerAttack(), "friendly-fire setting was not preserved");
        require(helper, loaded.regardTeamPlayers(), "team-player setting was not preserved");
        require(helper, loaded.forceSit(), "force-sit setting was not preserved");
        require(helper, loaded.dogAutoMount(), "auto-mount setting was not preserved");
        require(helper, loaded.crossOriginTp(), "cross-origin teleport setting was not preserved");
        require(helper, loaded.patrolTargetLock(), "patrol-target lock was not preserved");
        require(helper, loaded.hideArmor(), "hide-armor setting was not preserved");
        require(helper, loaded.dogOnDuty(), "on-duty setting was not preserved");
        require(helper, loaded.getBedPos(level.dimension()).filter(new BlockPos(4, 2, 6)::equals).isPresent(),
            "bed location was not preserved");
        require(helper, loaded.getBowlPos(level.dimension()).filter(new BlockPos(7, 2, 3)::equals).isPresent(),
            "bowl location was not preserved");
        helper.succeed();
    }

    private static Dog createDog(GameTestHelper helper) {
        Dog dog = DoggyEntityTypes.DOG.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        require(helper, dog != null, "dog entity type returned null");
        return dog;
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            helper.fail(message);
        }
    }
}
