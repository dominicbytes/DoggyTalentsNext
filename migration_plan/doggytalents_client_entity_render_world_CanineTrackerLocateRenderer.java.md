# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/render/world/CanineTrackerLocateRenderer.java`

Total Errors: 5

## Error: cannot find symbol
- **Lines:** 68
- **Suggested Fix:** The `drawFloatingDistanceText` method is defined within the `CanineTrackerLocateRenderer` class. This `cannot find symbol` error is likely a cascading effect from other compilation issues within the class or its dependencies. It is recommended to address other errors first, as this error may resolve itself once its dependencies are correctly compiled.

    **Concrete Change:** No direct change to this line is proposed at this moment. Focus on resolving other errors in the class first.

## Error: cannot find symbol
- **Lines:** 122, 164, 206
- **Suggested Fix:** The `drawInBatch` method of `net.minecraft.client.gui.Font` has changed its signature in Minecraft 1.21 (NeoForge 26.1.2). The parameters for light and overlay have been reordered.

    **Concrete Change:**
    Reorder the `light` and `overlay` parameters in the `drawInBatch` calls.

    **Example (for line 122):**
    ```java
    // Original:
    // font.drawInBatch(line1, tX, tY, 0xffffffff, false, text_mat, bufferSource, DisplayMode.SEE_THROUGH, 0, 15728880);
    // Proposed:
    font.drawInBatch(line1, tX, tY, 0xffffffff, false, text_mat, bufferSource, DisplayMode.SEE_THROUGH, 15728880, 0);
    ```
    Apply similar changes to lines 164 and 206.

## Error: cannot find symbol
- **Lines:** 228
- **Suggested Fix:** The `rgbToInt` method is defined within the `RenderUtil` class. This `cannot find symbol` error is likely a cascading effect from other compilation issues within the class or its dependencies. It is recommended to address other errors first, as this error may resolve itself once its dependencies are correctly compiled.

    **Concrete Change:** No direct change to this line is proposed at this moment. Focus on resolving other errors in the project first.