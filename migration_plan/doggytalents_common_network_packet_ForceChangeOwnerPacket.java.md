# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/network/packet/ForceChangeOwnerPacket.java`

Total Errors: 3

## Error: no suitable method found for addCooldown(Item,int)
- **Lines:** 52
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: cannot find symbol
- **Lines:** 32
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: incompatible types: Item cannot be converted to ItemStack
- **Lines:** 40
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

