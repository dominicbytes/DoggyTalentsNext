# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/talent/FisherDogTalent.java`

Total Errors: 2

## Error: no suitable method found for spawnAtLocation(ItemStack)
- **Lines:** 64
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 114
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

