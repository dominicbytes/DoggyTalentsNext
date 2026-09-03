# Dog Core Persistence QA — NeoForge 26.1.2

Date: 2026-09-03  
Contract: `SAVE-01` core, nested-state, and block-entity subgates
Minecraft: 26.1.2  
NeoForge: 26.1.2.101  
Java: 25

## Result

`PASS` — the NeoForge GameTest server ran four DTN tests in the `doggytalents:default` batch and reported `All 5 required tests passed`, including Minecraft's built-in control test.

Test IDs:

- `doggytalents:save_01_dog_core_round_trip`
- `doggytalents:save_01_dog_extended_round_trip`
- `doggytalents:save_01_block_entity_round_trip`
- `doggytalents:save_01_rice_mill_progress_round_trip`

Command:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

The test creates a real registered `Dog`, writes it through `Entity.saveWithoutId(ValueOutput)`, creates a second registered `Dog`, and reads the resulting tag through `Entity.load(ValueInput)`.

The following state survived the round trip:

- dog UUID;
- owner UUID, tame state, and custom name;
- gender and mode;
- hunger and incapacitation value;
- normal and kami levels;
- will-obey, friendly-fire, team-player, force-sit, auto-mount, cross-origin teleport, patrol-lock, hide-armor, and on-duty flags;
- overworld dog-bed and food-bowl positions;
- plain and stateful talent levels;
- Pack Puppy item slots plus render, pickup, food-offer, and loot-collection options;
- Doggy Tools item slots, item damage, and pick-first option;
- a dyeable collar's type and RGB color;
- the Feathered Mantle artifact.

The block-entity tests also reconstruct production block entities through `BlockEntity.saveWithFullMetadata(...)` and `BlockEntity.loadStatic(...)`. They preserve food-bowl inventory and placer UUID; dog-bed casing, bedding, owner UUID, bed name, and cached owner name; rice-mill input, bowl, and output inventory; and the oracle's canonical nonzero `grindingTime` state.

The registry now creates explicit 26.1 `GameTestInstance`s through NeoForge's `RegisterGameTestsEvent`. Registering only entries in Minecraft's test-function registry does not make those functions runnable tests in the 1.21.5+ GameTest architecture.

## Evidence boundary

This result validates the 26.1 `ValueInput`/`ValueOutput` bridge for identity, core dog state, representative talents, accessories, artifacts, dog-owned inventories, and the established state of three DTN block entities in a loader-aware server. It does not yet prove a process restart, broad frozen 1.21.1 fixture upgrade, or tracker data. Those remain separate `SAVE-01` subgates.

Raw log: `run/logs/latest.log`

## Aggregate and artifact gates

- Fresh `gradlew.bat clean build`: PASS, 30 tests, zero failures/errors.
- Strict Modmaker validation: PASS; project audit had zero errors and two shared-namespace warnings, while the artifact audit had zero findings.
- Production JAR SHA-256: `adb23d3b45bf17e421bf7c1ff27f830607dc2d585fd83fada81f5327c0794c31`.
- Clean official NeoForge 26.1.2.101 server: PASS; the exact JAR loaded from `mods/` and reached `Done (0.327s)` before a clean `stop`.

Production-server raw log: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/latest.log`
