# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/FisherDogRenderer.java`

Total Errors: 4

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 22
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class FisherDogRenderer extends RenderLayer<Dog, DogModel> {
    // Proposed:
    public class FisherDogRenderer extends RenderLayer<DogRenderState, DogModel> {
    ```

## Error: FisherDogRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 22
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
    import doggytalents.common.talent.FisherDogTalent;
    import doggytalents.DoggyTalents;
    import doggytalents.common.lib.Resources;
    import doggytalents.DoggyAccessoryTypes;
    import java.util.Optional;

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        Dog dog = state.dog;
        if (dog == null || dog.isInvisible()) {
            return;
        }

        var dogSkin = dog.getClientSkin();
        if (dogSkin.useCustomModel()) {
            var model = dogSkin.getCustomModel().getValue();
            if (!model.armorShouldRender(dog))
                return;
        }

        //dont render when hat is present
        var accessories = dog.getAccessories();
        for (var acc : accessories) {
            if (acc.getAccessory().getType() == DoggyAccessoryTypes.HEAD.get()) {
                return;
            }
        }

        Optional<FisherDogTalent> inst = dog.getTalent(DoggyTalents.FISHER_DOG)
            .map(x -> x.cast(FisherDogTalent.class));
        if (inst.isPresent() && inst.get().canRenderHat() && inst.get().renderHat()) {
            var dogModel = this.getParentModel();
            dogModel.copyPropertiesTo(this.model);
            this.model.sync(dogModel);

            // Updated call to renderColoredCutoutModel
            RenderLayer.renderColoredCutoutModel(this.model, Resources.FISHER_HAT, poseStack, submitNodeCollector.getBuffer(RenderType.entityCutout(Resources.FISHER_HAT)), lightCoords, dog, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 31
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.

## Error: method renderColoredCutoutModel in class RenderLayer<S#2,M> cannot be applied to given types;
- **Lines:** 59
- **Suggested Fix:** The `RenderLayer.renderColoredCutoutModel` method signature has changed. It now expects individual color components (`red`, `green`, `blue`, `alpha`) instead of a single `int` color.

    **Concrete Change:**
    Update the call to `RenderLayer.renderColoredCutoutModel` to pass individual float color components.

    **Example:**
    ```java
    // Original:
    // RenderLayer.renderColoredCutoutModel(this.model, Resources.FISHER_HAT, poseStack, buffer, packedLight, dog, 0xffffffff);
    // Proposed:
    RenderLayer.renderColoredCutoutModel(this.model, Resources.FISHER_HAT, poseStack, submitNodeCollector.getBuffer(RenderType.entityCutout(Resources.FISHER_HAT)), lightCoords, dog, 1.0F, 1.0F, 1.0F, 1.0F);
    ```
    Note: The `buffer` parameter in the original call needs to be replaced with `submitNodeCollector.getBuffer(RenderType.entityCutout(Resources.FISHER_HAT))` when adapting to the `submit` method.