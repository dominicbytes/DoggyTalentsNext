# Headless gameplay completion

User accepted continuation of REVIEW-GAMEPLAY-01 on 2026-09-04, from merged PR #42 (`ea94d10e36e0cbe3663d01ea66dcfa97a14334c5`). This is implementation/testing under Minecraft Modmaker, not a new preflight or pack-admission task. Target and accepted original/fork oracles are unchanged from PORT_REVIEW_26.1.2.md.

## Appended contract

Each row requires executable server outcomes, not catalog/helper presence. State setup may use public APIs or saved-state fixtures; actions must use real interaction/event dispatch or normal ticks. Client appearance/input and audio remain separate. Status for every new row is initially UNEXECUTED.

| Stable ID | Required outcomes |
|---|---|
| COMPLETE-DEFENSE | Pillow Paw fall/trample, Shock Absorber knockback/explosion and Hell Hound fire/freeze boundaries |
| COMPLETE-COMBAT | Black Pelt critical hit; Creeper Sweeper kill; Hunter Dog actual loot duplication; Ookamikaze consumption/explosion/cooldown |
| COMPLETE-CARE | Happy Eater food boundaries, Poison Fang curing, Chemi Canine transfer/cost, Water Holder refill/extinguishing |
| COMPLETE-RECOVERY | Bandage/Wagyu recovery and passive recovery; healthy dogs reject bandages, but retain Wagyu's intentional always-edible buff-food behavior |
| COMPLETE-SUPPLIES | Pack Puppy full/partial collection and food sharing; Doggy Torch placement/consumption/disable; Doggy Armor repair/removal |
| COMPLETE-SOCIAL | Puppy Eyes reputation/cooldown and Shepherd Dog animal movement |
| COMPLETE-TRAVEL | Wolf Mount interaction/exhaustion, Swimmer Dog submersion/rider, Flying Furball flight/glide, terrain/water follow and gate opening/closing |
| COMPLETE-WORK | Doggy Tools tool use/farming, Sniffer Dog scent consumption/detection, Bed Dog sleep/cost/cooldown |
| COMPLETE-EXTENDED | Fisher Dog no-drop/rain/cooking/treasure, Rescue Dog affordability/ownership/cooldown, Roaring Gale level/miss/expiry, Guard Dog expiry and Pest Fighter radius/max |
| COMPLETE-SOAK | Bounded long-duration server ticks with health, hunger, inventory and AI invariants |

Retain all failures and classify fixture mistakes separately from production defects. Do not close REVIEW-GAMEPLAY-01 until every required row is reconciled to executed evidence; keep source/JAR/reports under bytecraftpack/mod-development and submit concise PRs to both requested destinations.

Contract clarification before implementation: Bed Finder has no server action/command; its behavior is the client-only `BedFinderRenderer` outline while carrying the dog. Its server talent intentionally has empty callbacks. Registration/state remain covered; outline/range acceptance belongs to REVIEW-VISUAL-01, not a fabricated headless command test. Likewise dog crop trampling is globally disabled in the inherited Dog implementation; preserve that behavior at every Pillow Paw level.

## Executable reconciliation

All names below are registered `doggytalents:complete_...` GameTests. They run in separate test environments to avoid adjacent dogs, loot, world clearing and talent radii affecting another scenario. Existing tests remain registered.

| Stable ID | Executable suffixes | Result |
|---|---|---|
| COMPLETE-DEFENSE | `fall_shock`, `hell_creeper`, `explosion` | PASS |
| COMPLETE-COMBAT | `critical_loot`, `hell_creeper`, `ookami` | PASS |
| COMPLETE-CARE | `food_cure`, `chemi`, `water_holder`, `fire_drill` | PASS |
| COMPLETE-RECOVERY | `bandages`, `wagyu`, `soak` | PASS |
| COMPLETE-SUPPLIES | `pack_overflow`, `pack_sharing`, `torch`, `armor` | PASS |
| COMPLETE-SOCIAL | `puppy_eyes`, `shepherd` | PASS |
| COMPLETE-TRAVEL | `mount`, `submerged`, `flying`, `terrain_follow`, `gate`, `retrieve` | PASS |
| COMPLETE-WORK | `farming`, `sniffer`, `bed` | PASS |
| COMPLETE-EXTENDED | `fish_miss`, `fish_rain`, `fish_cook`, `fish_treasure`, `rescue_budget`, `roar_1` through `roar_5`, `guard_pest` | PASS |
| COMPLETE-SOAK | `soak` | PASS |

Ten required IDs, ten implemented/executed/passed, zero failed/blocked/manual/waived **within this headless contract**. Thirty-eight new GameTests plus the previous 38 produce 76 aggregate tests: 75 DTN and one vanilla control. These are bounded acceptance scenarios, not a claim of exhaustive path, performance, client or configuration coverage.

The immutable original/fork oracles in PORT_REVIEW_26.1.2.md still apply. In particular, preserve the inherited Shock Absorber sonic-damage multipliers, reusable Doggy Tools seed sample, Wagyu's always-edible buff-food behavior and all accepted fork balance differences. The mistaken contract reference to cake was corrected to the actual starvation recovery item, Golden A Five Wagyu.

## Production corrections

