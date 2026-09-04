# MODEL-FORMAT-02 QA — NeoForge 26.1.2

## Contract

Dog model JSON parts may declare `props.translucent`. A marked part and its descendants are extracted into a separately rendered translucent model, while headless ancestors retain the original pose hierarchy and insertion order.

## Port scope

- Ports DashieDev's translucent-part model format and renderer support.
- Ports the Amaterasu and Divine Shiranui resource annotations that use it.
- Adapts the layer to Minecraft 26.1's `DogRenderState`/`submit` renderer API.
- Preserves translucent branch order with insertion-ordered internal maps.

## Verification

- Clean Gradle build: passed.
- JUnit: 39 tests, 0 failures, 0 errors.
- `MODEL-FORMAT-02` verifies codec parsing, headless-parent extraction, descendant propagation, deterministic sibling order, and both bundled Amaterasu resources.
- GameTest: 11/11 required tests passed (10 DTN and one vanilla control).
- Strict Modmaker validation: 0 errors, 0 final warnings.
- Production dedicated server: exact JAR reached `Done (0.284s)` and stopped cleanly.
- JAR SHA-256: `1836e05294a7788528c8e5e4c7d9e5b42c33f59fb76b61c9a462da95ba72b98f`.

The Windows Netty epoll/kqueue debug-appender messages are native transport probes; the server selected its supported transport and completed startup.

## Manual client gate

Automatic client launch is excluded. A real-client resource reload and visual inspection of Amaterasu/Divine Shiranui swirl overlays remains a manual acceptance gate, so this slice does not claim public-release readiness.
