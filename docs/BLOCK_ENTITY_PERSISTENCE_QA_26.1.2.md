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

The registered `doggytalents:save_01_rice_mill_progress_round_trip` test additionally loads the oracle's canonical nonzero `grindingTime` fixture through production block-entity deserialization, then serializes and reconstructs that mill again. The unfinished progress value remains `23` through both paths without exposing or mutating private production state.

The NeoForge GameTest server ran four DTN tests plus Minecraft's control test and reported `All 5 required tests passed` in 632.2 ms.

## Aggregate gates

- Fresh Java 25 `clean build`: PASS, 30 tests.
- Strict Modmaker 26.1.2 NeoForge validation: PASS; project audit had zero errors and two shared-namespace warnings, while the artifact audit had zero findings.
- Production JAR SHA-256: `adb23d3b45bf17e421bf7c1ff27f830607dc2d585fd83fada81f5327c0794c31`.
- Official NeoForge 26.1.2.101 production server: PASS; the exact JAR reached `Done (0.327s)` and stopped cleanly.

## Evidence boundary

This closes the established food-bowl, dog-bed, rice-mill inventory, and nonzero rice-mill progress portions of `SAVE-01`. It does not assert tracker data, broad frozen 1.21.1 fixtures, or persistence across a server process restart.

Raw GameTest log: `run/logs/latest.log`  
Production-server raw log: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/latest.log`
