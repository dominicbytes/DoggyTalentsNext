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
- normal `BACKFLIP` and `SCRATCHIE` completion returns to the procedural pose outside animation-debug mode;
- summon data without `dogHunger` preserves the initialized hunger value instead of starting the dog at zero hunger;
- a newly summoned dog remains alive at unchanged health and hunger during the regression window.

During visual review, repeated activation of the animation-debug stick was initially reported as a freeze. This is intentional frame-inspection behavior: the second activation captures the current timestamp and pauses the selected animation, while sneak-use exits debug mode. It is not a normal animation-completion failure.

The visual run also exposed a separate summon-path regression. Missing `dogHunger` data was interpreted as explicit zero hunger, so a freshly summoned dog starved until animation-debug mode paused hunger processing. The loader now preserves the entity's initialized hunger unless the serialized field is present. This behavior is covered by `ANIMATION-BLEND-01-SPAWN-HEALTH`.

## Automated verification

- Clean unit build: **48/48 passed** across 20 test classes.
- GameTest server: **14/14 required tests passed** (13 DTN tests plus the vanilla test).
- Strict release validator: **0 errors**.
  - Project warnings: 2 known shared-namespace warnings.
  - Built artifact: 0 errors, 0 warnings.
- Exact tested artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `95f5fb441d034f25569ab6f4d5bc7ea1721364f3a05901ec14f5e90d59cf51b8`
- Production-style dedicated server reached `Done (0.336s)!` with the exact tested artifact, then stopped cleanly with all dimensions saved.

## Manual gate

`ANIMATION-BLEND-01-VISUAL` passed human review in the isolated NeoForge 26.1.2 client with master volume muted. All selected animations rendered and completed when normally activated. Debug-stick pause/resume and exit behavior matched its documented controls. After the summon-path fix, a newly summoned dog remained at `8.0f` health across two readings without entering debug mode or displaying repeated damage.

## Verdict

**PUBLIC_RELEASE_READY** for the `ANIMATION-BLEND-01` slice. All applicable automated, artifact, production-server, and human visual gates passed; multiplayer remains outside the user-approved scope.
