# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/entity/model/dog/VariantDogModel.java`

Total Errors: 2

## Error: cannot find symbol
- **Lines:** 146, 147
- **Suggested Fix:** The `copyFrom` method of `net.minecraft.client.model.geom.ModelPart` has been removed in Minecraft 1.21 (NeoForge 26.1.2). Instead of using `copyFrom`, you need to explicitly set the position and rotation of the target `ModelPart` using its `setPos` and `setRot` methods.

    **Concrete Change:**
    Replace the `copyFrom` calls with explicit `setPos` and `setRot` calls.

    **Example:**
    ```java
    // Original:
    // this.realTail2.copyFrom(this.realTail);
    // this.realTail3.copyFrom(this.realTail);

    // Proposed:
    this.realTail2.setPos(this.realTail.x, this.realTail.y, this.realTail.z);
    this.realTail2.setRot(this.realTail.xRot, this.realTail.yRot, this.realTail.zRot);
    this.realTail3.setPos(this.realTail.x, this.realTail.y, this.realTail.z);
    this.realTail3.setRot(this.realTail.xRot, this.realTail.yRot, this.realTail.zRot);
    ```