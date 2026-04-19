# Minecraft 26.1.2 Migration Plan

## Executive Summary

The migration to NeoForge 26.1.2 has resulted in **1,406** compilation errors. These errors have been categorized to help structure the migration effort. 

**Important Note:** This document is an analysis and plan. No code has been modified to fix these errors yet.

## Error Categories and Clues for Resolution

### 1. cannot find symbol (538 errors)
This is the most common error, indicating that classes, methods, or variables have been renamed, moved, or removed in the new Minecraft/NeoForge version.

**Top Affected Files:**
- `src/main/java/doggytalents/client/DTNClientPettingManager.java` (11 errors)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/DogStatusViewBoxElement.java` (10 errors)
- `src/main/java/doggytalents/common/util/DogUtil.java` (10 errors)

**Clues & Research:**
- Many of these are likely due to mappings changes (Mojang mappings updates).
- `ResourceLocation` might have been replaced or heavily refactored (e.g., to `Identifier` in some contexts, though NeoForge usually keeps `ResourceLocation`).
- Check for renamed registry methods, block/item properties, and entity attributes.
- **`SynchedEntityData` and `EntityDataSerializers` Changes (Affecting `Dog.java`):**
    - **`defineSynchedData` Signature:** The `defineSynchedData` method in entities now takes a `SynchedEntityData.Builder` parameter. Code like `this.entityData.define(MY_DATA, 0);` should be updated to `builder.define(MY_DATA, 0);`.
    - **`EntityDataSerializer` uses `StreamCodec`:** Custom `EntityDataSerializer` implementations no longer manually handle `FriendlyByteBuf` for reading/writing. They should now be wrappers around a `StreamCodec`, typically created using `EntityDataSerializer.forValueType(MyObject.STREAM_CODEC)`. This affects custom serializers like `DoggySerializers.DOG_VARIANT_SERIALIZER`.
    - **`EntityDataSerializers` is now a Registry:** `EntityDataSerializers` is no longer a static class for direct access/registration. Custom serializers must be registered via a `DeferredRegister` using `NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS`. When defining `EntityDataAccessor` IDs, the registered serializer's supplier (`MY_SERIALIZER_REG.get()`) should be used. This impacts `EntityDataSerializers.OPTIONAL_COMPONENT` and custom `DoggySerializers`.

### 2. does not override or implement a method from a supertype (226 errors)
This occurs when a method in a base class or interface provided by Minecraft/NeoForge has changed its signature (parameters or return type).

**Top Affected Files:**
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/view/EditInfoView.java` (6 errors)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/GroupsListElement.java` (3 errors)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/TalentListEntryButton.java` (2 errors)

**Clues & Research:**
- **Entity Methods:** Methods like `tick()`, `mobInteract()`, `hurt()`, etc., often get new parameters (like `ServerLevel` instead of `Level`, or `DamageSource` changes).
- **Talents:** The base `Talent` class or interfaces it implements likely need their method signatures updated to match the new Minecraft environment.

### 3. cannot be applied to given types (160 errors)
Similar to "no suitable method found", but usually occurs when the method name is correct but the arguments are wrong.

**Top Affected Files:**
- `src/main/java/doggytalents/client/entity/render/layer/accessory/DefaultAccessoryRenderer.java` (6 errors)
- `src/main/java/doggytalents/client/entity/render/layer/accessory/IncapacitatedRenderer.java` (2 errors)
- `src/main/java/doggytalents/client/debug/SODTunningScreen.java` (2 errors)

**Clues & Research:**
- **GUI Events:** `keyPressed` and similar methods in screens/widgets likely have new signatures (e.g., taking an `InputWithModifiers` object instead of raw integers for key codes).
- **Entity Actions:** Methods like `startRiding`, `playSound`, `addEffect` may have new parameters or require `Holder` objects instead of direct instances.

### 4. incompatible types (154 errors)
This indicates a mismatch between expected and actual types, often due to generics changes or class hierarchy changes.

**Top Affected Files:**
- `src/main/java/doggytalents/client/ClientSetup.java` (10 errors)
- `src/main/java/doggytalents/client/entity/render/layer/accessory/DefaultAccessoryRenderer.java` (7 errors)
- `src/main/java/doggytalents/common/storage/DogRespawnData.java` (5 errors)

**Clues & Research:**
- **Synced Data:** `Class<Dog> cannot be converted to Class<? extends SyncedDataHolder>`. This implies that entities using synced data must now explicitly implement `SyncedDataHolder`, or the way `EntityDataAccessor` is defined has changed.
- **Entity Hierarchy:** `Dog cannot be converted to LivingEntity` in some contexts might indicate a generic type bound issue or a change in how entity types are registered.

