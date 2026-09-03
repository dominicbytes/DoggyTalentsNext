package doggytalents.gametest;

import doggytalents.common.lib.Constants;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DTNGameTestRegistry {
    private static final DeferredRegister<Consumer<GameTestHelper>> TESTS =
        DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Constants.MOD_ID);

    static {
        TESTS.register("save_01_dog_core_round_trip", () -> DTNGameTests::save01DogCoreRoundTrip);
    }

    private DTNGameTestRegistry() {
    }

    public static void register(IEventBus modBus) {
        TESTS.register(modBus);
    }
}
