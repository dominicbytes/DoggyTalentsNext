package doggytalents.gametest;

import doggytalents.common.data.DTLootModifierGameTests;
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
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ANIMATION_BLEND_01_SPAWN_HEALTH =
        TESTS.register("animation_blend_01_spawn_health_stable",
            () -> DTNGameTests::animationBlend01SpawnHealthStable);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ANIMATION_BLEND_01_COMPLETION =
        TESTS.register("animation_blend_01_normal_completion",
            () -> DTNGameTests::animationBlend01NormalCompletion);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_TALENT_CATALOG =
        TESTS.register("game_01_talent_catalog", () -> GameplayParityGameTests::talentCatalog);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_COMBAT =
        TESTS.register("game_01_combat_talents", () -> GameplayParityGameTests::combatTalents);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_MOVEMENT_MOUNT =
        TESTS.register("game_01_movement_mount", () -> GameplayParityGameTests::movementAndMount);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_CARE =
        TESTS.register("game_01_care_talents", () -> GameplayParityGameTests::careTalents);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_COMMANDS =
        TESTS.register("game_01_command_tree", () -> GameplayParityGameTests::commandTree);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_INCAP_RESPAWN =
        TESTS.register("game_01_incapacitation_respawn",
            () -> GameplayParityGameTests::incapacitationAndRespawn);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GAME_01_INTERACTIONS =
        TESTS.register("game_01_accessories_tracker_bath",
            () -> GameplayParityGameTests::accessoriesTrackerAndBath);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LOOT_01_RICE_SOY =
        TESTS.register("loot_01_rice_soy_drops", () -> DTLootModifierGameTests::riceAndSoyDrops);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMAND_LOCATE =
        TESTS.register("review_command_locate", () -> CommandWorkflowGameTests::locate);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMAND_REVIVE =
        TESTS.register("review_command_revive", () -> CommandWorkflowGameTests::revive);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMAND_TRACKER =
        TESTS.register("review_command_tracker", () -> CommandWorkflowGameTests::tracker);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMAND_WHISTLE =
        TESTS.register("review_command_whistle", () -> CommandWorkflowGameTests::whistle);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> UPGRADE_INDEX =
        TESTS.register("review_upgrade_index", () -> doggytalents.common.storage.LegacyDogSavedDataGameTests::originalWorldIndexes);
    private static final java.util.List<DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>> LIVE_GAMEPLAY = java.util.List.of(
        TESTS.register("gameplay_live_follow", () -> LiveGameplayGameTests::follow),
        TESTS.register("gameplay_live_combat", () -> LiveGameplayGameTests::combat),
        TESTS.register("gameplay_live_fire", () -> LiveGameplayGameTests::fireAvoidance),
        TESTS.register("gameplay_live_pack", () -> LiveGameplayGameTests::packPickup),
        TESTS.register("gameplay_live_fish", () -> LiveGameplayGameTests::fishing),
        TESTS.register("gameplay_live_heal", () -> LiveGameplayGameTests::healing),
        TESTS.register("gameplay_live_rescue", () -> LiveGameplayGameTests::rescue),
        TESTS.register("gameplay_live_pest", () -> LiveGameplayGameTests::pest),
        TESTS.register("gameplay_live_roar", () -> LiveGameplayGameTests::roar),
        TESTS.register("gameplay_live_incap", () -> LiveGameplayGameTests::incapacitation),
        TESTS.register("gameplay_live_boundaries", () -> LiveGameplayGameTests::talentBoundaries));

    private DTNGameTestRegistry() {
    }

    private static final java.util.List<DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>> COMPLETION = java.util.List.of(
        TESTS.register("complete_explosion", () -> DefenseCompletionGameTests::explosion),
        TESTS.register("complete_fish_miss", () -> h -> FishingCompletionGameTests.fishing(h, false, false, false)),
        TESTS.register("complete_fish_treasure", () -> h -> FishingCompletionGameTests.fishing(h, true, true, false)),
        TESTS.register("complete_fish_cook", () -> h -> FishingCompletionGameTests.fishing(h, true, false, true)),
        TESTS.register("complete_fish_rain", () -> FishingCompletionGameTests::rain),
        TESTS.register("complete_rescue_budget", () -> CareCompletionGameTests::rescueBudget),
        TESTS.register("complete_submerged", () -> WaterCompletionGameTests::submerged),
        TESTS.register("complete_terrain_follow", () -> WaterCompletionGameTests::terrainFollow),
        TESTS.register("complete_ookami", () -> CombatCompletionGameTests::ookami),
        TESTS.register("complete_bed", () -> WorkCompletionGameTests::bed),
        TESTS.register("complete_sniffer", () -> WorkCompletionGameTests::sniffer),
        TESTS.register("complete_bandages", () -> RecoveryCompletionGameTests::bandages),
        TESTS.register("complete_wagyu", () -> RecoveryCompletionGameTests::wagyu),
        TESTS.register("complete_soak", () -> RecoveryCompletionGameTests::soak),
        TESTS.register("complete_critical_loot", () -> CombatCompletionGameTests::criticalAndLoot),
        TESTS.register("complete_guard_pest", () -> CombatCompletionGameTests::guardAndPest),
        TESTS.register("complete_roar_1", () -> h -> CombatCompletionGameTests.roar(h, 1)),
        TESTS.register("complete_roar_2", () -> h -> CombatCompletionGameTests.roar(h, 2)),
        TESTS.register("complete_roar_3", () -> h -> CombatCompletionGameTests.roar(h, 3)),
        TESTS.register("complete_roar_4", () -> h -> CombatCompletionGameTests.roar(h, 4)),
        TESTS.register("complete_roar_5", () -> h -> CombatCompletionGameTests.roar(h, 5)),
        TESTS.register("complete_food_cure", () -> CareCompletionGameTests::foodAndCure),
        TESTS.register("complete_chemi", () -> CareCompletionGameTests::chemi),
        TESTS.register("complete_water_holder", () -> CareCompletionGameTests::waterHolder),
        TESTS.register("complete_puppy_eyes", () -> CareCompletionGameTests::puppyEyes),
        TESTS.register("complete_fire_drill", () -> CareCompletionGameTests::fireDrill),
        TESTS.register("complete_fall_shock", () -> DefenseCompletionGameTests::fallAndShock),
        TESTS.register("complete_hell_creeper", () -> DefenseCompletionGameTests::hellAndCreeper),
        TESTS.register("complete_retrieve", () -> TravelCompletionGameTests::retrieve),
        TESTS.register("complete_mount", () -> TravelCompletionGameTests::mount),
        TESTS.register("complete_flying", () -> TravelCompletionGameTests::flying),
        TESTS.register("complete_shepherd", () -> TravelCompletionGameTests::shepherd),
        TESTS.register("complete_gate", () -> TravelCompletionGameTests::gate),
        TESTS.register("complete_pack_overflow", () -> SuppliesCompletionGameTests::packOverflow),
        TESTS.register("complete_pack_sharing", () -> SuppliesCompletionGameTests::packSharing),
        TESTS.register("complete_torch", () -> SuppliesCompletionGameTests::torch),
        TESTS.register("complete_armor", () -> SuppliesCompletionGameTests::armor),
        TESTS.register("complete_farming", () -> SuppliesCompletionGameTests::farming));

    public static void register(IEventBus modBus) {
        TESTS.register(modBus);
        modBus.addListener(DTNGameTestRegistry::registerTests);
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        for (var test : COMPLETION) {
            var isolated = event.registerEnvironment(test.getId(), new TestEnvironmentDefinition.AllOf());
            event.registerTest(test.getId(), new FunctionGameTestInstance(test.getKey(),
                new TestData<>(isolated, id("gameplay_arena"), test.getId().getPath().equals("complete_soak") ? 6500 : 2000, 0, true)));
        }
        var environment = event.registerEnvironment(id("default"), new TestEnvironmentDefinition.AllOf());
        for (var test : LIVE_GAMEPLAY) {
            var isolated = event.registerEnvironment(test.getId(), new TestEnvironmentDefinition.AllOf());
            event.registerTest(test.getId(), new FunctionGameTestInstance(test.getKey(),
                new TestData<>(isolated, id("gameplay_arena"), 400, 0, true)));
        }
        for (var test : java.util.List.of(COMMAND_LOCATE, COMMAND_REVIVE, COMMAND_TRACKER, COMMAND_WHISTLE, UPGRADE_INDEX)) {
            var isolated = event.registerEnvironment(test.getId(), new TestEnvironmentDefinition.AllOf());
            event.registerTest(test.getId(), new FunctionGameTestInstance(test.getKey(),
                new TestData<>(isolated, id("gameplay_arena"), 100, 0, true)));
        }
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
        event.registerTest(id("animation_blend_01_spawn_health_stable"), new FunctionGameTestInstance(
            ANIMATION_BLEND_01_SPAWN_HEALTH.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 120, 0, true)));
        event.registerTest(id("animation_blend_01_normal_completion"), new FunctionGameTestInstance(
            ANIMATION_BLEND_01_COMPLETION.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 120, 0, true)));
        event.registerTest(id("game_01_talent_catalog"), new FunctionGameTestInstance(
            GAME_01_TALENT_CATALOG.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_combat_talents"), new FunctionGameTestInstance(
            GAME_01_COMBAT.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_movement_mount"), new FunctionGameTestInstance(
            GAME_01_MOVEMENT_MOUNT.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_care_talents"), new FunctionGameTestInstance(
            GAME_01_CARE.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_command_tree"), new FunctionGameTestInstance(
            GAME_01_COMMANDS.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_incapacitation_respawn"), new FunctionGameTestInstance(
            GAME_01_INCAP_RESPAWN.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("game_01_accessories_tracker_bath"), new FunctionGameTestInstance(
            GAME_01_INTERACTIONS.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        event.registerTest(id("loot_01_rice_soy_drops"), new FunctionGameTestInstance(
            LOOT_01_RICE_SOY.getKey(),
            new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
