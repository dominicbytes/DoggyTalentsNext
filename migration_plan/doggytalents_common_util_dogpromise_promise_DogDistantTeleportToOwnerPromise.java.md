# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/util/dogpromise/promise/DogDistantTeleportToOwnerPromise.java`

Total Errors: 5

## Error: constructor ChunkPos in record ChunkPos cannot be applied to given types;
- **Lines:** 151
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: x has private access in ChunkPos
- **Lines:** 152
- **Suggested Fix:** Field is now private. Use the corresponding getter/setter method.

## Error: z has private access in ChunkPos
- **Lines:** 152
- **Suggested Fix:** Field is now private. Use the corresponding getter/setter method.

## Error: cannot find symbol
- **Lines:** 89, 108
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

