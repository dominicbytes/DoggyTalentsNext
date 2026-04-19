# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/view/ArtifactsView/widget/ArtifactHolder.java`

Total Errors: 10

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 56
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: invalid method reference
- **Lines:** 50
- **Suggested Fix:** Analyze the specific error message and compare with 26.1.2 sources.

## Error: method does not override or implement a method from a supertype
- **Lines:** 39, 59
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: cannot find symbol
- **Lines:** 49, 51, 52, 53, 54
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: ArtifactHolder is not abstract and does not override abstract method extractWidgetRenderState(GuiGraphicsExtractor,int,int,float) in AbstractWidget
- **Lines:** 21
- **Suggested Fix:** A new abstract method was added to the superclass/interface. Implement the missing method (e.g., extractWidgetRenderState for GUI widgets).

