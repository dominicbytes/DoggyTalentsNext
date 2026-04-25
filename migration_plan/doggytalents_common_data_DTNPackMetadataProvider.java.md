# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DTNPackMetadataProvider.java`

Total Errors: 2

## Error: cannot find symbol
- **Lines:** 20
- **Suggested Fix:** The symbol `DetectedVersion.BUILT_IN` exists in `net.minecraft.DetectedVersion`. This `cannot find symbol` error is likely a cascading effect from other compilation issues within the class or its dependencies. It is recommended to address other errors first, as this error may resolve itself once its dependencies are correctly compiled.

    **Concrete Change:** No direct change to this line is proposed at this moment. Focus on resolving other errors in the project first.

## Error: cannot find symbol
- **Lines:** 22
- **Suggested Fix:** The `InclusiveRange` class has moved to a new package in Minecraft 1.21 (NeoForge 26.1.2).

    **Concrete Change:**
    Update the import statement for `InclusiveRange`.

    **Example:**
    ```java
    // Original:
    // import com.mojang.datafixers.util.InclusiveRange; // Assuming this was the old import
    // Proposed:
    import net.minecraft.util.InclusiveRange; // New import
    ```