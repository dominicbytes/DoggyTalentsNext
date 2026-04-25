# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DTItemTagsProvider.java`

Total Errors: 1

## Error: cannot find symbol
- **Lines:** 39
- **Suggested Fix:** The `net.minecraft.tags.ItemTags.DYEABLE` tag has been removed in Minecraft 1.21 (NeoForge 26.1.2). Dyeable item functionality is now handled through data components or other item properties, not through this tag.

    **Concrete Change:**
    Remove the entire block that attempts to add items to `ItemTags.DYEABLE`. The dyeing functionality for these items will need to be re-implemented using the new Minecraft 1.21 data component system.

    **Example:**
    ```java
    // Original:
    // tag(ItemTags.DYEABLE).add(
    //     DoggyItems.WOOL_COLLAR.get(),
    //     DoggyItems.WOOL_COLLAR_THICC.get(),
    //     DoggyItems.CAPE_COLOURED.get(),
    //     DoggyItems.BOWTIE.get(),
    //     DoggyItems.WIG.get(),
    // );

    // Proposed: Remove this entire block.
    ```