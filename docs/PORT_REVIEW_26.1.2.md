# Port review — 2026-09-04

## Verdict and scope

The port is substantially implemented, but **full behavioral parity and general-release readiness are not yet demonstrated**. Earlier “complete gameplay coverage” language was too strong. Passing helper/serialization tests does not prove every talent, command, AI goal, screen, or rendering combination works in play.

This review supersedes current-status claims in `CLAUDE.md` and `MIGRATION_AUDIT_26.1.2.md`; their historical evidence remains useful. It reviews the merged continuation through PR #39 at `f898c7c488c93df7addd27dc66e5d26fea06b5cf`, the implementation diffs, QA reports, automated tests, and inherited differences from the original. It is not a claim of exhaustive execution of every source path.

The user explicitly authorized continuing the source port rather than waiting for the preflight replacement-test route. Target: Minecraft **26.1.2**, NeoForge **26.1.2.101**, Java **25**. Product name and copyright notices stay unchanged. Accepted upstream disabled features stay disabled; WangWang content is allowed, but its product rebranding is excluded. Multiplayer acceptance is waived; BytecraftPack/Passive Mobs compatibility is deferred. This review does not grant pack admission or publish a release.

## Reference correction

| Reference | Immutable commit | Purpose |
|---|---|---|
| MiiRaGe stable | `dc93249f12e79f0c494cece04b08674ed4a1a41d` | Port implementation base |
| MiiRaGe beta | `c123cb668cad873172487e76e505cda7db314b11` | Historical comparison only |
| DashieDev original fork point | `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2` | Original serializer/behavior reference |
| DashieDev inspected 1.21-master | `061ab865f6aa6498db3f30e277ecd4211659de97` | Later accepted animations, skins, UI/content changes; rebranding excluded |

The old plan said newer DashieDev changes were not scope authority. Subsequent user approvals explicitly incorporated them. Future comparisons must name which immutable source behavior applies and record exclusions, rather than compare everything to the original fork point or automatically import the moving branch tip.

## Preflight and plan findings

1. The September 3 preflight correctly established fork identity, exact-target artifacts, and the need for runtime testing. Its README-derived “networking pending,” “data components pending,” and `@OnlyIn` warnings were leads, not verified current defects. MiiRaGe had already removed the obsolete annotations; the latest cleanup corrected documentation, not Java annotations.
2. The preflight's “do not proceed to Modmaker” next action was superseded by the user's explicit implementation request. Preserve the original report as historical evidence with an addendum; do not rewrite its observed artifact or registry snapshot.
3. The April `CLAUDE.md` proposal is not the implemented architecture. NeoForge payload transport can retain compatibility wrappers with explicit flow checks. `DataComponents.CUSTOM_DATA` is a supported migration boundary; wholesale conversion to bespoke typed components is not a parity requirement.
4. The test contract must separate registration, serialization, helper behavior, world interaction, process restart, and human visual acceptance. The old `NET-02` ownership gate is not satisfied by a unit method that reuses that label for discriminator order; ownership/multiplayer acceptance remains waived, not passed.
5. Old slice-level `PUBLIC_RELEASE_READY` conclusions cannot establish readiness of the entire current project. A historical production-JAR run proves that historical hash, not every subsequent build.

## Corrections in this review

| Finding | Correction | Verification |
|---|---|---|
| CI ran `clean build` but omitted loader GameTests | Add `runGameTestServer` and upload unit reports/server log | Local loader run; CI execution checked on review PR |
| Metadata advertised any 26.x Minecraft/NeoForge, and publishing listed 26.1/26.1.1 | Require Minecraft `[26.1.2]`, NeoForge `[26.1.2.101,26.1.3)`; advertise only `target_mc_version` | Fresh build and strict artifact validator |
| Loot regression checked rice and only zombie soy success | Extend existing LOOT-01 to all four supported hostile mobs; append LOOT-02 exclusions for shears, other block tables, passive victims, and non-dog attackers | Registered-modifier GameTest with fixed random seeds |
| README/plan/audit overstated closure or retained resolved defects | Qualify README and mark historical documents superseded by this report | Documentation/diff review |

LOOT-02 is a contract erratum, not an additional registered test count. These checks call the real registered global modifiers with valid loot contexts; they do not simulate combat deaths or statistically prove drop rates. NeoForge 26.1.2 discovers typed loot-modifier JSON directly. Do not add the old `entries`/`replace` index format as a purported missing fix.

