package doggytalents.client.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import doggytalents.client.entity.model.dog.DogModel;
import doggytalents.client.entity.render.DogRenderState;
import doggytalents.client.entity.render.layer.accessory.DefaultAccessoryRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class DogModelTranslucentOverrideRenderer extends RenderLayer<DogRenderState, DogModel> {

    public DogModelTranslucentOverrideRenderer(
        RenderLayerParent<DogRenderState, DogModel> parentRenderer,
        EntityRendererProvider.Context ctx) {
        super(parentRenderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
        DogRenderState renderState, float yRot, float xRot) {
        if (renderState.isInvisible || renderState.dog == null || renderState.skinTexture == null)
            return;

        final var dog_model = this.getParentModel();
        final var translucent_model = dog_model.getTranslucentOverride();
        if (translucent_model == null)
            return;

        dog_model.copyPropertiesTo(translucent_model);
        translucent_model.sync(getParentModel());
        DefaultAccessoryRenderer.renderTranslucentModel(translucent_model, renderState.skinTexture,
            poseStack, submitNodeCollector, packedLight, renderState, 1f, 1f, 1f, 1f);
    }

}
