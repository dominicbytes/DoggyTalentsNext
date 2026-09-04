# Command workflows and original-world upgrade

User priority: complete REVIEW-COMMAND-01 and REVIEW-UPGRADE-01 before the broader gameplay sweep. Base: merged PR #40, `c43d72bbfde419bd005d638d6d7ee23d4ec1131a`. Existing fork behavior, registry IDs, branding, and copyright notices are retained. Multiplayer and pack acceptance remain out of scope.

## Executable contract

- `REVIEW-COMMAND-01-LOCATE`: execute both locate selectors through Brigadier with a server player; assert reported coordinates/dimension, malformed and missing UUIDs, missing owner/name, ambiguous names, non-player and insufficient-permission rejection.
- `REVIEW-COMMAND-01-REVIVE`: execute both revive selectors; assert live dog identity/owner/name, spawn position, consumed respawn record, repeated/missing/ambiguous/invalid requests and denied permission without mutation.
- `REVIEW-COMMAND-01-TRACKER`: exercise server-side tracker selection, missing target and incorrect held item, recorded target coordinates/name, and clear/reset. Verify owner/dimension/display eligibility and position updates where applicable. Screens/rendering remain the separate visual gate.
- `REVIEW-COMMAND-01-WHISTLE`: use actual item and server handler paths; assert stand/stay/stop-attack, owned/range/duty eligibility, invalid mode, absent whistle and cooldown rejection. Talent-specific whistle effects remain the broader gameplay gate; screen-based selection remains visual acceptance.
- `REVIEW-UPGRADE-01`: generate and save a disposable world in the unmodified original 1.21.1 runtime, preserve a closed-world backup and artifact provenance, upgrade only a copy with the production 26.1.2 JAR, save/stop/restart, and compare dog, inventory, block-entity and mod saved-data state. A reconstructed NBT payload does not satisfy this gate.

## Status

Both requested gates pass within the contracts above. The broader gameplay, visual and final-release gates remain open. No client was launched, multiplayer was not tested, and no public binary release was published.

## Findings and corrections

1. Tracker selection trusted an arbitrary dog UUID although the selection list only offers eligible owned dogs in the same dimension. The server now enforces the same ownership, dimension and display eligibility before writing item data. The regression failed before the fix. Clearing selection before the cross-dimension request prevents an unchanged-selection false positive.
2. Original 1.21.1 saves use root-level `data/doggytalentsDogLocations.dat` and `data/doggytalentsDeadDogs.dat`. The draft created new namespaced indexes instead of importing those files. The first authentic upgrade kept loaded dogs but omitted the unloaded dog's location and did not populate the revival index. Both original files now import before entity ticks; existing current indexes take precedence, and originals are retained. An unreadable original file fails instead of silently creating an empty replacement.
3. Vanilla's attribute rename did not visit DTN's custom entity ID. Old `minecraft:generic.*` attribute names produced decoding warnings. The compatibility reader maps only renamed IDs that exist in the target attribute registry, retaining bases/modifiers and leaving source NBT untouched. A regression uses movement base `0.47` plus permanent modifier `0.13`, not default values that could hide a failed load.

No talent balance, registry ID, fork exception, branding or copyright change is included.

## Executable evidence

| Contract | Execution and assertions | Result |
|---|---|---|
| REVIEW-COMMAND-01-LOCATE | `CommandWorkflowGameTests.locate`: actual dispatcher execution, both selectors, coordinates/dimension/name, ambiguity/disambiguation, missing/malformed requests, console and insufficient permission | PASS |
| REVIEW-COMMAND-01-REVIVE | `CommandWorkflowGameTests.revive`: both selectors spawn live owned dogs at the requested position and consume only their records; denial, missing/repeated and ambiguous requests do not revive | PASS |
| REVIEW-COMMAND-01-TRACKER | `CommandWorkflowGameTests.tracker`: authoritative selected-item UUID/name/coordinates, stale-position response emitted, unchanged/foreign/missing position request produces no response; selection eligibility, wrong item and reset | PASS |
| REVIEW-COMMAND-01-WHISTLE | `CommandWorkflowGameTests.whistle`: actual item use/hotkey handler, stand/stay/stop-attacking, owner/range/duty filtering, cooldown, invalid mode and missing item | PASS |
| REVIEW-UPGRADE-01 | Original production runtime world, closed backup, copied production upgrade, second process restart, 276 semantic state assertions; `LegacyDogSavedDataGameTests` additionally checks original binary indexes, revival, canonical precedence, no re-import of consumed records, bad-file rejection and custom attributes | PASS |

These five required contract IDs are implemented, executed and passed; zero failed/blocked/waived within this slice. Four command tests and one upgrade-index test are registered in the normal loader suite. Final aggregate: **27 required GameTests passed** (26 DTN and one vanilla control); clean build: **57 JUnit tests passed**, zero failures/errors. The strict Modmaker validator passes with zero outer errors/warnings; its nested JAR audit has the two existing intentional `data/minecraft` and `data/neoforge` shared-namespace warnings.

