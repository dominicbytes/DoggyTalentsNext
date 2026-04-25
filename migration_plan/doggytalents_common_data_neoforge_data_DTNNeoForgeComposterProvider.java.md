# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/neoforge_data/DTNNeoForgeComposterProvider.java`

Total Errors: 2

## Error: method does not override or implement a method from a supertype
- **Lines:** 19
- **Suggested Fix:** The `gather` method in `net.neoforged.neoforge.common.data.DataMapProvider` has changed its signature. It now expects a `HolderLookup.Provider` parameter.

    **Concrete Change:**
    Update the `gather` method signature to match the new `DataMapProvider` API.

    **Example:**
    ```java
    // Original:
    // @Override
    // protected void gather() {
    //     var compostables = DTNCompostables.getCompostables();
    //     var builder = this.builder(NeoForgeDataMaps.COMPOSTABLES);
    //     compostables.forEach((item, chance) -> {
    //         var id = BuiltInRegistries.ITEM.getResourceKey(item);
    //         if (!id.isPresent())
    //             return;
    //         builder.add(id.get(), new Compostable(chance), false);
    //     });
    // }

    // Proposed:
    @Override
    protected void gather(Provider provider) { // Added Provider parameter
        var compostables = DTNCompostables.getCompostables();
        var builder = this.builder(NeoForgeDataMaps.COMPOSTABLES);
        compostables.forEach((item, chance) -> {
            var id = BuiltInRegistries.ITEM.getResourceKey(item);
            if (!id.isPresent())
                return;
            builder.add(id.get(), new Compostable(chance), false);
        });
    }
    ```

## Error: DTNNeoForgeComposterProvider is not abstract and does not override abstract method gather(Provider) in DataMapProvider
- **Lines:** 13
- **Suggested Fix:** This error is a direct consequence of the `gather` method signature change. Once the `gather` method is updated to match the `DataMapProvider` API, this error should resolve itself.

    **Concrete Change:** This error should be resolved by applying the fix for the `gather` method signature.