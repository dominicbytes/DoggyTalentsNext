package doggytalents.client.block.model;

import doggytalents.common.util.DogBedUtil;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class DogBedItemOverride {

    public BlockStateModelPart resolve(DogBedModel model, ItemStack stack) {
        var materials = DogBedUtil.getMaterials(stack);
        return model.getModelPart(materials.getLeft(), materials.getRight(), Direction.NORTH);
    }
}
