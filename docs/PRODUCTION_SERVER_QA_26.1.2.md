# Doggy Talents Next 26.1.2 production-server QA

Date: 2026-09-03  
Gate: `SERVER-01`  
Result: **PASS**

## Artifact

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: `11,362,181` bytes
- SHA-256: `f01182592f29fdabc5e5d084f4af46d1ef410eec2df15d9f55f12e7645612004`
- SHA-512: `bda81763ad888e94b196eef9d077272ac35a03b1c8e4af1e45aca57abf00f680fc9b28cd75d8810ca2d7a0ae1a97dab387acf005c9ed223857d3704316298930`
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
- Runtime: isolated local-headless Minecraft MCP lease on a broker-assigned port

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
Done (1.752s)! For help, type "help"
```

No Doggy Talents Next client-class resolution, mixin, registration, or mod-loading failure occurred.

## Non-mod diagnostics

The Windows run logged NeoForge/Minecraft diagnostics while still reaching `Done`:

- Netty attempted to describe unavailable Linux `epoll` and macOS/BSD `kqueue` native transports while creating extended debug stack traces.
- Mojang public-key retrieval and the NeoForge update check timed out in the isolated environment.
- A missing first-run `server.properties` message appeared before Minecraft generated the file.

These diagnostics are outside the DTN artifact and did not prevent startup, world creation, saving, or permission-handler initialization.

## Evidence boundary

This proves clean production-JAR installation and dedicated-server classloading. It does not prove client rendering, client startup, multiplayer behavior, or save compatibility across a restart. Multiplayer is explicitly waived for the dominicbytes fork.
