# Walk Animation Registry QA (NeoForge 26.1.2)

## Scope

- Contract: `ANIMATION-REGISTRY-01`
- Upstream sources: DashieDev commits `2e01cd84c` and `00cd442a8`
- Registers `SLOW_TROT` (stable ID 94, 20 ticks) and `GALLOP` (stable ID 95, 10 ticks).
- Both animations retain the upstream `freeHead` behavior.
- `DogModel` queries the authoritative `DogAnimationRegistry` map directly; no duplicate mutable animation caches were added.

## Regression proof

The focused test was added before the implementation. Before the port, stable ID 94 resolved to `NONE`. The completed test verifies the stable IDs and metadata, and verifies that both canonical lowercase animation resources exist and decode successfully.

## Automated verification

- Clean unit build: **43/43 passed**.
- GameTest server: **12/12 required tests passed** (11 DTN tests plus the vanilla test).
- Strict release validator: **0 errors**.
  - Project warnings: 2 known shared-namespace warnings.
  - Built artifact: 0 errors, 0 warnings.
- Exact tested artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `dc754920e966c0c20c2545f15d17a2675fd57590dd508f5898f62128c5355522`
- Production-style dedicated server reached `Done (0.356s)!` with the exact tested artifact, then stopped cleanly with all dimensions saved.

## Manual gate

`ANIMATION-REGISTRY-01-VISUAL` was not run. An in-client check must still confirm that slow trot and gallop select and render correctly on a dog model.

## Verdict

**PROTOTYPE_ONLY** until `ANIMATION-REGISTRY-01-VISUAL` passes. This slice is not marked `PUBLIC_RELEASE_READY` solely because the visual client gate remains manual.
