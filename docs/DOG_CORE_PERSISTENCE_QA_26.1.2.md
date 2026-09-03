# Dog Core Persistence QA — NeoForge 26.1.2

Date: 2026-09-03  
Contract: `SAVE-01` core-state subgate  
Minecraft: 26.1.2  
NeoForge: 26.1.2.101  
Java: 25

## Result

`PASS` — the NeoForge GameTest server ran one required test and reported `All 1 required tests passed`.

Test ID: `doggytalents:save_01_dog_core_round_trip`

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
- overworld dog-bed and food-bowl positions.

## Evidence boundary

This result validates the 26.1 `ValueInput`/`ValueOutput` bridge for identity and core dog state in a loader-aware server. It does not yet prove a process restart, frozen 1.21.1 fixture upgrade, talents, accessories, item/inventory contents, tracker data, or block-entity persistence. Those remain separate `SAVE-01` subgates.

Raw log: `run/logs/latest.log`

## Aggregate and artifact gates

- Fresh `gradlew.bat clean build`: PASS, 30 tests, zero failures/errors.
- Strict Modmaker project/artifact validation: PASS, zero findings.
- Production JAR SHA-256: `f01182592f29fdabc5e5d084f4af46d1ef410eec2df15d9f55f12e7645612004`.
- Clean official NeoForge 26.1.2.101 server: PASS; the exact JAR loaded from `mods/` and reached `Done (1.752s)` before a clean `stop`.

Production-server raw log: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/latest.log`
