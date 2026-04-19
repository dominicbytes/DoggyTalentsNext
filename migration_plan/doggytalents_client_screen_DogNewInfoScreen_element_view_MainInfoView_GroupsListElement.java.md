# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/GroupsListElement.java`

Total Errors: 10

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 182
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: invalid method reference
- **Lines:** 176
- **Suggested Fix:** Analyze the specific error message and compare with 26.1.2 sources.

## Error: cannot find symbol
- **Lines:** 180, 180, 177, 178, 179
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: method does not override or implement a method from a supertype
- **Lines:** 75, 144, 164
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

