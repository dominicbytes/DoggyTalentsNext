# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/AccessoryStatusHover.java`

Total Errors: 8

## Error: no suitable method found for blit(Identifier,int,int,int,int,int,int)
- **Lines:** 51
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 50, 50, 46, 48, 49
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: method does not override or implement a method from a supertype
- **Lines:** 38
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: AccessoryStatusHover is not abstract and does not override abstract method extractWidgetRenderState(GuiGraphicsExtractor,int,int,float) in AbstractWidget
- **Lines:** 18
- **Suggested Fix:** A new abstract method was added to the superclass/interface. Implement the missing method (e.g., extractWidgetRenderState for GUI widgets).

