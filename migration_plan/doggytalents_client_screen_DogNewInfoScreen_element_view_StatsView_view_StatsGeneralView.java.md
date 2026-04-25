# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/StatsView/view/StatsGeneralView.java`

Total Errors: 2

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 39, 41
- **Suggested Fix:** The `blit` method in `net.minecraft.client.gui.GuiGraphicsExtractor` has changed its signature. It now typically requires a `RenderPipeline` as the first argument and also `textureWidth` and `textureHeight` parameters.

    **Concrete Change:**
    Update the `blit` calls to match the new `GuiGraphicsExtractor` API.

    **Example (for line 39):**
    ```java
    // Add import:
    import net.minecraft.client.renderer.RenderPipelines;

    // Original:
    // graphics.blit(DogScreenOverlays.GUI_ICONS_LOCATION, startX + font.width(draw), pY - 1, 16, 0 ,9, 9);
    // Proposed:
    graphics.blit(RenderPipelines.GUI_TEXTURED, DogScreenOverlays.GUI_ICONS_LOCATION, startX
        + font.width(draw), pY - 1, 16, 0 ,9, 9, 256, 256);
    ```
    Apply similar changes to line 41.