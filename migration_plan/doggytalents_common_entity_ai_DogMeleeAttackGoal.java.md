# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/ai/DogMeleeAttackGoal.java`

Total Errors: 5

## Error: method doHurtTarget in class Dog cannot be applied to given types;
- **Lines:** 365
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: cannot find symbol
- **Lines:** 93, 97, 169, 171
- **Suggested Fix:** Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec.

