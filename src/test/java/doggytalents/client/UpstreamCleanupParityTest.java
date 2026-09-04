package doggytalents.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import doggytalents.client.screen.framework.types.TextType;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpstreamCleanupParityTest {
    private static final List<String> REMOVED_NAMES = List.of(
        "Gigue", "Giocoso", "Humoresque", "Leitmotif", "Malagueña",
        "Pentatonic", "Pitch", "Più", "Riguadon");

    @Test
    void upstreamCleanup01UsesOnlyImplementedButtonAlignments() {
        assertEquals(List.of("LEFT", "MIDDLE"),
            List.of(TextType.Align.values()).stream().map(Enum::name).toList());
    }

    @Test
    void upstreamCleanup02UsesDashiesCuratedNamesAndKeepsWangWang() throws Exception {
        var resource = getClass().getResourceAsStream(
            "/assets/doggytalents/dogname/name.json");
        assertNotNull(resource, "dog name catalog is missing");

        try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            var entries = JsonParser.parseReader(reader).getAsJsonArray();
            var names = entries.asList().stream().map(e -> e.getAsString()).toList();
            REMOVED_NAMES.forEach(name -> assertFalse(names.contains(name), name));
            assertTrue(names.contains("Cantabile"));
            assertTrue(names.contains("Callie"));
            assertTrue(names.contains("Wang Wang"));
            assertTrue(names.contains("旺旺"));
        }
    }

    @Test
    void upstreamCleanup03OmitsWithdrawnFrenchTranslation() {
        assertNull(getClass().getResource("/assets/doggytalents/lang/fr_fr.json"));
    }
}
