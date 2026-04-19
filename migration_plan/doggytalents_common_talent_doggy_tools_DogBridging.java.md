# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/talent/doggy_tools/DogBridging.java`

Total Errors: 5

## Error: no suitable method found for addCooldown(WhistleItem,int)
- **Lines:** 150, 168
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 114, 120, 130
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

