package doggytalents.client.entity.model.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import doggytalents.api.anim.DogAnimation;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.client.animation.AnimationDefinition;
import org.junit.jupiter.api.Test;

class DogAnimationRegistryTest {

    @Test
    void animationRegistry01ReservesWalkingAnimationIdsAndMetadata() {
        var slowTrot = DogAnimation.byId(94);
        var gallop = DogAnimation.byId(95);

        assertEquals("SLOW_TROT", slowTrot.name());
        assertEquals(94, slowTrot.getId());
        assertEquals(20, slowTrot.getLengthTicks());
        assertTrue(slowTrot.freeHead());

        assertEquals("GALLOP", gallop.name());
        assertEquals(95, gallop.getId());
        assertEquals(10, gallop.getLengthTicks());
        assertTrue(gallop.freeHead());
    }

    @Test
    void animationRegistry01WalkingEntriesHaveBundledResources() throws Exception {
        assertNotNull(loadAnimation(DogAnimation.SLOW_TROT));
        assertNotNull(loadAnimation(DogAnimation.GALLOP));
    }

    private AnimationDefinition loadAnimation(DogAnimation animation) throws Exception {
        var id = animation.name().toLowerCase(Locale.ROOT);
        var resource = "/assets/doggytalents/doggytalents/dog_animations/" + id + ".json";
        try (var stream = getClass().getResourceAsStream(resource)) {
            assertNotNull(stream, "bundled walking animation is missing: " + resource);
            var json = JsonParser.parseReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
            return DTNAnimationCodec.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        }
    }
}
