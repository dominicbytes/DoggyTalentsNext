# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/MainButtonToolboxRowElement.java`

Total Errors: 9

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 63
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: invalid method reference
- **Lines:** 58
- **Suggested Fix:** Analyze the specific error message and compare with 26.1.2 sources.

## Error: cannot find symbol
- **Lines:** 62, 62, 59, 60, 61, 53
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: method does not override or implement a method from a supertype
- **Lines:** 51
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

