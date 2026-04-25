# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/screen/DogCannotInteractWithScreen.java`

Total Errors: 3

## Error: cannot find symbol
- **Lines:** 184, 185, 187
- **Suggested Fix:** The `RenderSystem.enableBlend()`, `RenderSystem.blendFunc()`, and `RenderSystem.disableBlend()` calls are outdated in Minecraft 1.21 (NeoForge 26.1.2). `GuiGraphicsExtractor` handles blending automatically.

    **Concrete Change:**
    Remove the outdated `RenderSystem` calls.

    **Example:**
    ```java
    // Original:
    // private void drawDefeatedKanji(GuiGraphicsExtractor graphics, int x, int y, int size)  {
    //     RenderSystem.enableBlend();
    //     RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    //     graphics.blit(RenderPipelines.GUI_TEXTURED, getDefeatedKanji(this.dog), x, y, 0.0F, 0.0F, size, size, size, size);
    //     RenderSystem.disableBlend();
    // }

    // Proposed:
    private void drawDefeatedKanji(GuiGraphicsExtractor graphics, int x, int y, int size)  {
        // RenderSystem.enableBlend(); // Remove
        // RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA); // Remove
        graphics.blit(RenderPipelines.GUI_TEXTURED, getDefeatedKanji(this.dog), x, y, 0.0F, 0.0F, size, size, size, size);
        // RenderSystem.disableBlend(); // Remove
    }
    ```