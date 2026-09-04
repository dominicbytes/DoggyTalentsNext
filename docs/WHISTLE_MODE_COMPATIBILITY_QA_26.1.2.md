# Whistle mode compatibility QA — NeoForge 26.1.2

## Scope and assumptions

- Contract ID: `GAMEPLAY-WHISTLE-01`.
- Frozen behavior reference: DashieDev `1.21-master` commit
  `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2`.
- User authorization overrides the research-only preflight boundary for implementation.
- Existing whistle mode indexes and the legacy `mode` and `dog_on_duty_only` item keys remain stable.
- Missing, negative, or oversized mode indexes resolve to `STAND`, matching the port's existing
  fallback for positive out-of-range values and preventing negative array indexes.
- Multiplayer behavior is outside this slice by user request.

## Change

`WhistleItem` now resolves stored mode indexes through one range-checked helper. Dog interaction,
normal use, duty-whistle detection, and stack naming all use that same result. Every valid legacy
mode remains unchanged; corrupt values safely use `STAND`.

## Test-first evidence

Before implementation, the focused contract failed compilation because `resolveMode(int)` did not
exist. The prior inline implementations also showed the causal defect: interaction and normal-use
paths checked only `mode >= VALUES.length`, then indexed `VALUES[mode]`, allowing a negative byte to
throw `ArrayIndexOutOfBoundsException`.

After implementation:

- `gradlew.bat test --tests doggytalents.common.item.WhistleModeCompatibilityTest --no-daemon`
  passes both focused tests.
- `gradlew.bat clean build --no-daemon` passes all 35 JUnit tests in 15 suites.
- `gradlew.bat runGameTestServer --no-daemon` passes all 9 required tests: eight DTN tests plus the
  vanilla control. The new loader-aware test uses the registered whistle, verifies an absent mode,
  preserves `DUTY_WHISTLE` plus `dog_on_duty_only`, and defaults negative and oversized stored bytes.
- The strict Modmaker validator passes with zero errors and zero final warnings. Its preliminary
  structural scan notes the repository's intentional shared `data/minecraft` and `data/neoforge`
  namespaces; the final strict result accepts them.
- The exact production JAR starts under NeoForge `26.1.2.101`, reaches `Done (0.265s)`, and stops
  cleanly. Windows Netty epoll/kqueue debug-appender probes remain known environment noise.

## Artifact

- File: `build/libs/DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `47e77a58a94572d42f4888fc11af1f3b43723ee69d2fda0da7dc4fe11a0e30fb`
- Validation report: `build/reports/gameplay-whistle-01-validation.json`

## Verdict

`GAMEPLAY-WHISTLE-01` is `ARTIFACT_TESTED` for NeoForge 26.1.2. Client visual/audio acceptance and
the user-waived multiplayer matrix remain outside this result, so public release readiness is still
`NOT_EVALUATED`.
