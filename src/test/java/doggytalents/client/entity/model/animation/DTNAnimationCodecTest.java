package doggytalents.client.entity.model.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.animation.AnimationChannel;
import org.junit.jupiter.api.Test;

class DTNAnimationCodecTest {

    @Test
    void animationFormat01UsesBlockbenchPositionCoordinates() {
        var source = JsonParser.parseString("""
            {
              "length": 1.0,
              "channels": [{
                "part": "body",
                "type": "position",
                "keyframes": [{
                  "at": 0.0,
                  "value": [2.0, 3.0, 4.0],
                  "interp": "linear"
                }]
              }]
            }
            """);

        var animation = DTNAnimationCodec.CODEC.parse(JsonOps.INSTANCE, source).getOrThrow();
        var position = animation.boneAnimations().get("body").stream()
            .filter(channel -> channel.target() == AnimationChannel.Targets.POSITION)
            .findFirst()
            .orElseThrow()
            .keyframes()[0]
            .preTarget();

        assertEquals(-2.0F, position.x());
        assertEquals(-3.0F, position.y());
        assertEquals(4.0F, position.z());

        JsonObject encoded = DTNAnimationCodec.CODEC.encodeStart(JsonOps.INSTANCE, animation)
            .getOrThrow()
            .getAsJsonObject();
        JsonArray value = encoded.getAsJsonArray("channels")
            .get(0).getAsJsonObject()
            .getAsJsonArray("keyframes")
            .get(0).getAsJsonObject()
            .getAsJsonArray("value");

        assertEquals(2.0F, value.get(0).getAsFloat());
        assertEquals(3.0F, value.get(1).getAsFloat());
        assertEquals(4.0F, value.get(2).getAsFloat());
    }

    @Test
    void animationFormat01BundledAnimationsDecodeAndEncode() throws Exception {
        var resource = getClass().getResource(
            "/assets/doggytalents/doggytalents/dog_animations/howl.json");
        assertNotNull(resource, "bundled howl animation is missing");
        var animationDirectory = Path.of(resource.toURI()).getParent();

        try (var paths = Files.list(animationDirectory)) {
            var animations = paths
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted()
                .toList();

            assertFalse(animations.isEmpty(), "bundled animation inventory is empty");
            for (var path : animations) {
                try (var reader = Files.newBufferedReader(path)) {
                    var animation = DTNAnimationCodec.CODEC
                        .parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                        .getOrThrow();
                    DTNAnimationCodec.CODEC.encodeStart(JsonOps.INSTANCE, animation).getOrThrow();
                }
            }
        }
    }
}
