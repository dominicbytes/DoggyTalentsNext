# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/TreatBagItem.java`

Total Errors: 6

## Error: method appendHoverText in class Item cannot be applied to given types;
- **Lines:** 134
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: no suitable method found for addCooldown(TreatBagItem,int)
- **Lines:** 97
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: selected has private access in Inventory
- **Lines:** 72
- **Suggested Fix:** Field is now private. Use the corresponding getter/setter method.

## Error: method does not override or implement a method from a supertype
- **Lines:** 131
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: cannot find symbol
- **Lines:** 156, 177
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

