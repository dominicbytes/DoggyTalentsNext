# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/RenderUtil.java`

Total Errors: 1

## Error: cannot find symbol
- **Lines:** 34
- **Suggested Fix:** The `cameraOrientation()` method on `entityRenderDispatcher` has been removed or replaced in Minecraft 1.21 (NeoForge 26.1.2). The camera orientation is now directly accessible via the `camera` field of `EntityRenderDispatcher`.

    **Concrete Change:**
    Replace `entityRenderDispatcher.cameraOrientation()` with `entityRenderDispatcher.camera.orientation`.

    **Example:**
    ```java
    // Original:
    // stack.mulPose(entityRenderDispatcher.cameraOrientation());
    // Proposed:
    stack.mulPose(entityRenderDispatcher.camera.orientation);
    ```