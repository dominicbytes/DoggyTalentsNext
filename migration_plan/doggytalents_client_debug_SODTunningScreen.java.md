# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/debug/SODTunningScreen.java`

Total Errors: 5

## Error: method keyPressed in class Screen cannot be applied to given types;
- **Lines:** 217
- **Suggested Fix:** The signature of `keyPressed` in `net.minecraft.client.gui.screens.Screen` has changed. It now takes a `KeyEvent` object instead of individual `keyCode`, `scanCode`, and `modifier` integers.

    **Concrete Change:**
    ```java
    // Add import:
    // import net.minecraft.client.gui.components.events.KeyEvent;

    @Override
    public boolean keyPressed(KeyEvent event) { // Changed signature
        var sneakKey = this.minecraft.options.keyShift;
        if (event.getKeyCode() == sneakKey.getKey().getValue()) { // Access key code from event
            this.simpleMode = true;
        }
        return super.keyPressed(event); // Pass event object
    }
    ```

## Error: method keyReleased in interface ContainerEventHandler cannot be applied to given types;
- **Lines:** 226
- **Suggested Fix:** The signature of `keyReleased` (inherited from `ContainerEventHandler` via `Screen`) has changed. It now takes a `KeyEvent` object instead of individual `keyCode`, `scanCode`, and `modifier` integers.

    **Concrete Change:**
    ```java
    // Add import:
    // import net.minecraft.client.gui.components.events.KeyEvent;

    @Override
    public boolean keyReleased(KeyEvent event) { // Changed signature
        var sneakKey = this.minecraft.options.keyShift;
        if (event.getKeyCode() == sneakKey.getKey().getValue()) { // Access key code from event
            this.simpleMode = false;
        }
        return super.keyReleased(event); // Pass event object
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 211, 220
- **Suggested Fix:** These errors are a direct consequence of the `keyPressed` and `keyReleased` method signature changes. Once the signatures are updated as described above, these "does not override" errors should be resolved.

    **Concrete Change:** Apply the changes for `keyPressed` and `keyReleased` as detailed above.

## Error: cannot find symbol
- **Lines:** 208
- **Suggested Fix:** The `render` method in `Screen` (and its superclasses) now expects `GuiGraphicsExtractor` instead of `GuiGraphics`. The `graphics` parameter in `SODTunningScreen`'s `render` method needs to be updated accordingly.

    **Concrete Change:**
    ```java
    // Add import:
    // import net.minecraft.client.gui.GuiGraphicsExtractor;

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pticks) { // Changed type
        graphics.fill(0, 0, this.width, this.height, 0x40000000);
        super.extractRenderState(graphics, mouseX, mouseY, pticks); // This error should resolve after type change
        this.sampleGraph.render(graphics, pticks);
    }
    ```