package doggytalents.common.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import net.neoforged.fml.jarcontents.JarContents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BundledResourceTest {
    private static final String ASSET = "doggytalents/dog_models/okami_amaterasu.json";
    private static final String ENTRY = "assets/doggytalents/" + ASSET;

    @TempDir
    Path directory;

    @Test
    void visualStartup01FindsPackedModel() throws Exception {
        var jar = directory.resolve("models.jar");
        try (var output = new JarOutputStream(Files.newOutputStream(jar));
             var input = getClass().getResourceAsStream("/" + ENTRY)) {
            output.putNextEntry(new JarEntry(ENTRY));
            input.transferTo(output);
            output.closeEntry();
        }
        try (var contents = JarContents.ofPath(jar)) {
            assertModelContents(contents);
            assertTrue(ForgeUtil.getBundledModResource(contents, "doggytalents", "missing.json").isEmpty());
        }
    }

    @Test
    void visualStartup01FindsExplodedModelAndRejectsMissingResource() throws Exception {
        var model = directory.resolve(ENTRY);
        Files.createDirectories(model.getParent());
        try (var input = getClass().getResourceAsStream("/" + ENTRY)) {
            Files.copy(input, model);
        }
        try (var contents = JarContents.ofPath(directory)) {
            assertModelContents(contents);
            assertTrue(ForgeUtil.getBundledModResource(contents, "doggytalents", "missing.json").isEmpty());
        }
    }

    private void assertModelContents(JarContents contents) throws Exception {
        var resource = ForgeUtil.getBundledModResource(contents, "doggytalents", ASSET).orElseThrow();
        try (var input = getClass().getResourceAsStream("/" + ENTRY)) {
            assertArrayEquals(input.readAllBytes(), resource.readAllBytes());
        }
    }
}
