# Gameplay food and training QA — NeoForge 26.1.2

## Scope

This slice is authorized by the user as continued implementation after the supplied preflight report. It does not rerun preflight. Multiplayer and automatic client launch remain excluded.

The frozen behavior oracle is DashieDev commit `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2`. Stable identifiers and established hunger/training behavior remain unchanged.

## Contracts

- `GAMEPLAY-FOOD-01`: cooked meat adds five times vanilla nutrition, consumes one item, and is rejected without consumption at full hunger; a golden apple adds hunger and applies its 26.1 consumable effects.
- `GAMEPLAY-TRAINING-01`: an owned vanilla wolf converts to one DTN dog, consuming one training treat while retaining UUID, owner, and name.

Both contracts are dedicated-server GameTests registered through `DTNGameTestRegistry`.

## Defect and correction

The first GameTest run showed that golden apples added hunger but did not apply regeneration or absorption. `BoostingFoodHandler` read `DataComponents.CONSUMABLE` after shrinking a one-item stack to empty. The correction captures the component before consumption, then applies its effects in the established post-consumption order.

No production correction was required for wolf conversion; the initial fixture placed the wolf outside the empty GameTest structure. Moving the fixture inside the structure made the original conversion path observable and it passed unchanged.

## Required gates

- Clean Gradle build and all unit tests.
- All required DTN and vanilla-control GameTests.
- Strict NeoForge 26.1.2 project validation.
- Exact production JAR dedicated-server startup and clean stop.
- Diff review with no unresolved blocking findings.

## Result

- Clean build: passed; 35 unit tests passed.
- GameTest server: all 11 required tests passed (10 DTN plus the vanilla control), including both new contracts.
- Strict validator: final artifact audit reported 0 errors and 0 warnings.
- Production JAR: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`, SHA-256 `125068a958f87c4a6be0b28e1559f726cee500656964a68dce563992f489e306`.
- Exact-JAR dedicated server: reached `Done (0.285s)` and stopped cleanly.
- Diff review: no blocking correctness, security, side-loading, or maintainability findings.

The Windows Netty epoll/kqueue debug-appender messages are known native transport probes; they did not prevent startup or shutdown.
