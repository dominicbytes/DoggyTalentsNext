# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/DogMouthItemRenderer.java`

Total Errors: 2

## Error: cannot find symbol
- **Lines:** 36
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class DogMouthItemRenderer extends RenderLayer<Dog, DogModel> {
    // Proposed:
    public class DogMouthItemRenderer extends RenderLayer<DogRenderState, DogModel> {
    ```
    This change will also require implementing the `submit` method from `RenderLayer`, as the old `render` method is no longer valid. The logic from the old `render` method should be adapted and moved into the new `submit` method.

## Error: method renderItem in class ItemInHandRenderer cannot be applied to given types;
- **Lines:** 93
- **Suggested Fix:** The `renderItem` method in `net.minecraft.client.renderer.ItemInHandRenderer` has changed its signature. It no longer takes a `boolean leftHanded` parameter and now expects a `SubmitNodeCollector` instead of a `MultiBufferSource`.

    **Concrete Change:**
    Update the `renderItem` method in `DogMouthItemRenderer` to accept `SubmitNodeCollector` instead of `MultiBufferSource`, and update the call to `this.itemInHandRenderer.renderItem` to match the new signature.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.client.renderer.SubmitNodeCollector;
    import net.minecraft.core.component.DataComponents; // For itemStack.has(DataComponents.WEAPON)

    // Original renderItem method signature:
    // public void renderItem(PoseStack stack, MultiBufferSource bufferSource, int packedLight, Dog dog, ItemStack itemStack) {
    // Proposed renderItem method signature:
    public void renderItem(PoseStack stack, SubmitNodeCollector submitNodeCollector, int packedLight, Dog dog, ItemStack itemStack) {
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

        // Updated call to renderItem:
        this.itemInHandRenderer.renderItem(dog, itemStack, ItemDisplayContext.GROUND, stack, submitNodeCollector, packedLight);
        stack.popPose();
    }
    ```
    The `submit` method (which `DogMouthItemRenderer` must implement as a `RenderLayer`) should call this adapted `renderItem` method, passing the `SubmitNodeCollector` it receives.