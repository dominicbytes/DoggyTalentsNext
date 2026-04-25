# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/accessory/IncapacitatedRenderer.java`

Total Errors: 7

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 32
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class IncapacitatedRenderer extends RenderLayer<Dog, DogModel> {
    // Proposed:
    public class IncapacitatedRenderer extends RenderLayer<DogRenderState, DogModel> {
    ```

## Error: IncapacitatedRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 32
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class now has a new abstract method `submit` that must be implemented. The signature of this method is `public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float red, float green, float blue, float alpha)`.

    **Concrete Change:**
    Implement the new `submit` method. The logic from the old `render` method should be adapted and moved into this new `submit` method. This will also require adapting the `renderTranslucentModel` method.

    **Example (conceptual, requires careful adaptation of original logic):**
    ```java
    // Add imports:
    import net.minecraft.client.renderer.SubmitNodeCollector;
    import net.minecraft.client.renderer.texture.OverlayTexture;
    import net.minecraft.client.renderer.RenderType;
    import com.mojang.blaze3d.vertex.VertexConsumer;
    import net.minecraft.resources.Identifier;
    import doggytalents.common.config.ConfigHandler;
    import doggytalents.common.entity.Dog;
    import doggytalents.common.entity.DogIncapacitatedMananger.IncapacitatedSyncState;
    import doggytalents.common.lib.Resources;
    import net.minecraft.client.renderer.entity.LivingEntityRenderer;
    import net.minecraft.util.ARGB;

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        Dog dog = state.dog;
        if (!dog.isTame() || dog.isInvisible()) {
            return;
        }

        if (!dog.isDefeated()) return;

        var skin = dog.getClientSkin();
        if (skin.useCustomModel()) {
            var model = skin.getCustomModel().getValue();
            if (!model.incapShouldRender(dog)) {
                return;
            }
        }
            
        if (!ConfigHandler.CLIENT.RENDER_INCAPACITATED_TEXTURE.get()) return;

        var dogModel = this.getParentModel();
        if (dogModel.useDefaultModelForAccessories()) {
            dogModel.copyPropertiesTo(defaultModel);
            defaultModel.copyFrom(dogModel);
            dogModel = defaultModel;
        }
        var sync_state = dog.getIncapSyncState();
        var texture_rl = pickInjuredTexture(dog, sync_state);
        if (texture_rl != null) {
            var current_alpha = getInjureOpascity(dog);
            renderTranslucentModel(dogModel, texture_rl, poseStack, submitNodeCollector, lightCoords, state, 1.0F, 1.0F, 1.0F, current_alpha);
        }
        //Bandaid layer
        var bandaid_state = sync_state.bandaid;
        Identifier bandaid_texture_rl = null;
        switch (bandaid_state) {
        case FULL:
            bandaid_texture_rl = Resources.BANDAID_OVERLAY_FULL;
            break;
        case HALF:
            bandaid_texture_rl = Resources.BANDAID_OVERLAY_HALF;
            break;
        default:
            break;
        }

        if (bandaid_texture_rl != null)
            renderTranslucentModel(dogModel, bandaid_texture_rl, poseStack, submitNodeCollector, lightCoords, state, 1.0F, 1.0F, 1.0F, 1);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 41
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.

## Error: method renderTranslucentModel in class IncapacitatedRenderer cannot be applied to given types;
- **Lines:** 71, 88
- **Suggested Fix:** The `renderTranslucentModel` method needs to be updated to align with the new rendering pipeline. It should accept `SubmitNodeCollector` instead of `MultiBufferSource`, and `DogRenderState` instead of `T extends LivingEntity`.

    **Concrete Change:**
    Update the `renderTranslucentModel` method signature and its internal call to `model.renderToBuffer`.

    **Original `renderTranslucentModel` signature:**
    ```java
    public static <T extends LivingEntity> void renderTranslucentModel(EntityModel<T> p_117377_, Identifier p_117378_, PoseStack p_117379_, MultiBufferSource p_117380_, int p_117381_, T p_117382_, float p_117383_, float p_117384_, float p_117385_, float opascity)
    ```

    **Proposed `renderTranslucentModel` signature and body:**
    ```java
    public static void renderTranslucentModel(DogModel model, Identifier texture, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        VertexConsumer vertexconsumer = submitNodeCollector.getBuffer(RenderType.entityTranslucent(texture));
        model.renderToBuffer(poseStack, vertexconsumer, lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), ARGB.colorFromFloat(alpha, red, green, blue));
    }
    ```

## Error: type argument T#1 is not within bounds of type-variable T#2
- **Lines:** 140
- **Suggested Fix:** This error is a cascading effect from the `renderTranslucentModel` method signature change. Once `renderTranslucentModel` is updated to use `DogRenderState` and `SubmitNodeCollector`, this error should resolve.

    **Concrete Change:** This error should be resolved by applying the fix for the `renderTranslucentModel` method.

## Error: incompatible types: T cannot be converted to LivingEntityRenderState
- **Lines:** 142
- **Suggested Fix:** The `LivingEntityRenderer.getOverlayCoords` method now expects `LivingEntityRenderState` as its first parameter, not a `LivingEntity`.

    **Concrete Change:**
    Within the `renderTranslucentModel` method, ensure that `LivingEntityRenderer.getOverlayCoords` is called with the `DogRenderState` object.

    **Example (within `renderTranslucentModel`):**
    ```java
    // Original:
    // LivingEntityRenderer.getOverlayCoords(p_117382_, 0.0F) // p_117382_ is T extends LivingEntity
    // Proposed:
    LivingEntityRenderer.getOverlayCoords(state, 0.0F) // Pass the DogRenderState object
    ```