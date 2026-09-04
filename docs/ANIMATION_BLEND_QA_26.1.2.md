# Animation Blend QA (NeoForge 26.1.2)

## Scope

- Contract: `ANIMATION-BLEND-01`
- Ports DashieDev's animation cross-fade backend and its final blend tuning from `1.21-master`.
- Supports procedural-to-animation blend-in, animation-to-animation cross-fade, and animation-to-procedural blend-out.
- Captures procedural pose state and interrupted keyframe time for deterministic transitions.
- Uses five ticks for the default blend-in and three ticks for the default blend-out.
- Keeps `STAND_QUICK` and `BACKFLIP` on a three-tick full-body blend; `STAND_UP` uses the five-tick head-rotation-and-children mode.
- Preserves NeoForge 26.1's `DogRenderState` model entry point.

Primary upstream commits: `85d1073ce`, `a152487ed`, `e0de8dd16`, `38558e101`, `57897019f`, `753c8b7e6`, `a762a270f`, `0e7823bd1`, `fa805b58e`, `cb4371843`, `0295c17d4`, `aa9bd95d7`, `a988bf3d5`, `c91064847`, `75ce8f6cc`, `5a134a4bb`, and `6b69db11c`. Supporting upstream refactors are included where required by this state machine.

## Compatibility adaptations

- Retains the public `IDog#getWagAngle`, `getShakeAngle`, and `getInterestedAngle` contracts as deprecated delegates instead of deleting them.
- Retains the deprecated public `DogModel#animateStandWalking` and legacy shaking/begging overloads for addon compatibility.
- Restores the NeoForge 26.1 renderer's required `createRenderState` method after replaying the older renderer history.
- Animation-manager mutable state remains confined to the owning dog's normal entity tick/render lifecycle; no background threads or new synchronization paths are introduced.

## Regression proof

The focused contract was added before implementation and initially failed compilation because the blend modes, records, and accessors did not exist. The completed tests verify:

- default and special-case blend configuration;
- blend direction classification and duration selection;
- shortest-arc rotation interpolation with radians at the model boundary.

## Automated verification

- Clean unit build: **48/48 passed** across 20 test classes.
- GameTest server: **12/12 required tests passed** (11 DTN tests plus the vanilla test).
- Strict release validator: **0 errors**.
  - Project warnings: 2 known shared-namespace warnings.
  - Built artifact: 0 errors, 0 warnings.
- Exact tested artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `86bcaf8a473c3e9e2d2ae6d256a96bab0379bc0e6d95f8be3ac97307b3fe5cf2`
- Production-style dedicated server reached `Done (0.378s)!` with the exact tested artifact, then stopped cleanly with all dimensions saved.

## Manual gate

`ANIMATION-BLEND-01-VISUAL` was not run. An in-client check must still confirm smooth pose transitions for blend-in, animation-to-animation, and blend-out, including the special `STAND_UP`, `STAND_QUICK`, and `BACKFLIP` settings.

## Verdict

**PROTOTYPE_ONLY** until `ANIMATION-BLEND-01-VISUAL` passes. This slice is not marked `PUBLIC_RELEASE_READY` solely because the visual client gate remains manual.
