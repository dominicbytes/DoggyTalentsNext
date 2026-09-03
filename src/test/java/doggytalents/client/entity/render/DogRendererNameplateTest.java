package doggytalents.client.entity.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.minecraft.network.chat.Component;

class DogRendererNameplateTest {

    @Test
    void name01ColorsTheHealthyPartAndLeavesTheRemainderGray() {
        var colored = DogRenderer.colorTextWithHealth(Component.literal("Rover"), 7, 10);

        assertEquals("Rover", colored.getString());
        assertEquals(0x0aff43, colored.getStyle().getColor().getValue());
        assertEquals("r", colored.getSiblings().getFirst().getString());
        assertEquals(0x4a4a4a, colored.getSiblings().getFirst().getStyle().getColor().getValue());
    }

    @Test
    void name01ClampsInvalidHealthValuesToTheNameLength() {
        var colored = DogRenderer.colorTextWithHealth(Component.literal("Rover"), 20, 10);

        assertEquals("Rover", colored.getString());
        assertEquals(0x0aff43, colored.getStyle().getColor().getValue());
        assertEquals("", colored.getSiblings().getFirst().getString());
    }
}