## Accepted inherited gameplay differences

These differences were inherited from MiiRaGe. On 2026-09-04 the user explicitly chose to retain them, including the registered Hunter Dog talent. They are accepted parity exceptions, not missing port work. Their original motivation is not established by this review; do not assume they are required API/compatibility changes. No rebalance or registry removal is applied.

| Talent | DashieDev behavior | Port behavior |
|---|---|---|
| Bed Dog, levels 1–5 | Hunger cost 40/40/40/30/0; cooldown days 3/3/3/2/0 | 40/35/30/20/0; days 5/4/3/2/0 |
| Rescue Dog | Hunger cost 20 below level 5, then 10 | 20/17/14/11/8 |
| Roaring Gale | Hit cooldown 100 ticks below level 5, then 60; miss half | 160/130/100/70/40; miss half |
| Creeper Sweeper | Scan range `level * 5` | `level * 6` |
| Mob Retriever | Width restrictions apply at every level | Level 5 bypasses them |
| Happy Eater | Talent handler's additional food is fish | Also accepts rotten flesh from level 3 |
| Hunter Dog | Registration disabled; former looting implementation commented out | Registered talent with chance to duplicate mob drops |

Future gameplay tests should use these accepted fork behaviors as the oracle for the listed differences, and immutable DashieDev behavior elsewhere. Preserve Hunter Dog's registry ID and saved state. Boundary-level gameplay verification remains part of REVIEW-GAMEPLAY-01; accepting the design is not a runtime test pass.

## Evidence reconciliation

| Area | What is established | What is not established |
|---|---|---|
| Build/toolchain | Portable Java 25 build and production packaging; CI build history | Every future 26.x release; compatibility metadata is narrowed accordingly |
| Network | Direction checks and selected bounded codecs/legacy formats | Comprehensive per-message budgets, malformed/trailing policy, adversarial channel behavior; multiplayer waived |
| Persistence | Dog/block/item data, legacy payload fixture, malformed inputs, historical two-process production save/restart | Authentic original-world upgrade; latest production artifact restart |
| Gameplay | Food consumption/effects, wolf conversion, representative interactions and combat/care helpers, registered loot hooks | Full level-by-level talent outcomes, sustained AI behavior, command execution/permission results |
| Talent catalog | All 33 registered port talents instantiate and serialize | All 33 are original enabled talents or work end-to-end |
| Commands | `/dog locate` and `/dog revive` nodes exist | Commands successfully locate/revive with correct constraints and messages |
| Rendering/UI | Implemented tint, bed, helmet/trim, nameplate and animation changes; helper/resource tests; user animation/health acceptance | Full adult/puppy/custom-model/accessory/item-view/config matrix on final artifact |
| Legacy fixture | Checksum-locked payload reconstructed from immutable original serializer | A payload/world captured from a running original release |
| Resource/content | Item definitions, curated skin catalog and accepted upstream content changes | Visual fidelity, sound, every recipe/use path, reload in a final clean client |

Historical evidence: [save/restart](SAVE_RESTART_QA_26.1.2.md), [legacy fixture](LEGACY_SAVE_FIXTURE_QA_26.1.2.md), [production server](PRODUCTION_SERVER_QA_26.1.2.md), [food/training](GAMEPLAY_FOOD_TRAINING_QA_26.1.2.md), [animation](ANIMATION_BLEND_QA_26.1.2.md), [skin catalog](SKIN_CATALOG_QA_26.1.2.md). Consult each report's artifact and scope; none is a universal pass.

## Remaining acceptance work, in order

These appended review IDs define the next work; they do not retroactively mark old gates passed.

