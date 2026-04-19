# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/SoyPodsDriedItem.java`

Total Errors: 3

## Error: method getDescriptionId in class Item cannot be applied to given types;
- **Lines:** 26
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: no suitable method found for spawnAtLocation(ItemStack)
- **Lines:** 46
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: method does not override or implement a method from a supertype
- **Lines:** 23
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

