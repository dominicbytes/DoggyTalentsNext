package doggytalents.client.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * TODO: DTItemModelProvider needs to be rewritten for the 26.1 data generation system.
 * NeoForge's ItemModelProvider and model generator classes were removed/redesigned.
 * Item models should now use the new MC data generation system via net.minecraft.client.data.models.
 */
public class DTItemModelProvider implements DataProvider {

    public DTItemModelProvider(PackOutput output) {
        // stub
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "DoggyTalents Item Models (stub - needs rewrite for 26.1)";
    }
}
