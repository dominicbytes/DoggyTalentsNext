package doggytalents.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import doggytalents.common.entity.misc.DogGunpowderProjectile;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DogGunpowderProjectileRenderer extends EntityRenderer<DogGunpowderProjectile, DogGunpowderProjectileRenderer.DogGunpowderRenderState> {

    public static class DogGunpowderRenderState extends EntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
    }

    private final ItemModelResolver itemModelResolver;
    private final ItemStack renderStack;

    public DogGunpowderProjectileRenderer(Context ctx) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelResolver();
        this.renderStack = new ItemStack(Items.GUNPOWDER);
    }

    @Override
    public DogGunpowderRenderState createRenderState() {
        return new DogGunpowderRenderState();
    }

    @Override
    public void extractRenderState(DogGunpowderProjectile entity, DogGunpowderRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        this.itemModelResolver.updateForNonLiving(state.item, renderStack, ItemDisplayContext.GROUND, entity);
    }

    @Override
    public Identifier getTextureLocation(DogGunpowderRenderState state) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void submit(DogGunpowderRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        stack.pushPose();
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        state.item.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        stack.popPose();
    }
}