| ID | Work and acceptance criterion | Status |
|---|---|---|
| REVIEW-PARITY-01 | Decide and document inherited talent differences, retaining saved IDs; assign runtime verification to REVIEW-GAMEPLAY-01 | PASS — user chose to retain the fork differences on 2026-09-04 |
| REVIEW-GAMEPLAY-01 | Exercise talent effects at relevant level boundaries, cooldowns/hunger, AI follow/attack/avoid/retrieve/fish, and incapacitation/recovery through actual events/ticks | BLOCKED — coverage incomplete |
| REVIEW-COMMAND-01 | Execute locate/revive and tracking/whistle flows; test valid, invalid, unavailable-target and permission outcomes | BLOCKED — current command test is registration only |
| REVIEW-VISUAL-01 | Final-artifact captures: adult/puppy and default/custom models, skins, armor/trim/helmet/accessories, wet/incapacitated states, nameplate settings, tints and bed materials in inventory/hand/ground; menus, reload, howl/audio manual acceptance | BLOCKED — partial earlier human review only |
| REVIEW-UPGRADE-01 | Back up an authentic 1.21 source world; upgrade a copy with dogs, inventories, beds/bowls/mills and saved data; restart and compare documented state | BLOCKED — authentic fixture unavailable; alternatively explicitly limit the upgrade claim |
| REVIEW-RELEASE-01 | Final candidate clean client install/play/save/restart and server startup; log review; unique version/changelog, fork issue/update destinations, checksums and GitHub release approval | BLOCKED — final artifact/metadata acceptance pending |
| REVIEW-MP-01 | Two-client ownership, synchronization, reconnect, hostile packet/channel testing | WAIVED — user's upstream-only multiplayer scope |
| REVIEW-PACK-01 | BytecraftPack/Passive Mobs compatibility and independent pack admission | DEFERRED — user request |

Counts for these eight appended acceptance IDs: six required, one passed by explicit scope decision, five blocked by incomplete acceptance evidence, zero demonstrated failing acceptance runs, one waived, one deferred. None of the five runtime/release gates has complete execution evidence; portions are already implemented/tested as described above. Visual and authentic-upgrade gates require manual evidence; these counts are not the automated suite totals.

## Better porting approach

- Freeze behavior by subsystem against immutable original commits and maintain a small explicit deviation list. Do not derive expected values from the port's own implementation.
- Keep compatibility adapters where they preserve IDs/data and reduce risk. Replace an adapter only for a verified API, side-safety, or behavior problem; avoid wholesale rewrites to satisfy old proposal wording.
- Extend existing tests into real outcomes rather than add more catalog/helper-only checks. Pair each correction with its source-backed regression and a focused runtime scenario.
- Make CI run loader tests and retain raw evidence. Track exact JAR hashes for client/restart gates, rather than carrying their result forward after source changes.
- Close one final-artifact acceptance matrix, then version and release. Do not equate accumulated merged PRs with full-port acceptance.

## This review's rerun

Both the focused and final aggregate loader runs passed all **22 required tests** (21 DTN plus one vanilla control), including the additional LOOT-01/02 assertions. The final clean build passed **57 JUnit tests across 25 suites**, with zero failures/errors. Commands ran in `doggy-talents-next-review-01` on Windows using the pinned target above; final aggregate run completed at 2026-09-04 19:30 UTC, exit 0, 45 seconds.

- `gradlew.bat clean build runGameTestServer --no-daemon`: PASS; evidence `release/review-build.log`, `run/logs/latest.log`, `build/test-results/test/`.
- `validate_mod_project.py . --target-version 26.1.2 --loader neoforge --strict-release --require-stable-loader --report-json release/validation-report.json`: PASS; fresh build exit 0, outer validator zero errors/warnings. Its nested JAR audit emitted the two known intentional shared-namespace warnings (`data/minecraft`, `data/neoforge`). Runtime/public-release classification is explicitly `NOT_EVALUATED` by that validator.
- Two consecutive clean builds of the corrected source produced the identical production JAR SHA-256: `c77459003717cc49b16bb1d85c6336248ff8047f0ef35a49620b05f00b344b1a` (11,422,478 bytes).
- No `ERROR`/exception entries in the final GameTest log; the malformed legacy fixture deliberately warns about `missing:entity`. General compiler/Gradle deprecation notices remain; these are not the removed `@OnlyIn` warnings.
- Concrete diff review and `git diff --check`: no blocking findings. Gameplay rebalance is deliberately not part of the diff.

Raw evidence and the production artifact are copied under `bytecraftpack/mod-development/doggy-talents-next-neoforge-26.1.2/evidence/review-2026-09-04/`. No automatic client launch, rebalance, multiplayer test, pack admission, or release publication is performed by this review. Overall acceptance remains `PROTOTYPE_ONLY` until the required evidence above is completed or explicitly scoped out; this is not a claim that the tested paths failed.
