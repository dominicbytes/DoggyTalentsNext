package doggytalents.common.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import doggytalents.common.lib.Constants;
import net.neoforged.fml.ModList;

public class ForgeUtil {

    public static Optional<Path> getBundledModResource(String assetPath) {
        return getBundledModResource(Constants.MOD_ID, assetPath);
    }

    public static Optional<Path> getBundledModResource(String modId, String assetPath) {
        final var contents = ModList.get().getModFileById(modId).getFile().getContents();
        return contents.getContentRoots().stream()
            .map(root -> resolve(root, modId, assetPath))
            .filter(Files::exists)
            .findFirst();
    }

    private static Path resolve(Path root, String modId, String assetPath) {
        var path = root.resolve("assets").resolve(modId);
        for (var segment : assetPath.split("/")) {
            path = path.resolve(segment);
        }
        return path;
    }

}
