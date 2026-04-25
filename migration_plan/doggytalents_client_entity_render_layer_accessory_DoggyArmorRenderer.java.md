# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/layer/accessory/DoggyArmorRenderer.java`

Total Errors: 6

## Error: type argument Dog is not within bounds of type-variable S
- **Lines:** 31
- **Suggested Fix:** The `net.minecraft.client.renderer.entity.layers.RenderLayer` class is now parameterized by `S extends EntityRenderState` (or a subclass of it) and `M extends EntityModel<? super S>>`. The current class declaration uses `Dog` as the first type parameter, which is an entity, not an `EntityRenderState`.

    **Concrete Change:**
    Update the class declaration to use `DogRenderState` as the first type parameter.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.DogRenderState;

    // Original:
    // public class DoggyArmorRenderer extends RenderLayer<Dog, DogModel>  {
    // Proposed:
    public class DoggyArmorRenderer extends RenderLayer<DogRenderState, DogModel>  {
    ```

## Error: DoggyArmorRenderer is not abstract and does not override abstract method submit(PoseStack,SubmitNodeCollector,int,Dog,float,float) in RenderLayer
- **Lines:** 31
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
    import net.minecraft.util.ARGB;
    import net.minecraft.client.model.Model;
    import net.minecraft.resources.Identifier;
    import net.minecraft.core.component.DataComponents;
    import com.mojang.blaze3d.vertex.VertexConsumer;

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

        // Adapt checkAndRenderSlot calls
        checkAndRenderSlot(dog, EquipmentSlot.HEAD, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.CHEST, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.LEGS, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
        checkAndRenderSlot(dog, EquipmentSlot.FEET, poseStack, submitNodeCollector, lightCoords, red, green, blue, alpha);
    }

    // Helper method to adapt checkAndRenderSlot
    private void checkAndRenderSlot(Dog dog, EquipmentSlot slot, PoseStack stack, SubmitNodeCollector collector, int light, float red, float green, float blue, float alpha) {
        var itemStack = dog.getItemBySlot(slot);
        var item = itemStack.getItem();

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
            startRenderAlternativeModelFromRoot(altModel.get(), dog, stack, collector, light, red, green, blue, alpha, itemStack);
            return;
        }

        renderArmorCutout(this.model, DoggyArmorMapping.getMappedResource(itemStack.getItem(), dog, itemStack), stack, collector, light, dog, red, green, blue, alpha);

        // Trim rendering removed: ArmorTrim.outerTexture(material) no longer exists in 26.1.2
        // TODO: re-implement trim rendering using EquipmentLayerRenderer when available

        if (itemStack.hasFoil())
            renderGlint(stack, collector, light, this.model);
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
- **Lines:** 52
- **Suggested Fix:** The `render` method with the signature `render(PoseStack, MultiBufferSource, int, Dog, float, float, float, float, float, float)` is no longer the primary rendering method to override in `RenderLayer`. It has been replaced by the `submit` method.

    **Concrete Change:**
    Remove the old `render` method entirely. Its logic should be adapted and moved into the new `submit` method.

## Error: cannot find symbol
- **Lines:** 166, 171, 176
- **Suggested Fix:** These errors are related to `renderArmorCutout`, `renderGlint`, and `startRenderAlternativeModelFromRoot`. These helper methods need to be updated to align with the new rendering pipeline, specifically by accepting a `SubmitNodeCollector` and individual color components (`red`, `green`, `blue`, `alpha`) instead of `MultiBufferSource` and a single `int` color.

    **Concrete Change:**
    Update the signatures and internal logic of `renderArmorCutout`, `renderGlint`, and `startRenderAlternativeModelFromRoot` as shown in the example `submit` method adaptation above. Ensure `DataComponents.EQUIPPABLE` is imported.