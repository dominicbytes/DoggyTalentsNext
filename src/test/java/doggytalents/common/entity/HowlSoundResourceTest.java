package doggytalents.common.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonParser;

class HowlSoundResourceTest {

    @Test
    void audio01PackagesRegisteredHowlSounds() throws Exception {
        var loader = HowlSoundResourceTest.class.getClassLoader();
        try (var soundsStream = loader.getResourceAsStream("assets/doggytalents/sounds.json")) {
            assertNotNull(soundsStream);
            var sounds = JsonParser.parseReader(
                new InputStreamReader(soundsStream, StandardCharsets.UTF_8)).getAsJsonObject();
            assertTrue(sounds.has("dog_classic_howl1"));
            assertTrue(sounds.has("dog_classic_howl2"));
        }

        assertNotNull(loader.getResource("assets/doggytalents/sounds/wolf/classic_howl1.ogg"));
        assertNotNull(loader.getResource("assets/doggytalents/sounds/wolf/classic_howl2.ogg"));
    }
}
