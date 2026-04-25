# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DTAdvancementProvider.java`

Total Errors: 3

## Error: incompatible types: Item cannot be converted to HolderGetter<Item>
- **Lines:** 58, 78
- **Suggested Fix:** The `of` method of `net.minecraft.advancements.critereon.ItemPredicate.Builder` now expects a `HolderGetter<Item>` (typically `HolderSet.direct(item.builtInRegistryHolder())`) instead of a direct `Item` object.

    **Concrete Change:**
    Convert the `Item` to a `HolderGetter<Item>` using `HolderSet.direct(item.builtInRegistryHolder())`.

    **Example (for line 58):**
    ```java
    // Add import:
    import net.minecraft.core.HolderSet;

    // Original:
    // ItemPredicate.Builder.item()
    //     .of(DoggyItems.DOGGY_CHARM.get())
    // Proposed:
    ItemPredicate.Builder.item()
        .of(HolderSet.direct(DoggyItems.DOGGY_CHARM.get().builtInRegistryHolder()))
    ```
    Apply similar changes to line 78.

## Error: no suitable method found for of(EntityType<Wolf>)
- **Lines:** 81
- **Suggested Fix:** The `of` method of `net.minecraft.advancements.critereon.EntityPredicate.Builder` now expects a `HolderSet<EntityType<?>>` instead of a direct `EntityType`.

    **Concrete Change:**
    Convert the `EntityType` to a `HolderSet<EntityType<?>>`.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.core.HolderSet; // Ensure this is imported

    // Original:
    // EntityPredicate.Builder.entity()
    //     .of(EntityType.WOLF)
    //     .build()))
    // Proposed:
    EntityPredicate.Builder.entity()
        .of(HolderSet.direct(EntityType.WOLF.builtInRegistryHolder()))
        .build()))
    ```