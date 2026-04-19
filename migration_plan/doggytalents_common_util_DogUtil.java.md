# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/util/DogUtil.java`

Total Errors: 11

## Error: constructor ChunkPos in record ChunkPos cannot be applied to given types;
- **Lines:** 606
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: cannot find symbol
- **Lines:** 744, 746, 747, 748, 749, 751, 760, 580, 642, 656
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

