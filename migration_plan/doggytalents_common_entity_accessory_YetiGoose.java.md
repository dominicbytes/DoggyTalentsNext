# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/accessory/YetiGoose.java`

Total Errors: 2

## Error: method does not override or implement a method from a supertype
- **Lines:** 44
- **Suggested Fix:** The `appendHoverText` method in `net.minecraft.world.item.Item` has changed its signature.

    **Concrete Change:**
    Update the `appendHoverText` method signature to match the new `Item` API.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.world.item.component.TooltipDisplay;

    // Original:
    // @Override
    // public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<net.minecraft.network.chat.Component> components,
    //         TooltipFlag flags) {
    //     var desc_id = this.getDescriptionId() + ".description";
    //     components.accept(Component.translatable(desc_id).withStyle(
    //         Style.EMPTY.withItalic(true)
    //     ));
    // }

    // Proposed:
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, java.util.function.Consumer<net.minecraft.network.chat.Component> builder,
            TooltipFlag flags) {
        var desc_id = this.getDescriptionId() + ".description";
        builder.accept(Component.translatable(desc_id).withStyle(
            Style.EMPTY.withItalic(true)
        ));
    }
    ```

## Error: method getDescriptionId in class Item cannot be applied to given types;
- **Lines:** 47
- **Suggested Fix:** The `getDescriptionId()` method in `net.minecraft.world.item.Item` exists and takes no arguments. This error is likely a cascading effect from the `appendHoverText` method signature mismatch. Once `appendHoverText` is fixed, this error should resolve.

    **Concrete Change:** This error should be resolved by applying the fix for the `appendHoverText` method.