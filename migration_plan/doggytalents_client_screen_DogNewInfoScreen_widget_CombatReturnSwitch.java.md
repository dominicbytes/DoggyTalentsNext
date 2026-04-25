# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/CombatReturnSwitch.java`

Total Errors: 4

## Error: CombatReturnSwitch is not abstract and does not override abstract method extractWidgetRenderState(GuiGraphicsExtractor,int,int,float) in AbstractWidget
- **Lines:** 24
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
        if (!this.visible) return;

        int cl = this.isHovered ? DEFAULT_HLCOLOR : DEFAULT_COLOR;
        graphics.fill( this.getX(), this.getY(), this.getX()+this.width, this.getY() +this.height, cl);

        this.updateHover(mouseX, mouseY);

        hoveredLeft = false;
        hoveredRight = false;

        if (this.isHovered) {
            if (mouseX - this.getX() < this.width/2) {
                hoveredLeft = true;
                hoveredRight = false;
            } else {
                hoveredLeft = false;
                hoveredRight = true;
            }
        }

        int mX = this.getX() + this.width/2;
        int mY = this.getY() + this.height/2;

        var back_c1 = Component.literal("<");
        back_c1.withStyle(
            Style.EMPTY.withBold(hoveredLeft)
        );
        int back_tX = this.getX() + PADDING_HORIZONTAL;
        int back_tY = mY - font.lineHeight/2;
        graphics.text(font, back_c1, back_tX, back_tY, hoveredLeft ? 0xffffffff : 0xa5ffffff);

        var next_c1 = Component.literal(">");
        next_c1.withStyle(
            Style.EMPTY.withBold(hoveredRight)
        );
        int next_tX = this.getX() + this.width - PADDING_HORIZONTAL - font.width(next_c1);
        int next_tY = mY - font.lineHeight/2;
        graphics.text(font, next_c1, next_tX, next_tY, hoveredRight ? 0xffffffff : 0xa5ffffff);

        var mode_c1 = this.getMessage();
        int mode_tX = mX - this.font.width(mode_c1)/2;
        int mode_tY = mY - this.font.lineHeight/2;
        graphics.text(font, mode_c1, mode_tX, mode_tY, 0xffffffff);

        if (this.stillHovered) {
            if (this.dog.tickCount - this.tickCount0 >= 1) {
                ++this.timeHoveredWithoutClick;
                this.tickCount0 = this.dog.tickCount;
            }
        }

        if (this.timeHoveredWithoutClick >= 25) {
            this.setOverlayToolTip(graphics.pose(), mouseX, mouseY);
        }
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 52
- **Suggested Fix:** The `renderWidget` method is no longer the correct method to override for custom rendering in widgets that extend `AbstractWidget`. Its logic should be moved to `extractWidgetRenderState`.

    **Concrete Change:**
    Remove the `renderWidget` method entirely.

## Error: method does not override or implement a method from a supertype
- **Lines:** 69
- **Suggested Fix:** The `onClick` method in `net.minecraft.client.gui.components.AbstractWidget` has changed its signature. It now takes a `MouseButtonEvent` and a `boolean doubleClick` parameter.

    **Concrete Change:**
    Update the `onClick` method signature.

    **Example:**
    ```java
    // Add import:
    import net.neoforged.neoforge.client.event.InputEvent.MouseButtonEvent;

    // Original:
    // @Override
    // public void onClick(double mouseX, double mouseY) {
    //     this.timeHoveredWithoutClick = 0;
    //     CombatReturnStrategy strategy;
    //     if (hoveredLeft) {
    //         strategy = this.dog.getCombatReturnStrategy().prev();
    //     } else {
    //         strategy = this.dog.getCombatReturnStrategy().next();
    //     }
    //     this.setMessage(Component.translatable(strategy.getUnlocalisedTitle()));
    //     PacketHandler.send(PacketDistributor.SERVER.noArg(),
    //         new CombatReturnStrategyData(this.dog.getId(), strategy));
    // }

    // Proposed:
    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) { // Changed signature
        this.timeHoveredWithoutClick = 0;
        CombatReturnStrategy strategy;
        if (hoveredLeft) {
            strategy = this.dog.getCombatReturnStrategy().prev();
        } else {
            strategy = this.dog.getCombatReturnStrategy().next();
        }
        this.setMessage(Component.translatable(strategy.getUnlocalisedTitle()));
        PacketHandler.send(PacketDistributor.SERVER.noArg(),
            new CombatReturnStrategyData(this.dog.getId(), strategy));
    }
    ```

## Error: incompatible types: Matrix3x2fStack cannot be converted to PoseStack
- **Lines:** 123
- **Suggested Fix:** This error is likely a cascading effect from the outdated `RenderSystem` calls within the `extractWidgetRenderState` method. Once those are removed, this error should resolve.

    **Concrete Change:** This error should be resolved by applying the fix for `extractWidgetRenderState`.