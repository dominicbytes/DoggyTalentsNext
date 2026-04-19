# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/data/DTNDatapackProvider.java`

Total Errors: 6

## Error: no suitable method found for shaped(RecipeCategory,Item,int)
- **Lines:** 37
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: no suitable method found for has(ItemLike)
- **Lines:** 72
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: method does not override or implement a method from a supertype
- **Lines:** 66
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: cannot find symbol
- **Lines:** 29
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: incompatible types: PackOutput cannot be converted to Provider
- **Lines:** 57
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: PackRecipeProvider is not abstract and does not override abstract method buildRecipes() in RecipeProvider
- **Lines:** 51
- **Suggested Fix:** A new abstract method was added to the superclass/interface. Implement the missing method (e.g., extractWidgetRenderState for GUI widgets).

