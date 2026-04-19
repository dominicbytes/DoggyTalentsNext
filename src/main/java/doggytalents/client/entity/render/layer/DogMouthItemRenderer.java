package doggytalents.client.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import doggytalents.DoggyTalents;
import doggytalents.api.inferface.IThrowableItem;
import doggytalents.client.ClientSetup;
import doggytalents.client.entity.model.SyncedRenderFunctionWithHeadModel;
import doggytalents.client.entity.model.dog.DogModel;
import doggytalents.client.entity.render.DogRenderState;
import doggytalents.common.config.ConfigHandler;
import doggytalents.common.entity.Dog;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;

public class DogMouthItemRenderer extends RenderLayer<DogRenderState, DogModel> {

    private ItemInHandRenderer itemInHandRenderer;
    private SyncedRenderFunctionWithHeadModel itemSyncer;

    public DogMouthItemRenderer(RenderLayerParent<DogRenderState, DogModel> dogRendererIn, EntityRendererProvider.Context ctx) {
        super(dogRendererIn);
        this.itemInHandRenderer = ctx.getItemInHandRenderer();
        itemSyncer = new SyncedRenderFunctionWithHeadModel(ctx.bakeLayer(ClientSetup.DOG_SYNCED_FUNCTION_WITH_HEAD));
    }

    @Override
    public void submit(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float yRot, float xRot) {
        Dog dog = state.dog;
        if (dog == null) return;

        if (!ConfigHandler.CLIENT.MOUTH_ITEM_FORCE_RENDER.get()) {
            var skin = dog.getClientSkin();
            if (skin.useCustomModel()) {
                var model = skin.getCustomModel().getValue();
                if (!model.armorShouldRender(dog))
                    return;
            }
        }

        var stackOptional = dog.getMouthItemForRender();
        if (!stackOptional.isPresent())
            return;
        var stack = stackOptional.get();

        var model = this.getParentModel();

        model.copyPropertiesTo(itemSyncer);
        itemSyncer.sync(model);

        // TODO: ItemInHandRenderer needs MultiBufferSource which is no longer available here.
        // For now, we stub this out - proper implementation requires SubmitNodeCollector item support.
        // itemSyncer.startRenderFromRoot(matrixStack, matrixStack1 -> {
        //     renderItem(matrixStack1, submitNodeCollector, lightCoords, dog, stack);
        // });
    }

    public void renderItem(PoseStack stack, MultiBufferSource bufferSource, int packedLight, Dog dog, ItemStack itemStack) {
        stack.pushPose();
        stack.translate(-0.025F, 0.125F, -0.32F);
        var item = itemStack.getItem();

        if (itemStack.has(DataComponents.WEAPON) || itemStack.has(DataComponents.TOOL)
            || itemStack.is(Items.TRIDENT)) {
            stack.translate(0.25, 0, 0);
        }
        if (item instanceof BowItem || item instanceof CrossbowItem) {
            stack.scale(1, -1, -1);
            stack.translate(0, 0, -0.1);
        }
        if (item instanceof BlockItem) {
            stack.scale(0.5f, -0.5f, -0.5f);
            stack.translate(0.2f, -0.31f, 0.07f);
            stack.mulPose(Axis.YP.rotationDegrees(60.0F));
        } else {
            stack.mulPose(Axis.YP.rotationDegrees(45.0F));
            stack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }

        this.itemInHandRenderer.renderItem(dog, itemStack, ItemDisplayContext.GROUND, false, stack, bufferSource, packedLight);
        stack.popPose();
    }
}
