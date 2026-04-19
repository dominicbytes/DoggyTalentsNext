# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/ai/GuardModeGoal.java`

Total Errors: 3

## Error: cannot find symbol
- **Lines:** 60
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: incompatible types: incompatible parameter types in lambda expression
- **Lines:** 32, 174
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