Server-player fixtures use NeoForge `FakePlayer`, the real Brigadier dispatcher and real server payload context. Outbound tracker responses are captured at the server connection; this establishes response emission/suppression, not socket delivery or rendered correction. Actual client screens, hotkeys, overlays and talent-specific whistle outcomes stay in the visual/gameplay gates. This is not multiplayer acceptance.

## Authentic source and world

- Unmodified original source: DashieDev fork-point commit `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2`, Minecraft 1.21.1, NeoForge 21.1.84, Java 21.
- Built original JAR: `DoggyTalentsNext-1.21.1-1.19.0.jar`; SHA-256 `86de69a5c2198942d1f11a3d0262f51de925cc7a62389e1cd97377838f4e92f4`.
- `scripts/upgrade-original-create.txt` and `upgrade-original-storage.txt` create state through the running original production server, then save/stop. This is a purpose-built real source-world fixture, not an existing player's long-lived world and not a reconstructed payload.
- Fixture: live female level 37/kami 9 dog, Pack Puppy inventory, Happy Eater, collar/artifact, groups/stats, bed/bowl links; one unloaded dog; one killed dog's actual revival record; bed ownership/materials/name, food bowl and rice mill inventories.
- Original closed-world ZIP SHA-256: `8f6d3bb3ba278a1c72866d0d772701fffb7bb1f1f47040e08ee507fa5f9f06f2`. It is retained unchanged; only extracted copies are upgraded.
- Frozen `.dat` resources are byte-for-byte source files with lowercase packaged filenames. The GameTest checks SHA-256, restores original disk filenames, imports to a separate temporary storage directory and cleans that directory afterward.

## Production reproduction

Target: Minecraft 26.1.2, NeoForge 26.1.2.101, Java 25. Each profile contains only its corresponding production DTN JAR. An isolated Minecraft MCP local-headless lease owns the launch; the loopback port is supplied by that lease. The Gradle init task starts an installed server, not the development classpath, and waits for `Done` before issuing commands. Every script ends in an orderly save/stop.

1. Install the exact original server and build the frozen original JAR; use `qaWorldServer` with `scripts/world-upgrade.init.gradle` and the two original command scripts.
2. Back up the stopped source world. Extract to a separate target profile with the exact target server and candidate JAR.
3. Run `qaWorldServer` with `scripts/upgrade-target-check.txt`; inspect the stopped world with `scripts/inspect-upgrade-world.cjs`.
4. Restart the same target world in another process, run the same checks, stop and inspect again.
5. Run `node scripts/verify-world-upgrade.cjs original-world.json target-upgraded.json target-restarted.json`.

The inspector needs `prismarine-nbt` (or `DTN_NBT_MODULE` pointing to an installed copy). The comparison checks live and unloaded dog identity/owner/state, normalized names and attribute IDs, all fixture talents/inventories/accessories/artifacts/groups/stats/locations, block-entity contents/materials/ownership, complete revival payload and original-index retention. Volatile tick timestamps and vanilla version-specific entity bookkeeping are not treated as parity invariants. The mill's actual persisted progress is zero because its deliberate non-recipe inventory cannot grind; interrupted recipe progress remains covered by the existing SAVE-01 GameTest.

Final candidate `DoggyTalentsNext-26.1.2-26.1.2.24.jar`: **11,450,007 bytes**, SHA-256 `325c174967ffbd21a6d67f08ed9d122d1e45c54de2a1fcc55b0b79d61508a4b6`. The candidate must be reverified after any binary change; this is not a released version.

Raw build/loader/original/upgrade/restart logs, three snapshots, comparison result, validator report, hashes, source archive, original-world backup and candidate JAR are retained locally under `bytecraftpack/mod-development/doggy-talents-next-neoforge-26.1.2/evidence/command-upgrade-2026-09-04/`.

## Review and limits

Concrete diff review used the Modmaker-required code-review skill. Corrected test cleanup after discard (which can itself create revival records), cross-dimension false-positive setup, preserved custom attribute modifiers, and enforced lowercase packaged resource paths. The initial harness sent commands before server readiness; that run is not acceptance evidence. All accepted runs use the readiness guard.

Known loader/environment diagnostics: version differences on upgrade; vanilla leaves old Nether/End directories containing NeoForge attachment files; Windows performance-counter and JDK/deprecation notices. The final upgrade log also records Log4j DebugFile appender errors while formatting Netty's unavailable OSX/Linux native transports on Windows; the server continues, binds loopback, completes all checks and exits 0. These are not DTN entity failures and were not hidden or patched. No DTN attribute-decoding warning remains in the corrected production runs. The existing malformed legacy fixture intentionally warns about `missing:entity` in GameTests.

Always upgrade a backup first. The importer does not merge old records into a current index: doing so could resurrect deliberately consumed/deleted records. A world already saved by the unfixed draft may need targeted recovery from its original backup; this test establishes a fresh original-to-fixed-port upgrade, not automatic repair of all previously damaged saves. Arbitrary modpacks, other historic versions and client-visible fidelity are not established here. Overall project acceptance remains **PROTOTYPE_ONLY** pending the gameplay, visual and final-release gates in `PORT_REVIEW_26.1.2.md`.