1. **Bed Dog missing sleeper lookup:** an actual sleep pair followed by a lookup of another player crashed the server in `gradle-2680c17d.log`. Return an empty optional for a missing pair. The same sleep test now checks lookup, dispatched sleep completion, hunger cost, retraining restriction and cooldown expiry.
2. **Premature underwater navigation:** a dog following across a shallow-water/raised-step corridor switched to underwater navigation with only its feet wet, lost its path and stopped. Reproductions include `gradle-6800546e.log` and the trace in `gradle-26dfbe63.log`. Require actual submersion before switching. Exploratory follow-goal/node-evaluator changes were removed; the final correction is one condition in `DogSwimmingManager`. Surface follow and submerged rider tests both pass.
3. **Gunpowder direction precision:** normalized nonzero horizontal vectors can have a computed length slightly below one. The old `< 1` guard rejected valid trajectories; use a near-zero squared-length guard instead. The actual Treat Bag/projectile/action test verifies consumption, target damage, self/terrain safety and normal cooldown expiry. The earlier intermittent action miss is retained in `gradle-b8024513.log`; the precision defect is also established by code inspection, not a claim that every projectile angle was exhaustively tested.

No branding, copyright, saved IDs or accepted talent balance was changed. These are correctness fixes, not a talent redesign.

## Fixture rigor and limitations

- Entity creation starts a dog at eight health; taming changes maximum health. Healthy fixtures now explicitly fill health after taming, and again after Kami-level changes. This prevents the fire-rescue test from accidentally starting with a nearly defeated dog.
- Fake players do not perform ordinary player physics and refuse mounting. The rider fixture uses a normal offline `ServerPlayer` with the existing fake connection's packet sink, without login or a DTN handshake. Its normal player tick is driven for submerged riding. This proves server mount/effect state, not client input, packet delivery or multiplayer.
- Fishing controls only the random branches near a naturally initiated shake; it never invokes the talent's shake callback. Named vanilla fishing loot sequences are reset through the real command dispatcher for repeatable cooking/treasure draws. An inherited `random` field initially shadowed the fixture variable; the variable was renamed. Random drop-rate distributions are not being measured.
- Rain uses a plains biome, real weather and removal of the test arena's enclosing barrier roof. That roof blocks the precipitation heightmap even when `canSeeSky` is true. The negative rain branch now includes a real wet/dry cycle.
- Ordinary follow starts below the twelve-block teleport threshold and above the ten-block follow threshold. Terrain follow requires observed water contact and arrival, rather than accepting teleportation.
- Recovery fixtures shorten only the remaining injury timer after actual incapacitation. Eight real bandage/Wagyu interactions, their cooldowns and normal recovery ticks remain live. The soak runs 6,000 normal server ticks; passive recovery is asserted after 1,100 ticks. This is five simulated minutes at 20 TPS, not a wall-clock performance benchmark.
- Bed Dog uses real sleep readiness and player state followed by the registered sleep-completion event. Its five-day cooldown expiry uses a world-clock advance, not five days of real-time waiting. Sniffer acceptance checks the server pointing state after real detection and owner attention, not rendered animation quality.
- Hunter observes the real death event before/after the handler and unregisters both observers in `finally`. No global listener or production testing hook is left installed.
- Failed fixtures and compile errors are retained in the local raw-log bundle. Failed runs are not promoted to gameplay passes. Test entities/storage entries are cleaned on success; owned failed runtimes are stopped before acceptance. Bed sleep state and event listeners have explicit cleanup.

## Build, review and remaining gates

Exact target: Minecraft 26.1.2, NeoForge 26.1.2.101, Java 25, Gradle 9.1.0. Worktree: `doggy-talents-next-gameplay-completion`, based on merged PR #42. `clean build runGameTestServer` passed all 76 GameTests in `gradle-6cb17312.log`. Strict Modmaker validation then rebuilt the final source successfully; zero outer errors/warnings, with only the two intentional shared-namespace warnings in the nested JAR audit. It does not classify runtime or public-release readiness.

The concrete diff review checked the three production fixes, fixture isolation, listener cleanup, owner boundaries, deterministic branches and artifact provenance. No blocking review findings remain. Client-only Bed Finder outlines, subjective visuals/audio and final clean-client/release acceptance remain in REVIEW-VISUAL-01 and REVIEW-RELEASE-01. Multiplayer remains waived and pack compatibility deferred. Overall public-release classification remains **PROTOTYPE_ONLY** until those separate required gates close; headless gameplay completion is not a public release or pack-admission decision.

The unreleased candidate retains version 26.1.2.24. Artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`, 11,520,951 bytes, SHA-256 `9faaa448959a2ae9d1888095d4a5dd399a5e12926255a0ca53e0e1d3d7600484`. SHA-512 is in the validation report. Source ZIP, candidate JAR, reports, JUnit XML and all raw loader logs are retained under `bytecraftpack/mod-development/doggy-talents-next-neoforge-26.1.2/evidence/gameplay-completion-2026-09-04/`.

Final-source loader run `gradle-f51cefaf.log`: all 76 tests passed at 22:43 UTC, exit 0, 20 seconds. The final clean build passed 57 JUnit tests in 25 suites, zero failures/errors. Strict validator exit 0 is captured in `validation.log`/`validation-report.json`. All runtime launches use the task-owned Minecraft MCP headless lease; no client or multiplayer session was launched.

Repeat `gradle-48d0598f.log`: all 76 tests passed at 22:46 UTC, exit 0, 21 seconds. No error/exception entries in either final-source loader log; intentional malformed-fixture warnings and existing Java/Gradle deprecation notices remain. `git diff --check` passes.
