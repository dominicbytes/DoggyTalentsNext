# Doggy Talents Next 26.1.2 production-server QA

Date: 2026-09-03  
Gate: `SERVER-01`  
Result: **PASS**

## Artifact

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: `11,368,316` bytes
- SHA-256: `adb23d3b45bf17e421bf7c1ff27f830607dc2d585fd83fada81f5327c0794c31`
- SHA-512: `0148cd1616e37246fbb5e29afd83d9f122aed09f2bd49bdf39bc97ce02ec08893a4fcec44f915029635ce18aafe188028a0e285aaab2a71d3d7c186cb2e87b41`
- Build: Java 25, `gradlew.bat clean build`
- Automated tests: 30 passed
- Strict Modmaker validation: PASS; project audit had zero errors and two shared-namespace warnings, while the artifact audit had zero findings

## Clean-server setup

- Minecraft: `26.1.2`
- Loader: NeoForge `26.1.2.101`
- Java: Eclipse Adoptium `25.0.3+9-LTS`
- Server source: official NeoForge installer
- Installer SHA-256: `921629c33bdd94350b499a620a0f23484c345e05e1e31ada309a35a0803d1264`
- Install directory: new disposable directory containing no project classes or development runtime
- Mods directory: contained the production Doggy Talents Next JAR
- Runtime: local headless server on the configured port

The server log identifies the mod source as:

```text
doggytalents (jar(mods/DoggyTalentsNext-26.1.2-26.1.2.24.jar))
```

The loaded versions were:

```text
Doggy Talents Next 26.1.2.24 (doggytalents)
Minecraft 26.1.2 (minecraft)
NeoForge 26.1.2.101 (neoforge)
```

The server completed world initialization:

```text
Done (0.327s)! For help, type "help"
```

No Doggy Talents Next client-class resolution, mixin, registration, or mod-loading failure occurred.

## Non-mod diagnostics

The Windows run logged one OSHI warning for an invalid local PerfOS registry value. This host diagnostic is outside the DTN artifact and did not prevent startup, world loading, saving, or permission-handler initialization.

## Evidence boundary

This proves clean production-JAR installation and dedicated-server classloading. It does not prove client rendering, client startup, multiplayer behavior, or save compatibility across a restart. Multiplayer is explicitly waived for the dominicbytes fork.
