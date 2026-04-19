# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/util/dogpromise/promise/DogDistantTeleportToBedPromise.java`

Total Errors: 4

## Error: constructor ChunkPos in record ChunkPos cannot be applied to given types;
- **Lines:** 56
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: cannot find symbol
- **Lines:** 93, 104, 117
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

