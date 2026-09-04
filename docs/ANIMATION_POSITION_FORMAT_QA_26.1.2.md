# Animation position format QA — NeoForge 26.1.2

Date: 2026-09-03 (America/Chicago)
Target: Minecraft 26.1.2, NeoForge 26.1.2.101, Java 25

## Scope

`ANIMATION-FORMAT-01` ports DashieDev commits `44fd25e60`, `f043eac8c`, and
`49f64e570`. Position channels now convert Blockbench coordinates symmetrically
on decode and encode. The 71 affected bundled animation resources use the new
X-axis convention; the final `sniff_neutral` form also retains its newer leg
correction.

The 26.1 adaptation uses `Keyframe.preTarget()` in place of the older
`Keyframe.target()` API.

## Automated evidence

- The focused codec test failed before the implementation because source X
  `+2` decoded as `+2` instead of `-2`.
- `DTNAnimationCodecTest` verifies decode coordinates, symmetric encode, and
  successful decode/encode of all 95 bundled animation JSON files.
- `gradlew.bat clean build --no-daemon`: PASS, 41 tests, 0 failures.
- `gradlew.bat runGameTestServer --no-daemon`: PASS, all 12 required tests
  (11 DTN plus Minecraft's control test).
- Strict Modmaker project validation: PASS, 0 errors and two intentional
  shared-namespace warnings (`data/minecraft`, `data/neoforge`).
- Strict production-artifact audit: PASS, 0 errors and 0 warnings.
- Exact production JAR dedicated-server smoke: PASS; NeoForge 26.1.2.101
  loaded DTN and reached `Done (0.275s)` before a clean `stop`.

## Artifact

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `37de039e72f6ae5f327687d261d997605c98efdf646f31b84902e01e856e39e7`

## Contract reconciliation

| Test ID | Harness | Result |
|---|---|---|
| `ANIMATION-FORMAT-01` | JUnit codec/resource test | PASS |
| `ANIMATION-FORMAT-01-VISUAL` | Muted isolated real client, representative translated animations | NOT RUN — manual gate |

Required: 2; implemented: 2; executed: 1; passed: 1; failed: 0;
manual/not run: 1; waived: 0.

Verdict: the code and packaged resource migration are `ARTIFACT_TESTED` for
server loading and deterministic codec behavior. The slice remains
`PROTOTYPE_ONLY`, and the project is not `PUBLIC_RELEASE_READY`, until a fresh
artifact-bound real-client visual check confirms representative left/right and
whole-body translated animations.
