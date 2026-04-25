# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/StyleView/widget/AccessoryHolder.java`

Total Errors: 10

## Error: AccessoryHolder is not abstract and does not override abstract method extractWidgetRenderState(GuiGraphicsExtractor,int,int,float) in AbstractWidget
- **Lines:** 22
- **Suggested Fix:** The `net.minecraft.client.gui.components.AbstractWidget` class now has a new abstract method `extractWidgetRenderState` that must be implemented. The rendering logic from the old `renderWidget` method should be moved into this new method.

    **Concrete Change:**
    Implement the `extractWidgetRenderState` method and move the rendering logic from `renderWidget` into it.

    **Example:**
    ```java
    // Add imports:
    import com.mojang.blaze3d.systems.RenderSystem;
    import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
    import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
    import net.minecraft.client.renderer.GameRenderer;
    import net.minecraft.client.renderer.RenderPipelines; // New import

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pTicks) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        this.active = !this.itemStack.isEmpty();
        if (!this.active) return;
        if (this.isHovered) {
            int bkg_col = this.add ? BKGCOL_ADD : BKGCOL_REM;
            graphics.fill( this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height, bkg_col);
        }
        
        graphics.renderItem(itemStack, this.getX()+1, this.getY()+1);
        // Remove outdated RenderSystem calls
        // RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.enableBlend();
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        int iX = add ? ICON_ADD_X : ICON_REM_X;
        if (warning) iX = ICON_WARN_X;
        // Update blit call to use RenderPipelines.GUI_TEXTURED
        graphics.blit(RenderPipelines.GUI_TEXTURED, Resources.STYLE_ADD_REMOVE, getX()+14, getY()+14, iX, 0, 9, 9, 256, 256);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 46
- **Suggested Fix:** The `renderWidget` method is no longer the correct method to override for custom rendering in widgets that extend `AbstractWidget`. Its logic should be moved to `extractWidgetRenderState`.

    **Concrete Change:**
    Remove the `renderWidget` method entirely.

## Error: method does not override or implement a method from a supertype
- **Lines:** 67
- **Suggested Fix:** The `onClick` method in `net.minecraft.client.gui.components.AbstractWidget` has changed its signature. It now takes a `MouseButtonEvent` and a `boolean doubleClick` parameter.

    **Concrete Change:**
    Update the `onClick` method signature.

    **Example:**
    ```java
    // Add import:
    import net.neoforged.neoforge.client.event.InputEvent.MouseButtonEvent;

    // Original:
    // @Override
    // public void onClick(double x, double y) {
    //     if (this.itemStack == null || this.itemStack.isEmpty())
    //         return;
    //     if (this.itemStack.is(Items.WOLF_ARMOR)) {
    //         PacketHandler.send(PacketDistributor.SERVER.noArg(),
    //             new ChangeAccessoriesData(this.dog.getId(), add, inventorySlotId, true));
    //         return;
    //     }
    //     PacketHandler.send(PacketDistributor.SERVER.noArg(),
    //         new ChangeAccessoriesData(this.dog.getId(), add, inventorySlotId));
    // }

    // Proposed:
    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) { // Changed signature
        if (this.itemStack == null || this.itemStack.isEmpty())
            return;
        if (this.itemStack.is(Items.WOLF_ARMOR)) {
            PacketHandler.send(PacketDistributor.SERVER.noArg(),
                new ChangeAccessoriesData(this.dog.getId(), add, inventorySlotId, true));
            return;
        }
        PacketHandler.send(PacketDistributor.SERVER.noArg(),
            new ChangeAccessoriesData(this.dog.getId(), add, inventorySlotId));
    }
    ```

## Error: invalid method reference
- **Lines:** 57
- **Suggested Fix:** This error is likely a cascading effect from the outdated `RenderSystem` calls and `blit` method usage within the `renderWidget` method. Once `extractWidgetRenderState` is correctly implemented and the rendering calls are updated, this error should resolve.

    **Concrete Change:** This error should be resolved by applying the fixes for `extractWidgetRenderState` and the `blit` call.

## Error: cannot find symbol
- **Lines:** 56, 58, 59, 60, 61
- **Suggested Fix:** These `cannot find symbol` errors are likely cascading effects from the outdated `RenderSystem` calls and `blit` method usage within the `renderWidget` method. Once `extractWidgetRenderState` is correctly implemented and the rendering calls are updated, these errors should resolve.

    **Concrete Change:** These errors should be resolved by applying the fixes for `extractWidgetRenderState` and the `blit` call.

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 64
- **Suggested Fix:** The `blit` method in `net.minecraft.client.gui.GuiGraphicsExtractor` has changed its signature. The original call `graphics.blit(Resources.STYLE_ADD_REMOVE, getX()+14, getY()+14, iX, 0, 9, 9);` needs to be updated to use `net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED` as the first parameter and include `textureWidth` and `textureHeight`.

    **Concrete Change:**
    Update the `blit` call within `extractWidgetRenderState`.

    **Example:**
    ```java
    // Original (within renderWidget, now extractWidgetRenderState):
    // graphics.blit(Resources.STYLE_ADD_REMOVE, getX()+14, getY()+14, iX, 0, 9, 9);
    // Proposed:
    graphics.blit(RenderPipelines.GUI_TEXTURED, Resources.STYLE_ADD_REMOVE, getX()+14, getY()+14, iX, 0, 9, 9, 256, 256);
    ```