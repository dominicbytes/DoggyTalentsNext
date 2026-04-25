# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/accessory/DefaultAccessoryRenderer.java`

Total Errors: 16

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 29
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class DefaultAccessoryRenderer extends RenderLayer<Dog, DogModel>  {
    // Proposed:
    public class DefaultAccessoryRenderer extends RenderLayer<DogRenderState, DogModel>  {
    ```

## Error: DefaultAccessoryRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 29
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class now has a new abstract method `submit` that must be implemented. The signature of this method is `public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float red, float green, float blue, float alpha)`.

    **Concrete Change:**
    Implement the new `submit` method. The logic from the old `render` method should be adapted and moved into this new `submit` method. This will also require adapting helper methods like `checkAndRenderSlot`, `renderArmorCutout`, `renderGlint`, and `startRenderAlternativeModelFromRoot` to use `SubmitNodeCollector` and the new color parameters.

    **Example (conceptual, requires careful adaptation of original logic):**
    ```java
    // Add imports:
    import net.minecraft.client.renderer.SubmitNodeCollector;
    import net.minecraft.client.renderer.texture.OverlayTexture;
    import net.minecraft.client.renderer.RenderType;
    import net.minecraft.world.item.ItemStack;
    import net.minecraft.world.item.Items;
    import net.minecraft.world.item.ItemDisplayContext;
    import net.minecraft.world.entity.EquipmentSlot;
    import doggytalents.common.talent.TalentInstance;
    import doggytalents.DoggyTalents;
    import doggytalents.common.config.ConfigHandler;
    import doggytalents.common.entity.accessory.Accessory;
    import doggytalents.common.entity.accessory.AccessoryInstance;
    import doggytalents.common.entity.accessory.IColoredObject;
    import doggytalents.common.entity.accessory.AccessoryRenderType;
    import net.minecraft.util.ARGB; // Still used for color conversion
    import net.minecraft.client.model.Model;
    import net.minecraft.resources.Identifier;
    import net.minecraft.core.component.DataComponents; // New import for DataComponents

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, DogRenderState state, float red, float green, float blue, float alpha) {
        Dog dog = state.dog;
        if (!dog.isTame() || dog.isInvisible()) {
            return;
        }

        if (!ConfigHandler.CLIENT.RENDER_ARMOR.get() || dog.hideArmor())
            return;

        var skin = dog.getClientSkin();
        if (skin.useCustomModel()) {
            var model = skin.getCustomModel().getValue();
            if (!model.armorShouldRender(dog))
                return;
        }

        Optional<TalentInstance> inst = dog.getTalent(DoggyTalents.DOGGY_ARMOR);
        if (!inst.isPresent()) return;

        if (ConfigHandler.CLIENT.USE_LEGACY_DOG_ARMOR_RENDER.get())
            this.model = this.legacyModel;
        else
            this.model = this.newModel;

        var parentModel = this.getParentModel();
        parentModel.copyPropertiesTo(this.model);
        this.model.sync(parentModel);
        // initAltModel = false; // Assuming this field is handled elsewhere

        // Adapt checkAndRenderSlot calls to use submitNodeCollector and color parameters
        checkAndRenderSlot(dog, EquipmentSlot.HEAD, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.CHEST, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.LEGS, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.FEET, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
    }

    // Helper method to adapt checkAndRenderSlot
    private void checkAndRenderSlot(Dog dog, EquipmentSlot slot, PoseStack stack, SubmitNodeCollector collector, int light, float red, float green, float blue, float alpha) {
        var itemStack = dog.getItemBySlot(slot);
        var item = itemStack.getItem();

        // ArmorItem no longer exists in 26.1.2; use DataComponents.EQUIPPABLE instead
        var equippable = itemStack.getOrDefault(DataComponents.EQUIPPABLE, null);
        if (equippable == null) return;

        switch (slot) {
        case HEAD:
            this.model.setHelmet();
            break;
        case CHEST:
            this.model.setChestplate();
            break;
        case LEGS:
            this.model.setLeggings();
            break;
        case FEET:
            this.model.setBoot();
            break;
        default:
            return;
        }

        var altModel = getAlternativeArmorModel(dog, slot, stack, itemStack);
        if (altModel.isPresent()) {
            // if (!initAltModel) { ... } // Assuming initAltModel is handled
            startRenderAlternativeModelFromRoot(altModel.get(), dog, stack, collector, light, red, green, blue, alpha, itemStack); // Adapt this call
            return;
        }

        renderArmorCutout(this.model, DoggyArmorMapping.getMappedResource(itemStack.getItem(), dog, itemStack), stack, collector, light, dog, red, green, blue, alpha);

        // Trim rendering removed: ArmorTrim.outerTexture(material) no longer exists in 26.1.2
        // TODO: re-implement trim rendering using EquipmentLayerRenderer when available

        if (itemStack.hasFoil())
            renderGlint(stack, collector, light, this.model); // Adapt renderGlint
    }

    // Adapt renderArmorCutout
    public static void renderArmorCutout(DogArmorModel model, Identifier texture, PoseStack stack, SubmitNodeCollector collector, int light, Dog dog, float red, float green, float blue, float alpha) {
        VertexConsumer vertexconsumer = collector.getBuffer(RenderType.armorCutoutNoCull(texture));
        model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    // Adapt renderGlint
    private void renderGlint(PoseStack stack, SubmitNodeCollector collector, int light, DogArmorModel model) {
        VertexConsumer vertexconsumer = collector.getBuffer(RenderType.armorEntityGlint());
        model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    // Adapt startRenderAlternativeModelFromRoot
    private void startRenderAlternativeModelFromRoot(Model model, Dog dog, PoseStack stack, SubmitNodeCollector collector, int light, float red, float green, float blue, float alpha, ItemStack itemStack) {
        // This method needs to be adapted based on how alternative models are rendered.
        // It will likely involve calling model.renderToBuffer with the new parameters.
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 41
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.

## Error: incompatible types: DogModel cannot be converted to SyncedAccessoryModel
- **Lines:** 88, 93, 128, 133
- **Suggested Fix:** This error indicates a type mismatch where a `DogModel` is being used in a context that expects a `SyncedAccessoryModel`. This is likely due to changes in how models are handled or passed around.

    **Concrete Change:**
    Review the usage of `dogModel` in these lines. If `dogModel` is intended to be a `SyncedAccessoryModel`, ensure it is cast correctly or that the `SyncedAccessoryModel` is being used where appropriate. If `dogModel` is a `DogModel`, then the method being called (e.g., `copyFrom`) might have changed its expected parameter type.

    **Example (Line 88):**
    ```java
    // Original:
    // dogModel.copyFrom(parentModel);
    // Proposed:
    // If parentModel is a DogModel and copyFrom expects DogModel:
    // dogModel.copyFrom(parentModel); // This should be fine if copyFrom is updated
    // If copyFrom expects SyncedAccessoryModel, then a cast or different approach is needed.
    ```
    Further investigation into `DogModel.copyFrom` and `SyncedAccessoryModel` is needed to provide a precise fix.

## Error: incompatible types: cannot infer type-variable(s) S
- **Lines:** 90, 130, 149
- **Suggested Fix:** These errors are likely cascading effects from the `RenderLayer` class declaration change and the `renderColoredCutoutModel` method signature change. Once the class declaration is updated and the `submit` method is correctly implemented, these errors should resolve.

    **Concrete Change:** This error should be resolved by applying the fix for the "type argument Dog is not within bounds of type-variable S" error and implementing the `submit` method.

## Error: method renderColoredCutoutModel in class RenderLayer<S#2,M> cannot be applied to given types;
- **Lines:** 90, 95, 130, 135, 149, 154
- **Suggested Fix:** The `RenderLayer.renderColoredCutoutModel` method signature has changed. It now expects individual color components (`red`, `green`, `blue`, `alpha`) instead of a single `int` color.

    **Concrete Change:**
    Update calls to `RenderLayer.renderColoredCutoutModel` to pass individual float color components.

    **Example (Line 90):**
    ```java
    // Original:
    // RenderLayer.renderColoredCutoutModel(dogModel, texture_rl, poseStack, buffer, packedLight, dog, ARGB.colorFromFloat(1, color[0], color[1], color[2]));
    // Proposed:
    RenderLayer.renderColoredCutoutModel(dogModel, texture_rl, poseStack, buffer, packedLight, dog, color[0], color[1], color[2], 1.0F);
    ```
    Note: `ARGB.colorFromFloat` is likely no longer needed for this method.