package doggytalents.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class LootModifierResourceTest {
    private static final String LEGACY_INDEX =
        "/data/neoforge/loot_modifiers/global_loot_modifiers.json";

    @Test
    void loot01ProvidesSelfRegisteringRiceAndSoyModifiers() throws Exception {
        assertNull(getClass().getResource(LEGACY_INDEX),
            "26.1.2 treats the legacy global modifier index as a modifier definition");
        assertModifierType("rice_from_grass_modifier", "doggytalents:rice_from_grass");
        assertModifierType("soy_from_zombie_modifier", "doggytalents:soy_from_zombie");
    }

    private void assertModifierType(String name, String expectedType) throws Exception {
        var path = "/data/doggytalents/loot_modifiers/" + name + ".json";
        var resource = getClass().getResourceAsStream(path);
        assertNotNull(resource, "Loot modifier is missing: " + path);

        try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            var root = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals(expectedType, root.get("type").getAsString());
            assertNotNull(root.getAsJsonArray("conditions"));
        }
    }
}
