# Dog model format and reload QA — NeoForge 26.1.2

## Scope

This slice ports the dependency-safe prerequisites from DashieDev commits `7dbe0edd8dad2c8be2d39074f456cce4eb623b12` and `fd71e99649c253ec221eeb1ac6cb2bf46ea71048`. It does not include the later animation engine, model-content, or WangWang changes. The user-authorized implementation route is Modmaker; the supplied preflight report is context only and is not rerun.

## Contract

`MODEL-FORMAT-01` requires:

- a DTN dog-model JSON without a `props` object decodes with `DogModelProps.DEFAULT`;
- resource reload removes parsed model holders so changed resources can be decoded again;
- legacy code-backed model holders survive that invalidation.

The first focused test failed because `DogModelRegistry.invalidateAllParsed()` did not exist. The implemented changes make `props` optional, add parsed-holder invalidation, and invoke it at the start of model-resource application.

## Required gates

- Focused `MODEL-FORMAT-01` unit tests.
- Clean Gradle build and all unit tests.
- Dedicated-server GameTests for side safety.
- Strict NeoForge 26.1.2 project validation.
- Exact production JAR dedicated-server startup and clean stop.
- Diff review with no unresolved blocking findings.

Visual/resource-reload acceptance in a real client remains a named manual gate because the user prohibited automatic client launch.

## Result

- Focused contract: both `MODEL-FORMAT-01` unit scenarios passed.
- Clean build: passed; 37 unit tests passed.
- GameTest server: all 9 required tests passed (8 DTN plus the vanilla control).
- Strict validator: final artifact audit reported 0 errors and 0 warnings.
- Production JAR: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`, SHA-256 `c18058f99da75ce9232fd83c0db1ca5de91944a3080a498e25f3f92c485c145c`.
- Exact-JAR dedicated server: reached `Done (0.297s)` and stopped cleanly.
- Diff review: no blocking correctness, security, side-loading, or maintainability findings.

The Windows Netty epoll/kqueue debug-appender messages are known native transport probes; they did not prevent startup or shutdown.
