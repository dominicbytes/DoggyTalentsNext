package doggytalents.client.entity.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import doggytalents.client.entity.model.util.DTNModelCodec;
import doggytalents.client.entity.model.util.MutableParsedModel;
import doggytalents.common.util.Util;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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

    @Test
    void modelFormat02ExtractsTranslucentBranchesWithHeadlessParents() {
        var json = JsonParser.parseString("""
            {
              "texture_size": [16, 16],
              "parts": [{
                "id": "root",
                "cubes": [{"uv": [0, 0], "from": [0, 0, 0], "to": [1, 1, 1]}],
                "children": [{
                  "id": "overlay",
                  "props": {"translucent": true},
                  "cubes": [{"uv": [0, 0], "from": [0, 0, 0], "to": [1, 1, 1]}],
                  "children": [{
                    "id": "overlay_child",
                    "cubes": [{"uv": [0, 0], "from": [0, 0, 0], "to": [1, 1, 1]}]
                  }]
                }, {
                  "id": "overlay_second",
                  "props": {"translucent": true},
                  "cubes": [{"uv": [0, 0], "from": [0, 0, 0], "to": [1, 1, 1]}]
                }]
              }]
            }
            """);
        var result = DTNModelCodec.CODEC
            .parse(new Dynamic<>(JsonOps.INSTANCE, json))
            .getOrThrow();
        var tracker = MutableParsedModel.create();

        DTNModelCodec.layerDefinitionFromParsed(result, Optional.of(tracker));

        assertFalse(tracker.isEmpty());
        var extracted = tracker.buildHeadlessCopyFrom(result).orElseThrow();
        var headlessRoot = extracted.parts().getFirst();
        assertTrue(headlessRoot.cubeList().isEmpty());
        var overlay = headlessRoot.children().getFirst();
        assertTrue(overlay.props().translucent());
        assertEquals(1, overlay.cubeList().size());
        assertEquals("overlay_child", overlay.children().getFirst().id());
        assertEquals(1, overlay.children().getFirst().cubeList().size());
        assertEquals("overlay_second", headlessRoot.children().get(1).id());
    }

    @Test
    void modelFormat02BundledAmaterasuModelsDeclareTranslucentParts() throws Exception {
        assertTrue(countTranslucentParts("/assets/doggytalents/doggytalents/dog_models/okami_amaterasu.json") > 0);
        assertTrue(countTranslucentParts("/assets/doggytalents/doggytalents/dog_models/ammy_divine_shiranui.json") > 0);
    }

    private int countTranslucentParts(String resource) throws Exception {
        try (var stream = getClass().getResourceAsStream(resource)) {
            assertNotNull(stream, "bundled dog model is missing: " + resource);
            var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            var result = DTNModelCodec.CODEC
                .parse(new Dynamic<>(JsonOps.INSTANCE, json))
                .getOrThrow();
            return result.parts().stream().mapToInt(this::countTranslucentParts).sum();
        }
    }

    private int countTranslucentParts(DTNModelCodec.ParsedPart part) {
        return (part.props().translucent() ? 1 : 0)
            + part.children().stream().mapToInt(this::countTranslucentParts).sum();
    }
}
