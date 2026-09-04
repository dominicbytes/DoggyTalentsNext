package doggytalents.common.item;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class WhistleModeCompatibilityTest {

    @Test
    void gameplayWhistle01PreservesValidLegacyModes() {
        for (var mode : WhistleItem.WhistleMode.VALUES) {
            assertSame(mode, WhistleItem.resolveMode(mode.getIndex()));
        }
    }

    @Test
    void gameplayWhistle01DefaultsInvalidLegacyModesToStand() {
        assertSame(WhistleItem.WhistleMode.STAND, WhistleItem.resolveMode(-1));
        assertSame(WhistleItem.WhistleMode.STAND,
            WhistleItem.resolveMode(WhistleItem.WhistleMode.VALUES.length));
        assertSame(WhistleItem.WhistleMode.STAND, WhistleItem.resolveMode(Byte.MAX_VALUE));
    }
}
