# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/model/animation/DTNModelLoader.java`

Total Errors: 3

## Error: incompatible types: Gson cannot be converted to Codec
- **Lines:** 31
- **Suggested Fix:** The constructor for `net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener` no longer accepts a `Gson` object. Instead, it now requires a `Codec` for the data type it is loading.

    **Concrete Change:**
    1.  Update the class declaration to specify the type of data being loaded (e.g., `AnimationDefinition`).
    2.  Modify the `super()` call in the constructor to pass the appropriate `Codec` and a `FileToIdConverter`.

    **Example:**
    ```java
    // Original class declaration:
    // public class DTNModelLoader extends SimpleJsonResourceReloadListener {
    // Proposed class declaration:
    public class DTNModelLoader extends SimpleJsonResourceReloadListener<AnimationDefinition> {
        // ...
        private DTNModelLoader() {
            // Original:
            // super(new Gson(), createRegistryPath());
            // Proposed:
            super(DTNAnimationCodec.CODEC, new FileToIdConverter("dog_models")); // Assuming "dog_models" is the base directory for the JSON files.
        }
        // ...
    }
    ```
    You may need to adjust the `FileToIdConverter` parameter based on the exact path structure.

## Error: method does not override or implement a method from a supertype
- **Lines:** 39
- **Suggested Fix:** This error is a cascading effect of the incorrect `super()` call in the constructor. The `apply` method signature in `SimpleJsonResourceReloadListener<T>` is `protected void apply(Map<Identifier, JsonElement> contents, ResourceManager resourceManager, ProfilerFiller profiler)`. The current `apply` method in `DTNModelLoader` matches this signature.

    **Concrete Change:**
    Once the constructor (`super()` call) is correctly updated to parameterize `SimpleJsonResourceReloadListener` with `AnimationDefinition`, this error should resolve itself as the compiler will correctly infer the `T` type for the `apply` method.

## Error: DTNModelLoader is not abstract and does not override abstract method apply(Object,ResourceManager,ProfilerFiller) in SimplePreparableReloadListener
- **Lines:** 23
- **Suggested Fix:** This error is also a cascading effect of the incorrect `super()` call in the constructor. The compiler is inferring `Object` as the type for `T` in `SimplePreparableReloadListener` because the `super()` call is incorrect.

    **Concrete Change:**
    Once the constructor (`super()` call) is correctly updated to parameterize `SimpleJsonResourceReloadListener` with `AnimationDefinition`, this error should resolve itself.