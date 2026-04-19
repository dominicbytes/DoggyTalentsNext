# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/DoggyCharmItem.java`

Total Errors: 6

## Error: method getDescriptionId in class Item cannot be applied to given types;
- **Lines:** 190
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: no suitable method found for addCooldown(DoggyCharmItem,int)
- **Lines:** 89, 133
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 75, 118
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: method does not override or implement a method from a supertype
- **Lines:** 187
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

