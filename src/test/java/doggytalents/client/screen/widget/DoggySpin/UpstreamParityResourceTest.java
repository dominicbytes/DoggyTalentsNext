package doggytalents.client.screen.widget.DoggySpin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import doggytalents.DoggyAccessories;
import doggytalents.DoggyItems;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpstreamParityResourceTest {
    private static final List<String> DISABLED_ITEMS = List.of(
        "plague_doctor_mask",
        "dragon_costume_head",
        "dragon_costume_suit",
        "dragon_costume_wings",
        "sussy_sickle");

    @Test
    void upstreamDisable01RemovesRegistrationsAndGeneratedResources() {
        for (var id : DISABLED_ITEMS) {
            assertThrows(NoSuchFieldException.class,
                () -> DoggyItems.class.getDeclaredField(toFieldName(id)),
                () -> "item remains registered: " + id);
            assertNull(getClass().getResource("/assets/doggytalents/items/" + id + ".json"),
                () -> "item definition remains bundled: " + id);
            assertNull(getClass().getResource("/assets/doggytalents/models/item/" + id + ".json"),
                () -> "item model remains bundled: " + id);
            assertNull(getClass().getResource("/data/doggytalents/recipe/" + id + ".json"),
                () -> "recipe remains bundled: " + id);
            var recipeGroup = id.equals("sussy_sickle") ? "combat" : "decorations";
            assertNull(getClass().getResource(
                    "/data/doggytalents/advancement/recipes/" + recipeGroup + "/" + id + ".json"),
                () -> "recipe advancement remains bundled: " + id);
        }

        for (var id : DISABLED_ITEMS.subList(0, 4)) {
            assertThrows(NoSuchFieldException.class,
                () -> DoggyAccessories.class.getDeclaredField(toAccessoryFieldName(id)),
                () -> "accessory remains registered: " + id);
        }
    }

    @Test
    void doggySpin01LoadsBundledCustomModelsAndIncludesWangWang() throws Exception {
        assertNotNull(DoggySpinModel.Style.valueOf("WANG_WANG"));
        assertNotNull(getClass().getResource(
            "/assets/doggytalents/doggytalents/dog_models/okami_amaterasu.json"));
        assertNotNull(getClass().getResource(
            "/assets/doggytalents/doggytalents/dog_models/sol_hope.json"));
        assertNotNull(getClass().getResource(
            "/assets/doggytalents/doggytalents/dog_models/wangwang.json"));
        assertNotNull(getClass().getResource(
            "/assets/doggytalents/textures/entity/dog/custom/wangwang.png"));
        assertThrows(ClassNotFoundException.class,
            () -> Class.forName(getClass().getPackageName() + ".AmaterasuModel"));
        assertThrows(ClassNotFoundException.class,
            () -> Class.forName(getClass().getPackageName() + ".HopeModel"));

        var resource = getClass().getResourceAsStream(
            "/assets/doggytalents/dogname/name.json");
        assertNotNull(resource, "dog name catalog is missing");
        try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            var names = JsonParser.parseReader(reader).getAsJsonArray();
            assertTrue(names.asList().stream().anyMatch(e -> e.getAsString().equals("Wang Wang")));
            assertTrue(names.asList().stream().anyMatch(e -> e.getAsString().equals("旺旺")));
        }
    }

    private static String toFieldName(String id) {
        return id.toUpperCase();
    }

    private static String toAccessoryFieldName(String id) {
        return id.equals("plague_doctor_mask") ? "PLAGUE_DOC_MASK" : toFieldName(id);
    }
}
