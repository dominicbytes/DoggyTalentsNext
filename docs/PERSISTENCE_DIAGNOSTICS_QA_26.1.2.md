# Persistence diagnostics QA — NeoForge 26.1.2

Date: 2026-09-03
Gate: `SAVE-02`
Result: **PASS**

## Diagnostic contract

Eight persistence exception paths that previously continued without a diagnostic now emit a warning with the original exception and enough context to identify the affected dog, dimension, or legacy storage key:

- `SAVE-02-RESPAWN-DIAGNOSTIC` — configured respawn exclusions, configured additional tags, component-name fallback, respawn-index name loading/type validation, and malformed UUID parsing
- `SAVE-02-ARMOR-DIAGNOSTIC` — current dog-armor state loading and legacy armor-state upgrade
- `SAVE-02-LOCATION-MIGRATION-DIAGNOSTIC` — current and legacy dog-location storage loading

The existing compatibility behavior is unchanged: a partial record still falls back or is skipped instead of preventing the world from loading. A malformed respawn name now resolves to the established `noname` value rather than exposing an empty display name.

## Regression coverage

`DogStorageCompatibilityTest` now has seven cases. In addition to the original 1.21.1 owner UUID, interim string UUID, legacy location keys, canonical re-encoding, and missing-UUID rejection cases, it verifies that:

- a respawn name stored under the wrong NBT type falls back to `noname`; and
- a malformed owner UUID does not prevent the remaining respawn record from loading.

The malformed-name test failed before the production change and passed afterward.

## Validation

- Java 25 `gradlew.bat clean build`: **PASS**, 32 tests
- NeoForge `26.1.2.101` GameTest server: **PASS**, all seven required tests (six DTN plus Minecraft's control test)
- Strict Modmaker validator: **PASS**, zero project or artifact findings; the two reviewed shared-namespace notices remain informational
- Production JAR dedicated server: **PASS**, loaded from `mods/`, reached `Done (1.604s)`, and stopped cleanly

Validated artifact:

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: `11,375,469` bytes
- SHA-256: `258cfc7fba02979f41240f4f13a7adcbadf85ff9bfad9af2d58079140fc30906`
- SHA-512: `67c291266b4ae3792a4747df00f94fbc6446ef4a5e453c20562e8b0e1db5eddadc513d271e53abad8d278a34182c8c344b7e3fbfbd3e78e4dc0130c00d82609f`

## Fixture boundary

The repository contains the checksum-locked, oracle-derived 1.21.1 dog payload and the completed two-JVM production save/restart evidence. It does not contain an authentic captured 1.21.1 full-world save. No synthetic world was added or represented as captured evidence; a full-world upgrade remains an optional evidence improvement when a provenance-verifiable source world becomes available.
