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
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
