package doggytalents.common.util;

import java.util.Optional;

import doggytalents.common.lib.Constants;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.jarcontents.JarResource;

public class ForgeUtil {

    public static Optional<JarResource> getBundledModResource(String assetPath) {
        return getBundledModResource(Constants.MOD_ID, assetPath);
    }

    public static Optional<JarResource> getBundledModResource(String modId, String assetPath) {
        final var contents = ModList.get().getModFileById(modId).getFile().getContents();
        return getBundledModResource(contents, modId, assetPath);
    }

    static Optional<JarResource> getBundledModResource(JarContents contents, String modId, String assetPath) {
        return Optional.ofNullable(contents.get("assets/" + modId + "/" + assetPath));
    }

}
