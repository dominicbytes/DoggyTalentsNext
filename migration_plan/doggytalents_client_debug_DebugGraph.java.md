# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/debug/DebugGraph.java`

Total Errors: 1

## Error: invalid method reference
- **Lines:** 89
- **Suggested Fix:** The `RenderSystem.setShader(GameRenderer::getPositionColorShader);` call is invalid in Minecraft 1.21 (NeoForge 26.1.2). Both `RenderSystem.setShader` (in this form) and `GameRenderer.getPositionColorShader` have been removed or significantly refactored. The rendering pipeline for shaders has changed.

    For rendering debug lines, the new approach involves using `RenderType.lines()` or `RenderType.debugLineStrip()` to obtain a `VertexConsumer` from a `MultiBufferSource`. The `Tesselator.getInstance().begin(Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);` line is also outdated.

    **Concrete Change:**
    Refactor the debug line rendering logic. Replace the usage of `RenderSystem.setShader` and direct `Tesselator.getInstance().begin` with the new `RenderType` based approach.

    **Example (conceptual, exact implementation depends on context):**
    ```java
    // Instead of:
    // RenderSystem.setShader(GameRenderer::getPositionColorShader);
    // var tessellator = Tesselator.getInstance();
    // var buffer = tessellator.begin(Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

    // Use a VertexConsumer obtained from MultiBufferSource with RenderType.lines()
    // Example if within a render method with MultiBufferSource and PoseStack:
    // VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
    // // Then use vertexConsumer.vertex(poseStack.last().pose(), x, y, z).color(r, g, b, a).endVertex();
    ```
    The `DebugGraph` class will need to be adapted to receive a `MultiBufferSource` and `PoseStack` if it doesn't already have access to them in its rendering context.