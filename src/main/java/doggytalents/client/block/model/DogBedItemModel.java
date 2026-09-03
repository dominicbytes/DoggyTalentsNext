package doggytalents.client.block.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DogBedItemModel implements ItemModel {

    private final DogBedModel model;
    private final ModelRenderProperties properties;
    private final DogBedItemOverride override = new DogBedItemOverride();

    public DogBedItemModel(DogBedModel model, ModelRenderProperties properties) {
        this.model = model;
        this.properties = properties;
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver,
            ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
        renderState.appendModelIdentityElement(this);

        ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
        if (stack.hasFoil()) {
            layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            renderState.setAnimated();
            renderState.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        BlockStateModelPart part = override.resolve(model, stack);
        List<BakedQuad> quads = collectQuads(part);
        layer.setExtents(() -> CuboidItemModelWrapper.computeExtents(quads));
        properties.applyToLayer(layer, displayContext);
        layer.prepareQuadList().addAll(quads);
    }

    static List<BakedQuad> collectQuads(BlockStateModelPart part) {
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(part.getQuads(null));
        for (Direction direction : Direction.values()) {
            quads.addAll(part.getQuads(direction));
        }
        return quads;
    }
}
