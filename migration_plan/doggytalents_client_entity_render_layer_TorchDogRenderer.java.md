# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/TorchDogRenderer.java`

Total Errors: 4

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 18
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class TorchDogRenderer extends RenderLayer<Dog, DogModel> {
    // Proposed:
    public class TorchDogRenderer extends RenderLayer<DogRenderState, DogModel> {
    ```

## Error: TorchDogRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 18
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class now has a new abstract method `submit` that must be implemented. The signature of this method is `public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float red, float green, float blue, float alpha)`.

    **Concrete Change:**
    Implement the new `submit` method. The logic from the old `render` method should be adapted and moved into this new `submit` method.

    **Example:**
    ```java
    // Add imports:
    import net.minecraft.client.renderer.SubmitNodeCollector;
    import net.minecraft.client.renderer.texture.OverlayTexture;
    import net.minecraft.client.renderer.RenderType;
    import com.mojang.blaze3d.vertex.VertexConsumer;
    import net.minecraft.resources.Identifier;
    import doggytalents.common.config.ConfigHandler;
    import doggytalents.common.entity.Dog;
    import doggytalents.common.talent.DoggyTorchTalent;
    import doggytalents.DoggyTalents;
    import doggytalents.common.lib.Resources;
    import java.util.Optional;

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        Dog dog = state.dog;
        if (dog == null || dog.isInvisible()) {
            return;
        }

        var inst = dog.getTalent(DoggyTalents.DOGGY_TORCH)
            .map(x -> x.cast(DoggyTorchTalent.class));
        if (!inst.isPresent())
            return;
        if (!inst.get().canRenderTorch())
            return;
        if (!inst.get().renderTorch())
            return;
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(dog, state.walkAnimPos, state.walkAnimSpeed, state.ageInTicksForAnim);
        this.model.setupAnim(dog, state.walkAnimPos, state.walkAnimSpeed, state.ageInTicksForAnim, state.headYawForAnim, state.headPitchForAnim);

        Identifier res = Resources.TORCH_DOG;
        int renderLight = 15728880;
        if (dog.isDefeated()) {
            res = Resources.TORCH_DOG_UNLIT;
            renderLight = lightCoords; // Use lightCoords from submit method
        }
        // Updated call to renderColoredCutoutModel
        RenderLayer.renderColoredCutoutModel(this.model, res, poseStack, submitNodeCollector.getBuffer(RenderType.entityCutout(res)), renderLight, dog, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 27
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.

## Error: method renderColoredCutoutModel in class RenderLayer<S#2,M> cannot be applied to given types;
- **Lines:** 51
- **Suggested Fix:** The `RenderLayer.renderColoredCutoutModel` method signature has changed. It now expects individual color components (`red`, `green`, `blue`, `alpha`) instead of a single `int` color.

    **Concrete Change:**
    Update the call to `RenderLayer.renderColoredCutoutModel` to pass individual float color components.

    **Example:**
    ```java
    // Original:
    // RenderLayer.renderColoredCutoutModel(this.model, res, poseStack, buffer, renderLight, dog, 0xffffffff);
    // Proposed:
    RenderLayer.renderColoredCutoutModel(this.model, res, poseStack, submitNodeCollector.getBuffer(RenderType.entityCutout(res)), renderLight, dog, 1.0F, 1.0F, 1.0F, 1.0F);
    ```
    Note: The `buffer` parameter in the original call needs to be replaced with `submitNodeCollector.getBuffer(RenderType.entityCutout(res))` when adapting to the `submit` method.