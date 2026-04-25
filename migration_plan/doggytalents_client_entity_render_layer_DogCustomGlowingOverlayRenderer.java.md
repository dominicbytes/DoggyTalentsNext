# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/DogCustomGlowingOverlayRenderer.java`

Total Errors: 3

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 16
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class DogCustomGlowingOverlayRenderer extends RenderLayer<Dog, DogModel> {
    // Proposed:
    public class DogCustomGlowingOverlayRenderer extends RenderLayer<DogRenderState, DogModel> {
    ```

## Error: DogCustomGlowingOverlayRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 16
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class now has a new abstract method `submit` that must be implemented. The signature of this method is `public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float red, float green, float blue, float alpha)`.

    **Concrete Change:**
    Implement the new `submit` method. The logic from the old `render` method should be adapted and moved into this new `submit` method.

    **Example:**
    ```java
    // Add imports:
    import net.minecraft.client.renderer.SubmitNodeCollector;
    import net.minecraft.client.renderer.texture.OverlayTexture;
    import net.minecraft.client.renderer.RenderType; // Assuming RenderTypes is now RenderType
    import com.mojang.blaze3d.vertex.VertexConsumer; // Needed for getBuffer

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        if (state.dog == null || state.dog.isInvisible())
            return;
        
        var skin = state.dog.getClientSkin();
        if (!skin.isCustom() || !skin.hasGlowingOverlay())
            return;
        
        var glow_layer = skin.getGlowingOverlay().get();
        // Adapt to SubmitNodeCollector: getBuffer from submitNodeCollector
        VertexConsumer vertexconsumer = submitNodeCollector.getBuffer(RenderType.entityTranslucent(glow_layer));
        this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 22
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.