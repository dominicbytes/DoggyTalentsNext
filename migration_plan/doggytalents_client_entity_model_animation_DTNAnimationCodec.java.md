# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/model/animation/DTNAnimationCodec.java`

Total Errors: 3

## Error: invalid method reference
- **Lines:** 218
- **Suggested Fix:** The `com.mojang.serialization.Codec` API has undergone significant changes in Minecraft 1.21 (NeoForge 26.1.2). The `xmap` method, used for mapping between types in a `Codec`, likely has a different signature or expects different functional interfaces.

    **Concrete Change:**
    The usage of `Codec.STRING.xmap(DTNAnimationCodec::getInterpFromId, DTNAnimationCodec::getIdFromInterp)` needs to be updated to conform to the new `com.mojang.serialization.Codec` API. This will require consulting the updated documentation or examples for `Codec` usage in 1.21.

## Error: cannot find symbol
- **Lines:** 142, 164
- **Suggested Fix:** These errors occur during the initialization of `KEYFRAME_PROCESSORS` using `KeyframeProcessor.of`. While the method references passed to `KeyframeProcessor.of` (`KeyframeAnimations::posVec`, `KeyframeAnimations::degreeVec`, `DTNAnimationCodec::invertedRotationVec`) appear to match the `KeyframeValueFunction` interface, the `cannot find symbol` error here is likely a cascading effect of the underlying `com.mojang.serialization` API changes. The compiler might be unable to correctly resolve types or method calls within the context of the `Codec` and `RecordCodecBuilder` refactoring.

    **Concrete Change:**
    Address the broader `com.mojang.serialization` API changes first. Once the `Codec` and `RecordCodecBuilder` usage throughout the `DTNAnimationCodec` class is updated to the new 1.21 API, these `cannot find symbol` errors related to `KeyframeProcessor.of` should either resolve themselves or become clearer, allowing for more targeted fixes. This will involve a comprehensive refactoring of the serialization logic in this class.