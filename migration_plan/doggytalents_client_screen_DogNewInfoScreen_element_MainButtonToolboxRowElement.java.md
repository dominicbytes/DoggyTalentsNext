# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/MainButtonToolboxRowElement.java`

Total Errors: 9

## Error: method does not override or implement a method from a supertype
- **Lines:** 51
- **Suggested Fix:** This error is on the `renderElement` method. This method is likely from a custom interface or superclass. Without knowing the supertype, it's hard to give a concrete fix. This error is likely a cascading error.

    **Concrete Change:** No direct change to this line is proposed at this moment. Focus on resolving other errors in the class first.

## Error: invalid method reference
- **Lines:** 58
- **Suggested Fix:** This error is likely a cascading effect from the `blit` method signature mismatch.

    **Concrete Change:** This error should resolve once the `blit` method call is correctly handled.

## Error: cannot find symbol
- **Lines:** 53, 59, 60, 61, 62, 62
- **Suggested Fix:** These errors are likely cascading effects from the `blit` method signature mismatch and potential issues with `RenderPipelines.GUI_TEXTURED` or `Resources.HAMBURGER`. While `RenderPipelines.GUI_TEXTURED` and `Resources.HAMBURGER` exist, the compiler might be unable to resolve them due to other underlying issues.

    **Concrete Change:** These errors should resolve once the `blit` method call is correctly handled.

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 63
- **Suggested Fix:** The `blit` method in `net.minecraft.client.gui.GuiGraphicsExtractor` has a different signature than what is being called. The current call `graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Resources.HAMBURGER, mX - 10, mY - 10, (float)0, (float)0, 20, 20, 256, 256);` actually matches one of the new `blit` signatures: `public void blit(RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)`. The error is likely a cascading effect from other compilation issues, or a problem with the `RenderPipelines` class itself.

    **Concrete Change:** No direct change to this line is proposed at this moment. Focus on resolving other errors in the class first.