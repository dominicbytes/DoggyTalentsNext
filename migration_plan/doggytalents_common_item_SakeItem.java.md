# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/SakeItem.java`

Total Errors: 8

## Error: method getDescriptionId in class Item cannot be applied to given types;
- **Lines:** 158
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: no suitable method found for addCooldown(SakeItem,int)
- **Lines:** 57, 70
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: no suitable method found for spawnAtLocation(ItemStack)
- **Lines:** 85
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: method does not override or implement a method from a supertype
- **Lines:** 94, 155
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: incompatible types: SakeItem cannot be converted to ItemStack
- **Lines:** 45
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: incompatible types: Reference<SoundEvent> cannot be converted to SoundEvent
- **Lines:** 102
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

