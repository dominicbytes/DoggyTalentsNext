# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/DTNClientPettingManager.java`

Total Errors: 12

## Error: cannot find symbol
- **Lines:** 273, 274, 379, 380, 381, 382, 383, 384, 267, 286
- **Suggested Fix:** The symbols `Axis.XP`, `Axis.ZP`, `Axis.YP` are not found. These are now located in `com.mojang.math.Axis`.

    **Concrete Change:**
    Add the following import statement:
    ```java
    import com.mojang.math.Axis;
    ```

## Error: cannot find symbol
- **Lines:** 147
- **Suggested Fix:** The `getTimer()` method on `Minecraft` no longer provides `getGameTimeDeltaPartialTick()`. This functionality has moved to `DeltaTracker`.

    **Concrete Change:**
    Replace `mc.getTimer().getGameTimeDeltaPartialTick(true)` with `mc.getDeltaTracker().getGameTimeDeltaPartialTick(true)`.

    **Example:**
    ```java
    // Original:
    // var pTicks = mc.getTimer().getGameTimeDeltaPartialTick(true);
    // Proposed:
    var pTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
    ```

## Error: incompatible types: int cannot be converted to MouseButtonEvent
- **Lines:** 176
- **Suggested Fix:** The `matchesMouse` method of `net.minecraft.client.KeyMapping` no longer accepts an `int` representing the mouse button. It now expects a `MouseButtonEvent` object. The `InputEvent.MouseButton.Pre` event object itself can be passed directly.

    **Concrete Change:**
    Replace `mc.options.keyUse.matchesMouse(button)` with `mc.options.keyUse.matchesMouse(event)`.

    **Example:**
    ```java
    // Original:
    // var button = event.getButton();
    // if (!mc.options.keyUse.matchesMouse(button))
    // Proposed:
    if (!mc.options.keyUse.matchesMouse(event))
    ```