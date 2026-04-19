# Migration Plan for `  /home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/texture/DogAllowedSkinManager.java`

Total Errors: 5

## Error: method addListener in class SortedReloadListenerEvent cannot be applied to given types;
- **Lines:** 127
- **Suggested Fix:** Arguments passed to the method are incorrect for 26.1.2. Check the new method signature.

## Error: method does not override or implement a method from a supertype
- **Lines:** 63
- **Suggested Fix:** The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source.

## Error: incompatible types: Gson cannot be converted to Codec
- **Lines:** 55
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: incompatible types: DogAllowedSkinManager cannot be converted to Identifier
- **Lines:** 127
- **Suggested Fix:** Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder).

## Error: DogAllowedSkinManager is not abstract and does not override abstract method apply(Object,ResourceManager,ProfilerFiller) in SimplePreparableReloadListener
- **Lines:** 41
- **Suggested Fix:** A new abstract method was added to the superclass/interface. Implement the missing method (e.g., extractWidgetRenderState for GUI widgets).

