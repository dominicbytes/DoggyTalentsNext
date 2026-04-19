# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/GenderBoneItem.java`

Total Errors: 4

## Error: no suitable method found for addCooldown(Item,int)
- **Lines:** 44
- **Suggested Fix:** Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>.

## Error: no suitable constructor found for ItemParticleOption(ParticleType<ItemParticleOption>,ItemStack)
- **Lines:** 50
- **Suggested Fix:** Analyze the specific error message and compare with 26.1.2 sources.

## Error: cannot find symbol
- **Lines:** 58
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: incompatible types: Item cannot be converted to ItemStack
- **Lines:** 35
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

