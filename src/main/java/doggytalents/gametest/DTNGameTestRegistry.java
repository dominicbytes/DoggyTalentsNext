package doggytalents.gametest;

import doggytalents.common.lib.Constants;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DTNGameTestRegistry {
    private static final DeferredRegister<Consumer<GameTestHelper>> TESTS =
        DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Constants.MOD_ID);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_CORE =
        TESTS.register("save_01_dog_core_round_trip", () -> DTNGameTests::save01DogCoreRoundTrip);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_EXTENDED =
        TESTS.register("save_01_dog_extended_round_trip", () -> DTNGameTests::save01DogExtendedRoundTrip);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_BLOCK_ENTITIES =
        TESTS.register("save_01_block_entity_round_trip", () -> DTNGameTests::save01BlockEntityRoundTrip);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_RICE_MILL_PROGRESS =
        TESTS.register("save_01_rice_mill_progress_round_trip", () -> DTNGameTests::save01RiceMillProgressRoundTrip);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_STATS_TRACKER =
        TESTS.register("save_01_stats_tracker_round_trip", () -> DTNGameTests::save01StatsTrackerRoundTrip);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SAVE_01_LEGACY_FIXTURE =
        TESTS.register("save_01_legacy_dog_fixture_upgrade", () -> DTNGameTests::save01LegacyDogFixtureUpgrade);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ITEM_HANDLER_01 =
        TESTS.register("item_handler_01_transactional_storage", () -> DTNGameTests::itemHandler01TransactionalStorage);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ITEM_DATA_01 =
        TESTS.register("item_data_01_custom_data_compatibility",
            () -> DTNGameTests::itemData01CustomDataCompatibility);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAMEPLAY_WHISTLE_01 =
        TESTS.register("gameplay_whistle_01_custom_data_compatibility",
            () -> DTNGameTests::gameplayWhistle01CustomDataCompatibility);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAMEPLAY_FOOD_01 =
        TESTS.register("gameplay_food_01_consumption", () -> DTNGameTests::gameplayFood01Consumption);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAMEPLAY_TRAINING_01 =
        TESTS.register("gameplay_training_01_wolf_conversion", () -> DTNGameTests::gameplayTraining01WolfConversion);

    private DTNGameTestRegistry() {
    }

    public static void register(IEventBus modBus) {
        TESTS.register(modBus);
        modBus.addListener(DTNGameTestRegistry::registerTests);
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("default"), new TestEnvironmentDefinition.AllOf());
        event.registerTest(id("save_01_dog_core_round_trip"), new FunctionGameTestInstance(
            SAVE_01_CORE.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("save_01_dog_extended_round_trip"), new FunctionGameTestInstance(
            SAVE_01_EXTENDED.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("save_01_block_entity_round_trip"), new FunctionGameTestInstance(
            SAVE_01_BLOCK_ENTITIES.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("save_01_rice_mill_progress_round_trip"), new FunctionGameTestInstance(
            SAVE_01_RICE_MILL_PROGRESS.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("save_01_stats_tracker_round_trip"), new FunctionGameTestInstance(
            SAVE_01_STATS_TRACKER.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("save_01_legacy_dog_fixture_upgrade"), new FunctionGameTestInstance(
            SAVE_01_LEGACY_FIXTURE.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("item_handler_01_transactional_storage"), new FunctionGameTestInstance(
            ITEM_HANDLER_01.getKey(), new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("item_data_01_custom_data_compatibility"), new FunctionGameTestInstance(
            ITEM_DATA_01.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("gameplay_whistle_01_custom_data_compatibility"), new FunctionGameTestInstance(
            GAMEPLAY_WHISTLE_01.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("gameplay_food_01_consumption"), new FunctionGameTestInstance(
            GAMEPLAY_FOOD_01.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("gameplay_training_01_wolf_conversion"), new FunctionGameTestInstance(
            GAMEPLAY_TRAINING_01.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
