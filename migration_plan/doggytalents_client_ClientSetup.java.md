# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/ClientSetup.java`

Total Errors: 10

## Error: incompatible types: bad return type in method reference
- **Lines:** 179, 180, 181, 183, 184, 185, 186, 187, 189, 190
- **Suggested Fix:** The `RenderLayer` class in Minecraft 1.21 (NeoForge 26.1.2) is now parameterized by `S extends EntityRenderState` and `M extends EntityModel<? super S>>`. The project's custom `RenderLayer` implementations (e.g., `DogVariantRenderer`, `DefaultAccessoryRenderer`, etc.) are currently parameterized with `Dog` (the entity type) as the first argument.

    **Concrete Change:** For each `RenderLayer` implementation registered in `CollarRenderManager.registerLayer`, change its inheritance to use `DogRenderState` as the first type parameter instead of `Dog`.

    **Example:**
    Change `public class DogVariantRenderer extends RenderLayer<Dog, DogModel>`
    To `public class DogVariantRenderer extends RenderLayer<DogRenderState, DogModel>`

    This change needs to be applied to the following files:
    - `src/main/java/doggytalents/client/entity/render/layer/DogVariantRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/DogCustomGlowingOverlayRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/DefaultAccessoryRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/DogWolfArmorRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/IncapacitatedRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/DoggyArmorRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/PackPuppyRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/RescueDogRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/AccessoryModelRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/DogMouthItemRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/TorchDogRenderer.java`
    - `src/main/java/doggytalents/client/entity/render/layer/FisherDogRenderer.java`