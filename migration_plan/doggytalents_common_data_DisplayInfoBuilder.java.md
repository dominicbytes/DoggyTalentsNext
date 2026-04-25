# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DisplayInfoBuilder.java`

Total Errors: 1

## Error: incompatible types: ItemStack cannot be converted to ItemStackTemplate
- **Lines:** 100
- **Suggested Fix:** The constructor for `net.minecraft.advancements.DisplayInfo` now expects an `ItemStackTemplate` for its icon parameter, instead of a direct `ItemStack`.

    **Concrete Change:**
    Convert the `ItemStack` (`icon`) to an `ItemStackTemplate` before passing it to the `DisplayInfo` constructor. This involves using the `ItemStack`'s `typeHolder()`, `getCount()`, and `getComponentsPatch()` methods to construct the `ItemStackTemplate`.

    **Example:**
    ```java
    // Add imports:
    import net.minecraft.world.item.ItemStackTemplate;
    import net.minecraft.core.Holder;
    import net.minecraft.core.component.DataComponentPatch;

    // Original:
    // public DisplayInfo build() {
    //     return new DisplayInfo(icon, title, description, Optional.ofNullable(background), frame, showToast, announceToChat, hidden);
    // }

    // Proposed:
    public DisplayInfo build() {
        ItemStackTemplate iconTemplate = new ItemStackTemplate(
            icon.getItem().builtInRegistryHolder(), // Get Holder<Item>
            icon.getCount(),
            icon.getComponentsPatch() // Get DataComponentPatch
        );
        return new DisplayInfo(iconTemplate, title, description, Optional.ofNullable(background), frame, showToast, announceToChat, hidden);
    }
    ```