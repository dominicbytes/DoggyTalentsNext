# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/view/EditInfoView.java`

Total Errors: 12

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 390
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: method does not override or implement a method from a supertype
- **Lines:** 113, 135, 157, 179, 229, 383
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: cannot find symbol
- **Lines:** 115, 137, 159, 181, 231
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

