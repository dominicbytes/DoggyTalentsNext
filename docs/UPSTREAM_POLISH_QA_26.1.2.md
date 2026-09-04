# Upstream Polish QA (NeoForge 26.1.2)

## Scope

- Contracts: `UPSTREAM-DISABLE-01`, `DOGGY-SPIN-01`, and `ANIM-DEBUG-ROT-01`.
- Ports DashieDev's current disablement of the plague doctor mask, three dragon costume pieces, and sussy sickle, including generated-resource cleanup.
- Loads the loading-icon custom dog models from bundled model JSON, adds WangWang as an in-mod loading-spinner option, and adds the two WangWang dog-name entries.
- Expands animation-debug rotation locking to body yaw, head yaw, head pitch, banking, and tail pitch.
- Retains the product identity and metadata as **Doggy Talents Next**. DashieDev's WangWang repository banner/icon branding commit is intentionally excluded.

Upstream code/data lineage: `385995b47`, `27c4a28c5`, `5cb616828`, `54256285f`, `ff758ac41`, `4b2e58252`, `c5ecc81cc`, `2b34f7ba4`, and `e51f96f94`.

## NeoForge 26.1.2 adaptations

- Preserves the 26.1 render-state and `GuiGraphicsExtractor` entry points while applying the expanded debug controls.
- Uses the 26.1 single-argument NBT presence check and preserves legacy lowercase `yrot` save compatibility.
- Resolves bundled model resources through the 26.1 `JarContents` content roots without mixing filesystem providers.
- Removes the five 26.1 namespace-local item definitions in addition to DashieDev's generated models, recipes, and recipe advancements.

## Regression proof

- `UPSTREAM-DISABLE-01` verifies all five item registrations are absent, all four accessory registrations are absent, and their item definitions, model geometry, recipes, and recipe advancements are not bundled.
- `DOGGY-SPIN-01` verifies the three bundled custom model JSONs, WangWang texture/style/name entries, and removal of the redundant Amaterasu/Hope Java model classes.
- `ANIM-DEBUG-ROT-01` verifies all five rotation channels survive NBT and network round trips, remain attached to debug state, and load the legacy lowercase `yrot` value.

## Automated verification

- Focused parity contracts: **passed**.
- Clean unit build: **53/53 passed** across 23 test classes.
- GameTest server: **14/14 required tests passed** (13 DTN tests plus the vanilla test).
- Strict release validator: **0 errors**.
  - Project warnings: 2 known shared-namespace warnings.
  - Built artifact: 0 errors, 0 warnings.
- Exact tested artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `b0a8dd1c06a79bfa30d63cdcf53a69a1aaa20fdc0305acb336566ed204118d05`
- Production-style dedicated server reached `Done (0.280s)!` with the exact tested artifact.

## Client gate

The isolated NeoForge client started with master volume at `0.0`. `ClientSetup` completed `DoggySpinModel.init()` without an exception, the mod remained listed as `Doggy Talents Next 26.1.2.24`, all 141 dog-skin entries loaded, all 81 dog models loaded, and all built-in animations loaded successfully. No WangWang banner or icon branding file is part of this diff.

Pixel-level interaction with the randomized loading spinner and the expanded debug-mode selector remains a human visual follow-up; the client startup/model-loading and executable state/resource contracts are complete.

## Verdict

**PR_READY** for this combined parity slice. All automated, loader, artifact, production-server, and client-startup gates passed; multiplayer remains outside the user-approved scope.
