# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/network/packet/DogUntamePacket.java`

Total Errors: 2

## Error: no suitable method found for addCooldown(Item,int)
- **Lines:** 51
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: incompatible types: Item cannot be converted to ItemStack
- **Lines:** 29
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

