package doggytalents.client.block.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;

class DogBedItemModelTest {

    @Test
    void itemModelCollectsUnculledAndEveryDirectionalQuadBucket() {
        List<Direction> requestedDirections = new ArrayList<>();
        BlockStateModelPart part = new BlockStateModelPart() {
            @Override
            public List<BakedQuad> getQuads(Direction direction) {
                requestedDirections.add(direction);
                return List.of();
            }

            @Override
            public boolean useAmbientOcclusion() {
                return true;
            }

            @Override
            public Material.Baked particleMaterial() {
                return null;
            }

            @Override
            public int materialFlags() {
                return 0;
            }
        };

        DogBedItemModel.collectQuads(part);

        assertEquals(7, requestedDirections.size());
        assertNull(requestedDirections.getFirst());
        assertEquals(List.of(Direction.values()), requestedDirections.subList(1, requestedDirections.size()));
    }
}