### 5. other (148 errors)
- `src/main/java/doggytalents/common/item/ThrowableItem.java` (4 errors)
- `src/main/java/doggytalents/client/entity/model/misc/SamoyedPlushieModel.java` (3 errors)
- `src/main/java/doggytalents/client/entity/model/misc/DogPlushieModel.java` (3 errors)

**Clues & Research:**
- **Protected Access:** `random has protected access in Level`. This means direct access to `Level.random` is no longer allowed; use `Level.getRandom()` instead.
- **Constructor Changes:** `no suitable constructor found for EntityModel(no arguments)`. Constructors for models might now require parameters (e.g., `ModelPart`).
- **Type Argument Bounds:** `type argument SamoyedPlushie is not within bounds of type-variable T`. This indicates generic type parameter issues, possibly due to changes in base class definitions.

### 6. no suitable method found (118 errors)
This happens when calling a method with arguments that no longer match any available method signature.

**Top Affected Files:**
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/DogStatusViewBoxElement.java` (5 errors)
- `src/main/java/doggytalents/common/item/SakeItem.java` (3 errors)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/StatsView/view/StatsGeneralView.java` (2 errors)

**Clues & Research:**
- **Recipes (`DTRecipeProvider`):** In 26.1.2, `ShapedRecipeBuilder.shaped()` and `ShapelessRecipeBuilder.shapeless()` now require a `HolderGetter<Item>` as the first argument, or use an `ItemStackTemplate`. The old signatures `shaped(RecipeCategory, Item)` are gone.
- **GUI Rendering (`GuiGraphics`):** The `blit` methods have changed significantly. Many now require a `RenderPipeline` as the first argument, and some use `Identifier` instead of `ResourceLocation`.
- **Entity Queries:** `Level.getEntitiesOfClass` might have stricter type bounds or require different Predicate types.

### 7. is not abstract and does not override abstract method (40 errors)
This happens when an interface or abstract class adds a new abstract method that custom classes must implement.

**Top Affected Files:**
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/TalentListEntryButton.java` (1 error)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/CombatReturnSwitch.java` (1 error)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/AccessoryStatusHover.java` (1 error)

**Clues & Research:**
- **GUI Widgets:** Abstract methods in `AbstractButton` or `AbstractWidget` have changed. For example, `onPress` might now take `InputWithModifiers`, and rendering methods like `extractContents` or `extractWidgetRenderState` have been introduced and must be implemented.
- **Render Layers:** `RenderLayer` implementations now require a `submit(PoseStack,SubmitNodeCollector,int,Dog,float,float)` method.

### 8. has private access (22 errors)
Fields or methods that were previously public or protected are now private.

**Top Affected Files:**
- `src/main/java/doggytalents/common/util/dogpromise/promise/DogDistantTeleportToOwnerCrossDimensionPromise.java` (2 errors)
- `src/main/java/doggytalents/common/util/dogpromise/promise/DogDistantTeleportToOwnerPromise.java` (2 errors)
- `src/main/java/doggytalents/client/screen/DogNewInfoScreen/element/view/MainInfoView/view/ArtifactsView/ArtifactEditElement.java` (1 error)

**Clues & Research:**
- **ChunkPos:** `x` and `z` fields in `ChunkPos` might be private now; use getter methods like `x()` or `z()`.
- **Inventory:** Direct access to `items` or `selected` fields in inventories might be restricted; use provided getter/setter methods.

## Recommended Action Plan

1.  **Fix API Source Set Compilation:** Address the `package does not exist` errors for `doggytalents.api.*` first. This is likely a Gradle configuration issue where the `main` source set cannot see the `api` source set during compilation, or the `api` source set itself is failing to compile due to missing NeoForge dependencies.
2.  **Update Mappings and Basic Renames:** Tackle the `cannot find symbol` errors by updating class names, method names, and field names to match the 26.1.2 mappings. Prioritize `SynchedEntityData` and `EntityDataSerializers` changes as detailed above.
3.  **Update Method Signatures:** Go through the `does not override` and `is not abstract` errors. Update the signatures of overridden methods in entities, blocks, items, and GUI widgets.
4.  **Fix Method Calls:** Address `no suitable method found` and `cannot be applied to given types`. Update calls to Minecraft/NeoForge methods (like `GuiGraphics.blit`, `RecipeBuilder.shaped`, etc.) to provide the correct arguments.
5.  **Resolve Type Incompatibilities:** Fix `incompatible types` by adjusting generics, implementing new required interfaces (like `SyncedDataHolder`), and using `Holder`s where necessary.
6.  **Fix Access Issues:** Replace direct field access with getters/setters for the `has private access` errors.