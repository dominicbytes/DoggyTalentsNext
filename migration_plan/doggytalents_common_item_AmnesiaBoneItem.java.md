# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/item/AmnesiaBoneItem.java`

Total Errors: 6

## Error: method getDescriptionId in class Item cannot be applied to given types;
- **Lines:** 99
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: method does not override or implement a method from a supertype
- **Lines:** 96
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: cannot find symbol
- **Lines:** 64, 88, 89, 91
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

