# Block-entity persistence QA — NeoForge 26.1.2

Date: 2026-09-03  
Contract: `SAVE-01` block-entity subgate  
Result: **PASS**

## Runtime contract

The registered `doggytalents:save_01_block_entity_round_trip` GameTest places real DTN blocks, modifies their block entities, serializes each through `BlockEntity.saveWithFullMetadata(...)`, and reconstructs it through `BlockEntity.loadStatic(...)` using the live 26.1 registry context.

The round trip preserves:

- food-bowl inventory slots and placer UUID;
- dog-bed casing and bedding IDs, owner UUID, bed name, and cached owner name;
- rice-mill input, bowl, and output inventory slots.

The NeoForge GameTest server ran three DTN tests plus Minecraft's control test and reported `All 4 required tests passed` in 471.4 ms.

## Aggregate gates

- Fresh Java 25 `clean build`: PASS, 30 tests.
- Strict Modmaker 26.1.2 NeoForge validation: PASS, zero findings.
- Production JAR SHA-256: `7989657c3ff63a554f2fde983ac537417b8fd29d6766c0d911cd26c7eaf95a0a`.
- Official NeoForge 26.1.2.101 production server: PASS; the exact JAR reached `Done (0.329s)` and stopped cleanly.

## Evidence boundary

This closes the established food-bowl, dog-bed, and rice-mill inventory portion of `SAVE-01`. It does not assert nonzero rice-mill processing progress, tracker data, frozen 1.21.1 fixtures, or persistence across a server process restart.

Raw GameTest log: `run/logs/latest.log`  
Production-server raw log: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/latest.log`
