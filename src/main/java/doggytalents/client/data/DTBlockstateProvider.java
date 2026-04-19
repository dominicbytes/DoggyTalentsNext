package doggytalents.client.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * TODO: DTBlockstateProvider needs to be rewritten for the 26.1 data generation system.
 * NeoForge's BlockStateProvider and model generator classes were removed/redesigned.
 * Blockstate models should now use the new MC data generation system via net.minecraft.client.data.models.
 */
public class DTBlockstateProvider implements DataProvider {

    public DTBlockstateProvider(PackOutput output) {
        // stub
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "DoggyTalents Blockstates (stub - needs rewrite for 26.1)";
    }
}
