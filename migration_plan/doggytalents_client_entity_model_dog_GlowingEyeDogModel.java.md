# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/model/dog/GlowingEyeDogModel.java`

Total Errors: 3

## Error: renderToBuffer(PoseStack,VertexConsumer,int,int,int) in GlowingEyeDogModel cannot override renderToBuffer(PoseStack,VertexConsumer,int,int,int) in Model
- **Lines:** 44
- **Suggested Fix:** The `renderToBuffer` method in `net.minecraft.client.model.Model` is now declared as `final`. `final` methods cannot be overridden. The existing override in `GlowingEyeDogModel` is simply calling `super.renderToBuffer`, which is no longer allowed.

    **Concrete Change:**
    Remove the `renderToBuffer` method override from `GlowingEyeDogModel.java`. The rendering of the model's parts should be handled directly within the model's primary rendering method (e.g., a `render` method if it exists, or by ensuring the model parts are rendered when the model is used by a renderer).

    **Example:**
    ```java
    // Original:
    // @Override
    // public void renderToBuffer(PoseStack stack, VertexConsumer vertex_consumer, int light, int overlay,
    //         int color_overlay) {
    //     this.glowingEyes.visible = false;
    //     this.realGlowingEyes.visible = false;
    //     super.renderToBuffer(stack, vertex_consumer, light, overlay, color_overlay);
    // }

    // Proposed: Remove the entire method.
    ```

## Error: cannot find symbol
- **Lines:** 39, 40
- **Suggested Fix:** The `copyFrom` method of `net.minecraft.client.model.geom.ModelPart` has been removed in Minecraft 1.21 (NeoForge 26.1.2). Instead of using `copyFrom`, you need to explicitly set the position and rotation of the target `ModelPart` using its `setPos` and `setRot` methods.

    **Concrete Change:**
    Replace the `copyFrom` calls with explicit `setPos` and `setRot` calls.

    **Example:**
    ```java
    // Original:
    // this.glowingEyes.copyFrom(this.head);
    // this.realGlowingEyes.copyFrom(this.realHead);

    // Proposed:
    this.glowingEyes.setPos(this.head.x, this.head.y, this.head.z);
    this.glowingEyes.setRot(this.head.xRot, this.head.yRot, this.head.zRot);
    this.realGlowingEyes.setPos(this.realHead.x, this.realHead.y, this.realHead.z);
    this.realGlowingEyes.setRot(this.realHead.xRot, this.realHead.yRot, this.realHead.zRot);
    ```