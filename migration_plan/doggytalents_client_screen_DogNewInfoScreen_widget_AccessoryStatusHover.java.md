# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/AccessoryStatusHover.java`

Total Errors: 8

## Error: AccessoryStatusHover is not abstract and does not override abstract method extractWidgetRenderState(GuiGraphicsExtractor,int,int,float) in AbstractWidget
- **Lines:** 18
- **Suggested Fix:** The `net.minecraft.client.gui.components.AbstractWidget` class now has a new abstract method `extractWidgetRenderState` that must be implemented. The rendering logic from the old `renderWidget` method should be moved into this new method.

    **Concrete Change:**
    Implement the `extractWidgetRenderState` method and move the rendering logic from `renderWidget` into it.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.client.renderer.RenderPipelines; // New import

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pTicks) {
        var render_icon = this.logoIcon;
        if (state == AccessoryState.MODEL_ONLY)
            render_icon = this.modelIcon;

        if (render_icon == ItemStack.EMPTY)
            return;
        graphics.item(render_icon, this.getX()+1, this.getY()+1);
        int iX = getIconXState();
        graphics.blit(RenderPipelines.GUI_TEXTURED, Resources.STYLE_ADD_REMOVE, getX()+11, getY()+11, (float)iX, (float)0, 9, 9, 256, 256);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 38
- **Suggested Fix:** The `renderWidget` method is no longer the correct method to override for custom rendering in widgets that extend `AbstractWidget`. Its logic should be moved to `extractWidgetRenderState`.

    **Concrete Change:**
    Remove the `renderWidget` method entirely.

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 51
- **Suggested Fix:** The `blit` method in `net.minecraft.client.gui.GuiGraphicsExtractor` has changed its signature. The original call `graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Resources.STYLE_ADD_REMOVE, getX()+11, getY()+11, (float)iX, (float)0, 9, 9, 256, 256);` needs to be updated to use `net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED` as the first parameter and include `textureWidth` and `textureHeight`.

    **Concrete Change:**
    Update the `blit` call within `extractWidgetRenderState`.

    **Example:**
    ```java
    // Original (within renderWidget, now extractWidgetRenderState):
    // graphics.blit(Resources.STYLE_ADD_REMOVE, getX()+11, getY()+11, (float)iX, (float)0, 9, 9);
    // Proposed:
    graphics.blit(RenderPipelines.GUI_TEXTURED, Resources.STYLE_ADD_REMOVE, getX()+11, getY()+11, (float)iX, (float)0, 9, 9, 256, 256);
    ```

## Error: cannot find symbol
- **Lines:** 46, 48, 49, 50, 50
- **Suggested Fix:** These `cannot find symbol` errors are likely cascading effects from the outdated `RenderSystem` calls and `blit` method usage within the `renderWidget` method. Once `extractWidgetRenderState` is correctly implemented and the rendering calls are updated, these errors should resolve.

    **Concrete Change:** These errors should be resolved by applying the fixes for `extractWidgetRenderState` and the `blit` call.