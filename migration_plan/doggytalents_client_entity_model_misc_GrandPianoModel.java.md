# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/model/misc/GrandPianoModel.java`

Total Errors: 4

## Error: type argument Piano is not within bounds of type-variable T
- **Lines:** 16
- **Suggested Fix:** The `net.minecraft.client.model.EntityModel` class is now parameterized by `T extends EntityRenderState` (or a subclass of it), not directly by the entity type. `GrandPianoModel` should be parameterized with `PianoRenderState`.

    **Concrete Change:**
    Update the class declaration to use `PianoRenderer.PianoRenderState`.

    **Example:**
    ```java
    // Add import:
    import doggytalents.client.entity.render.misc.PianoRenderer.PianoRenderState;

    // Original:
    // public class GrandPianoModel extends EntityModel<Piano> {
    // Proposed:
    public class GrandPianoModel extends EntityModel<PianoRenderState> {
    ```

## Error: no suitable constructor found for EntityModel(no arguments)
- **Lines:** 22
- **Suggested Fix:** The `net.minecraft.client.model.EntityModel` class no longer has a no-argument constructor. It now requires a `ModelPart` (representing the root of the model) to be passed to its constructor.

    **Concrete Change:**
    Update the constructor of `GrandPianoModel` to explicitly call the `super` constructor with the `ModelPart` representing the model's root.

    **Example:**
    ```java
    // Original:
    // public GrandPianoModel(ModelPart root) {
    //     this.piano = root.getChild("piano");
    //     this.fallBoard = piano.getChild("lid");
    //     this.fallBoardStick = fallBoard.getChild("lid_prop");
    // }

    // Proposed:
    public GrandPianoModel(ModelPart root) {
        super(root); // Explicitly call super constructor
        this.piano = root.getChild("piano");
        this.fallBoard = piano.getChild("lid");
        this.fallBoardStick = fallBoard.getChild("lid_prop");
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 219
- **Suggested Fix:** The `setupAnim` method in `net.minecraft.client.model.Model` (which `EntityModel` extends) has changed its signature. It now takes a single `EntityRenderState` object (or a subclass like `PianoRenderState`) instead of multiple float parameters.

    **Concrete Change:**
    Update the `setupAnim` method signature to match the new `Model` API.

    **Example:**
    ```java
    // Original:
    // @Override
    // public void setupAnim(Piano entity, float limbSwing, float limbSwingAmount, float ageInTicks, float relativeHeadYRot, float headPitch) {
    //
    // }

    // Proposed:
    @Override
    public void setupAnim(PianoRenderState state) {
        // Access entity data from 'state' object if needed
        // Example: Piano entity = state.entity;
    }
    ```

## Error: renderToBuffer(PoseStack,VertexConsumer,int,int,int) in GrandPianoModel cannot override renderToBuffer(PoseStack,VertexConsumer,int,int,int) in Model
- **Lines:** 225
- **Suggested Fix:** The `renderToBuffer` method in `net.minecraft.client.model.Model` is now declared as `final`. `final` methods cannot be overridden.

    **Concrete Change:**
    Remove the `renderToBuffer` method override from `GrandPianoModel.java`. The rendering of the model's parts should be handled by the model's primary rendering method or by ensuring the model parts are rendered when the model is used by a renderer.

    **Example:**
    ```java
    // Original:
    // @Override
    // public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color_overlay) {
    //     root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color_overlay);
    // }

    // Proposed: Remove the entire method.
    ```