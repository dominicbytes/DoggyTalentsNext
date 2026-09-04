package doggytalents.client.entity.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class DogSkinCatalogResourceTest {
    private static final String CATALOG =
        "/assets/doggytalents/textures/entity/dog/skin.json";
    private static final List<String> RETIRED_SKINS = List.of(
        "arcanine_shiro", "otter", "ammy_divine_rebirth", "borzoi_long");

    @Test
    void skinCatalog01MatchesCuratedCatalogAndModels() throws Exception {
        var resource = getClass().getResourceAsStream(CATALOG);
        assertNotNull(resource, "dog skin catalog is missing");

        try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            var skins = JsonParser.parseReader(reader).getAsJsonObject()
                .getAsJsonArray("dog_skins");
            assertEquals(141, skins.size(), "unexpected curated dog skin count");

            var ids = new HashSet<String>();
            for (var element : skins) {
                var skin = element.getAsJsonObject();
                var id = skin.get("skin_id").getAsString();
                var model = skin.get("use_model").getAsString();
                assertTrue(ids.add(id), () -> "duplicate dog skin id: " + id);
                assertNotNull(getClass().getResource(
                    "/assets/doggytalents/doggytalents/dog_models/" + model + ".json"),
                    () -> "missing dog model for skin " + id + ": " + model);
            }

            assertEquals("ammy_divine_mouthopen",
                skins.get(0).getAsJsonObject().get("skin_id").getAsString());
            assertEquals("ammy_divine_shiranui",
                skins.get(1).getAsJsonObject().get("skin_id").getAsString());
            assertEquals("wangwang", skins.get(2).getAsJsonObject().get("skin_id").getAsString());
            assertEquals("wangwang_mouthclosed",
                skins.get(3).getAsJsonObject().get("skin_id").getAsString());
            assertTrue(ids.containsAll(List.of("wangwang", "wangwang_mouthclosed")));
            assertNotNull(getClass().getResource(
                "/assets/doggytalents/textures/entity/dog/custom/wangwang.png"));
            assertNotNull(getClass().getResource(
                "/assets/doggytalents/textures/entity/dog/custom/wangwang_mouthclosed.png"));
            assertFalse(ids.stream().anyMatch(RETIRED_SKINS::contains));
        }
    }
}
