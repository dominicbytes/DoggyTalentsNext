package doggytalents.client.entity.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import doggytalents.client.entity.model.util.DTNModelCodec;
import doggytalents.common.util.Util;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DTNModelFormatCompatibilityTest {
    private static final String DEFAULT_MODEL =
        "/assets/doggytalents/doggytalents/dog_models/default.json";

    @Test
    void modelFormat01AcceptsMissingPropsWithDefaults() {
        var json = JsonParser.parseString("""
            {
              "texture_size": [64, 32],
              "parts": []
            }
            """);

        var decoded = DTNModelCodec.DOG_MODEL_CODEC
            .parse(new Dynamic<>(JsonOps.INSTANCE, json))
            .getOrThrow();

        assertEquals(DTNModelCodec.DogModelProps.DEFAULT, decoded.getRight());
    }

    @Test
    void modelFormat01ReloadInvalidatesOnlyParsedModels() throws Exception {
        DogModelRegistry.init();
        var parsedId = Util.getResource("model_format_01_reloadable");
        var legacyId = Util.getResource("default");
        var legacyHolder = DogModelRegistry.getDogModelHolder(legacyId);

        try (var stream = getClass().getResourceAsStream(DEFAULT_MODEL)) {
            assertNotNull(stream, "bundled default dog model is missing");
            var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            var decoded = DTNModelCodec.DOG_MODEL_CODEC
                .parse(new Dynamic<>(JsonOps.INSTANCE, json))
                .getOrThrow();
            var registered = DogModelRegistry.registerParsed(parsedId, decoded.getLeft(), decoded.getRight());
            assertTrue(registered);
        }

        assertNotNull(DogModelRegistry.getDogModelHolder(parsedId));
        assertNotNull(legacyHolder);

        DogModelRegistry.invalidateAllParsed();

        assertNull(DogModelRegistry.getDogModelHolder(parsedId));
        assertSame(legacyHolder, DogModelRegistry.getDogModelHolder(legacyId));
    }
}
