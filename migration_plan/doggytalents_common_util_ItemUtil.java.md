# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/util/ItemUtil.java`

Total Errors: 5

## Error: constructor DyedItemColor in record DyedItemColor cannot be applied to given types;
- **Lines:** 148, 153
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: cannot find symbol
- **Lines:** 119, 163, 172
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

