# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/util/NBTUtil.java`

Total Errors: 6

## Error: cannot find symbol
- **Lines:** 226, 233
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

## Error: incompatible types: Optional<Long> cannot be converted to long
- **Lines:** 59
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: incompatible types: Optional<String> cannot be converted to String
- **Lines:** 80, 114
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: incompatible types: Optional<Reference<T>> cannot be converted to T
- **Lines:** 156
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

