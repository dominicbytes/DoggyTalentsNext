# Dog-bed tag refresh QA — NeoForge 26.1.2

Date: 2026-09-03
Target: Minecraft 26.1.2, NeoForge 26.1.2.101, Java 25

## Contract

`ITEM-01-TAG-RELOAD` preserves NeoForge's static-data ownership rule for dog-bed material tags:

- dedicated-server and remote-client tag updates refresh their respective material view;
- an integrated client's follow-up tag packet does not clear or replace the material maps already refreshed by the integrated server;
- client texture validation and the optional dog-bed model-cache clear run only for a real client-owned refresh.

This replaces the deprecated `TagsUpdatedEvent.UpdateCause` branch with `shouldUpdateStaticData()` and the event subtype supplied by NeoForge 26.1.2.

## Regression evidence

The focused test `DogBedMaterialManagerTest.item01IntegratedClientTagUpdateKeepsSharedServerMaterials` failed before the production change because the integrated-client notification entered `refresh`, cleared both maps, and attempted to read client config. It passes after the listener returns when `shouldUpdateStaticData()` is false.

Commands:

```text
gradlew.bat test --tests doggytalents.common.block.DogBedMaterialManagerTest --no-daemon
gradlew.bat compileJava --warning-mode all --rerun-tasks --no-daemon
gradlew.bat clean build --no-daemon
gradlew.bat runGameTestServer --no-daemon
python <modmaker>/scripts/validate_mod_project.py . --target-version 26.1.2 --loader neoforge --strict-release --require-stable-loader
```

Result:

- focused test: 1 passed;
- aggregate unit suite: 33 passed;
- loader-aware GameTests: all 7 required tests passed (six DTN tests plus the Minecraft control);
- the seven `TagsUpdatedEvent.UpdateCause` removal warnings are gone;
- remaining compile warnings: 65, all on the separate legacy NeoForge item-handler surface;
- strict Modmaker validation: passed with zero final findings;
- the exact production JAR loaded from the clean NeoForge server's `mods/` directory, reached `Done (0.329s)`, and stopped cleanly;
- fresh production JAR SHA-256: `06c508da07ec2363a1d1e30e4a62c7972f5aa7d382283227c9160bd8c95a3404`.

The Windows Netty epoll/kqueue debug-appender probes remain the known environment diagnostic from the earlier server evidence; they did not prevent startup. No client launch or visual evidence is required for this ownership/lifecycle correction.
