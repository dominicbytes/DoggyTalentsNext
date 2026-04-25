# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DTLootModifierProvider.java`

Total Errors: 4

## Error: incompatible types: Item cannot be converted to HolderGetter<Item>
- **Lines:** 66
- **Suggested Fix:** The `of` method of `net.minecraft.advancements.critereon.ItemPredicate.Builder` now expects a `HolderGetter<Item>` (typically `HolderSet.direct(item.builtInRegistryHolder())`) instead of a direct `Item` object.

    **Concrete Change:**
    Convert the `Item` to a `HolderGetter<Item>`.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.core.HolderSet;

    // Original:
    // MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS))
    // Proposed:
    MatchTool.toolMatches(ItemPredicate.Builder.item().of(HolderSet.direct(Items.SHEARS.builtInRegistryHolder())))
    ```

## Error: no suitable method found for of(EntityType<Dog>)
- **Lines:** 85
- **Suggested Fix:** The `of` method of `net.minecraft.advancements.critereon.EntityPredicate.Builder` now expects a `HolderSet<EntityType<?>>` instead of a direct `EntityType`.

    **Concrete Change:**
    Convert the `EntityType` to a `HolderSet<EntityType<?>>`.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.core.HolderSet; // Ensure this is imported

    // Original:
    // EntityPredicate.Builder.entity().of(
    //     DoggyEntityTypes.DOG.get())
    // Proposed:
    EntityPredicate.Builder.entity().of(
        HolderSet.direct(DoggyEntityTypes.DOG.get().builtInRegistryHolder()))
    ```

## Error: no suitable method found for of(TagKey<EntityType<?>>)
- **Lines:** 93
- **Suggested Fix:** The `of` method of `net.minecraft.advancements.critereon.EntityPredicate.Builder` now expects a `HolderGetter<EntityType<?>>` and a `TagKey<EntityType<?>>`.

    **Concrete Change:**
    Update the `of` method call to include `registries.lookupOrThrow(Registries.ENTITY_TYPE)`.

    **Example:**
    ```java
    // Add imports:
    import net.minecraft.core.HolderLookup;
    import net.minecraft.core.registries.Registries;

    // Original:
    // EntityPredicate.Builder.entity().of(DoggyTags.DROP_SOY_WHEN_DOG_KILL)
    // Proposed:
    EntityPredicate.Builder.entity().of(
        registries.lookupOrThrow(Registries.ENTITY_TYPE), // Assuming 'registries' is available from the constructor
        DoggyTags.DROP_SOY_WHEN_DOG_KILL
    )
    ```

## Error: cannot find symbol
- **Lines:** 63
- **Suggested Fix:** The `LootTableIdCondition` class has moved to a new package in NeoForge 26.1.2.

    **Concrete Change:**
    Update the import statement for `LootTableIdCondition`.

    **Example:**
    ```java
    // Original:
    // import net.minecraft.world.level.storage.loot.predicates.LootTableIdCondition;
    // Proposed:
    import net.neoforged.neoforge.common.loot.LootTableIdCondition;
    ```