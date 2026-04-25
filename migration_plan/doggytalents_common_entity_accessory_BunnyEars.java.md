# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/accessory/BunnyEars.java`

Total Errors: 1

## Error: no suitable method found for spawnAtLocation(ItemStack,float)
- **Lines:** 50
- **Suggested Fix:** The `spawnAtLocation` method in `net.minecraft.world.entity.Entity` (which `Dog` extends) has a signature `public @Nullable ItemEntity spawnAtLocation(ServerLevel level, ItemStack itemStack, float offset)` that matches the original call. The error is likely due to the explicit cast `(net.minecraft.server.level.ServerLevel) dog.level()` failing because `dog.level()` might not always be a `ServerLevel`.

    **Concrete Change:**
    Check if `dog.level()` is an instance of `ServerLevel` before casting and calling `spawnAtLocation`.

    **Example:**
    ```java
    // Original:
    // dog.spawnAtLocation((net.minecraft.server.level.ServerLevel) dog.level(), new ItemStack(item), 0.0F);
    // Proposed:
    if (dog.level() instanceof ServerLevel serverLevel) {
        dog.spawnAtLocation(serverLevel, new ItemStack(item), 0.0F);
    }
    ```