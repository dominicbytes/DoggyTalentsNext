# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/StyleView/view/SkinView/DogSkinElement.java`

Total Errors: 10

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int,int,int,int)
- **Lines:** 338, 343
- **Suggested Fix:** The `blit` method in `net.minecraft.client.gui.GuiGraphicsExtractor` no longer has a `zLevel` parameter in this position.

    **Concrete Change:**
    Remove the `zLevel` parameter (the third `0` in the original call) from the `blit` calls.

    **Example (for line 338):**
    ```java
    // Original:
    // graphics.blit(Resources.KANJI_MYSTERY_BKG, x - imgeSize/2,
    //     y - imgeSize/2 - 27, 0, 0, 0, imgeSize, imgeSize, imgeSize, imgeSize);
    // Proposed:
    graphics.blit(Resources.KANJI_MYSTERY_BKG, x - imgeSize/2,
        y - imgeSize/2 - 27, 0, imgeSize, imgeSize, imgeSize, imgeSize); // Removed zLevel (the 3rd 0)
    ```
    Apply similar changes to line 343.

## Error: cannot find symbol
- **Lines:** 333, 335, 336, 341, 345, 346
- **Suggested Fix:** The `RenderSystem` calls for `setShaderColor`, `enableBlend`, `blendFunc`, and `disableBlend` are outdated in Minecraft 1.21 (NeoForge 26.1.2). `GuiGraphicsExtractor` handles blending automatically.

    **Concrete Change:**
    Remove the outdated `RenderSystem` calls.

    **Example:**
    ```java
    // Original:
    // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    // RenderSystem.enableBlend();
    // RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    // ...
    // RenderSystem.disableBlend();

    // Proposed: Remove these lines.
    ```

## Error: incompatible types: int cannot be converted to Matrix3x2f
- **Lines:** 342
- **Suggested Fix:** This error is likely a cascading effect from the outdated `RenderSystem` calls. The `PoseStack.translate` method correctly accepts `float` or `double` parameters. Once the outdated `RenderSystem` calls are removed, this error should resolve itself.

    **Concrete Change:** This error should be resolved by applying the fix for the `cannot find symbol` errors related to `RenderSystem`.