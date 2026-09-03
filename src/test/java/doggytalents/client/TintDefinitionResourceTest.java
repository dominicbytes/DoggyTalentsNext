package doggytalents.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

class TintDefinitionResourceTest {

    @Test
    void item01DefinesEveryMigratedItemTint() throws Exception {
        for (var item : new String[] {
            "angel_wings", "baker_hat", "bowtie", "cape_coloured", "chef_hat",
            "flatcap", "flying_cape", "frisbee", "lab_coat", "wool_collar",
            "wool_collar_thicc"
        }) {
            assertTints(item, "minecraft:dye");
        }

        assertTints("wig", "minecraft:dye");
        assertTints("ceremonial_garb", "minecraft:constant", "minecraft:dye");
        assertTints("dog_plushie_toy_item", "minecraft:constant", "minecraft:dye");
        assertTints("midi_keyboard", "minecraft:constant", "minecraft:dye");
        assertTints("frisbee_wet", "minecraft:dye", "minecraft:constant");
        assertTints("dog_bath", "minecraft:constant");

        assertTintValue("wig", 0, "default", -14409443);
        assertTintValue("ceremonial_garb", 1, "default", -5495373);
        assertTintValue("dog_plushie_toy_item", 1, "default", 11546150);
        assertTintValue("midi_keyboard", 1, "default", -14277083);
        assertTintValue("dog_bath", 0, "value", -12618012);
        assertTintValue("dog_gift_costume", 0, "default", -5636096);
        assertTintValue("dog_gift_costume", 1, "default", -171);

        for (var item : new String[] {
            "birthday_hat", "dog_gift_costume", "doggy_contacts",
            "locator_orb_dyable", "striped_scarf"
        }) {
            assertTints(item,
                "doggytalents:double_dyable_bg", "doggytalents:double_dyable_fg");
        }
    }

    @Test
    void item01CustomTintSourcesUseEncodedDefaultForUnexpectedItems() {
        assertEquals(0xff123456,
            new DTNTintSources.DoubleDyableForeground(0xff123456)
                .calculate(net.minecraft.world.item.ItemStack.EMPTY, null, null));
        assertEquals(0xff654321,
            new DTNTintSources.DoubleDyableBackground(0xff654321)
                .calculate(net.minecraft.world.item.ItemStack.EMPTY, null, null));
    }

    private static void assertTints(String item, String... expectedTypes) throws Exception {
        JsonArray tints = readModel(item).getAsJsonArray("tints");
        assertNotNull(tints, item);
        assertEquals(expectedTypes.length, tints.size(), item);
        for (int i = 0; i < expectedTypes.length; ++i) {
            assertEquals(expectedTypes[i],
                tints.get(i).getAsJsonObject().get("type").getAsString(), item);
        }
    }

    private static void assertTintValue(String item, int index, String field, int expected)
            throws Exception {
        assertEquals(expected, readModel(item).getAsJsonArray("tints")
            .get(index).getAsJsonObject().get(field).getAsInt(), item);
    }

    private static com.google.gson.JsonObject readModel(String item) throws Exception {
        var resource = "assets/doggytalents/items/" + item + ".json";
        try (var stream = TintDefinitionResourceTest.class.getClassLoader()
                .getResourceAsStream(resource)) {
            assertNotNull(stream, resource);
            var root = JsonParser.parseReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            return root.getAsJsonObject("model");
        }
    }
}
