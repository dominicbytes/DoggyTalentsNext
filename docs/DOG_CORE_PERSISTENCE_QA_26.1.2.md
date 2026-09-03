# Dog Core Persistence QA — NeoForge 26.1.2

Date: 2026-09-03  
Contract: `SAVE-01` core and nested-state subgates
Minecraft: 26.1.2  
NeoForge: 26.1.2.101  
Java: 25

## Result

`PASS` — the NeoForge GameTest server ran both DTN tests in the `doggytalents:default` batch and reported `All 3 required tests passed`, including Minecraft's built-in control test.

Test IDs:

- `doggytalents:save_01_dog_core_round_trip`
- `doggytalents:save_01_dog_extended_round_trip`

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

The registry now creates explicit 26.1 `GameTestInstance`s through NeoForge's `RegisterGameTestsEvent`. Registering only entries in Minecraft's test-function registry does not make those functions runnable tests in the 1.21.5+ GameTest architecture.

## Evidence boundary

This result validates the 26.1 `ValueInput`/`ValueOutput` bridge for identity, core dog state, representative talents, accessories, artifacts, and dog-owned inventories in a loader-aware server. It does not yet prove a process restart, frozen 1.21.1 fixture upgrade, tracker data, or block-entity persistence. Those remain separate `SAVE-01` subgates.

Raw log: `run/logs/latest.log`

## Aggregate and artifact gates

- Fresh `gradlew.bat clean build`: PASS, 30 tests, zero failures/errors.
- Strict Modmaker project/artifact validation: PASS, zero findings.
- Production JAR SHA-256: `f4c54fe3f68e282e8800f9e70d5c117a6fd6a417ad0751f87997126dab51b534`.
- Clean official NeoForge 26.1.2.101 server: PASS; the exact JAR loaded from `mods/` and reached `Done (0.389s)` before a clean `stop`.

Production-server raw log: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/latest.log`
