package doggytalents.client.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import doggytalents.client.ClientSetup;
import doggytalents.client.block.model.RiceMillModel;
import doggytalents.common.block.RiceMillBlock;
import doggytalents.common.block.tileentity.RiceMillBlockEntity;
import doggytalents.common.lib.Resources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;

public class RiceMillRenderer implements BlockEntityRenderer<RiceMillBlockEntity, RiceMillRenderer.RiceMillRenderState> {

    private RiceMillModel model;

    public RiceMillRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new RiceMillModel(ctx.bakeLayer(ClientSetup.RICE_MILL));
    }

    public static class RiceMillRenderState extends BlockEntityRenderState {
        public float animProgress;
        public net.minecraft.core.Direction facing;
    }

    @Override
    public RiceMillRenderState createRenderState() {
        return new RiceMillRenderState();
    }

    @Override
    public void extractRenderState(RiceMillBlockEntity mill, RiceMillRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(mill, state, crumblingOverlay);
        state.animProgress = partialTick;
        var blockState = mill.getBlockState();
        state.facing = RiceMillBlock.getFacing(blockState);
    }

    @Override
    public void submit(RiceMillRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        // TODO: migrate to SubmitNodeCollector-based rendering
        // The full render is currently disabled pending render state migration
    }

    // Neoforge IBlockEntityRendererExtension
    public net.minecraft.world.phys.AABB getRenderBoundingBox(RiceMillBlockEntity mill) {
        return mill.getRenderBoundingBox();
    }

}
