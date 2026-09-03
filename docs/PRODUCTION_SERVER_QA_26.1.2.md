# Doggy Talents Next 26.1.2 production-server QA

Date: 2026-09-03  
Gate: `SERVER-01`  
Result: **PASS**

## Artifact

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: `11,365,400` bytes
- SHA-256: `f4c54fe3f68e282e8800f9e70d5c117a6fd6a417ad0751f87997126dab51b534`
- SHA-512: `5ad1fa84ed65ed61fde7ee6ce4d9b178a7dd0d342652a8e741d7135f669df313ee76ee04ae35538f443cdec47465edac46619f884efc62570f21c3cabaaca254`
- Build: Java 25, `gradlew.bat clean build`
- Automated tests: 30 passed
- Strict Modmaker artifact validation: PASS, zero findings

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

The server completed new-world initialization:

```text
Done (0.389s)! For help, type "help"
```

No Doggy Talents Next client-class resolution, mixin, registration, or mod-loading failure occurred.

## Non-mod diagnostics

The Windows run logged one OSHI warning for an invalid local PerfOS registry value. This host diagnostic is outside the DTN artifact and did not prevent startup, world loading, saving, or permission-handler initialization.

## Evidence boundary

This proves clean production-JAR installation and dedicated-server classloading. It does not prove client rendering, client startup, multiplayer behavior, or save compatibility across a restart. Multiplayer is explicitly waived for the dominicbytes fork.
