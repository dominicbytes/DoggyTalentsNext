# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/DogStatusViewBoxElement.java`

Total Errors: 16

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int,int,int,int)
- **Lines:** 94, 280
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 170, 171, 266
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 92, 92, 278, 278, 89, 91, 96, 276, 277, 281
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: method does not override or implement a method from a supertype
- **Lines:** 51
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

