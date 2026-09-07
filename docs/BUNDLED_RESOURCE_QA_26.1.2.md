# Packaged client startup — 2026-09-07

VISUAL-STARTUP-01: PASS. REVIEW-VISUAL-01 remains incomplete.

The previous production JAR (`9faaa448959a2ae9d1888095d4a5dd399a5e12926255a0ca53e0e1d3d7600484`)
crashed during client setup in DoggySpinModel. The bundled Amaterasu JSON existed,
but ForgeUtil resolved filesystem paths below the JAR file itself. NeoForge's
JarContents content roots are physical archive paths, not mounted ZIP roots.

Use JarContents.get and JarResource.bufferedReader so packaged and exploded
resources share the same lookup. Models, resource IDs, branding and copyright
are unchanged. These internal helpers now return JarResource rather than Path;
all repository callers were updated. No stream or archive ownership is leaked.

## Verification

- Added two VISUAL-STARTUP-01 tests using the real Amaterasu resource, a temporary
  JAR and an exploded directory. Both check exact bytes and missing-resource behavior.
- Before fix: packed lookup assertion failed. The initial sandbox run also hit
  a Windows temporary-directory cleanup permission error; the normal-user rerun
  completed without that error.
- `gradlew.bat build --no-daemon`: PASS, 59 tests / 26 suites, no failures/errors.
- `gradlew.bat runGameTestServer --no-daemon`: PASS, all 76 required tests.
- Repaired JAR SHA-256: `ac047c9b1a7f27ba16ca701dac7ab1f658ebe0afc5f53d23f336cef874450a2c`.
- Isolated client harness: Minecraft 26.1.2, NeoForge 26.1.2.101, Java 25,
  only the production mod JAR in its mods folder; no development mod source sets.
  Loader itself runs through the Gradle development harness, not a retail launcher.
- Client logs confirm 80 models and all built-in animations loaded. No ERROR,
  FATAL, missing item/block model, texture-reference or atlas-baking failures
  appeared in the inspected startup log. Master volume stayed at 0%.
- User confirmed: "Okay, it works."
- Concrete diff review and `git diff --check`: no blocking findings.

## Remaining visual evidence

Native text-state inspection succeeded, but Windows refused the unsigned
compatibility screenshot script. No security setting was changed and no blind
UI input was sent. This startup pass does not establish the full visual/menu,
resource-reload or audio matrix, nor public-release readiness.

Local evidence, source and artifact are retained under
`bytecraftpack/mod-development/doggy-talents-next-neoforge-26.1.2/evidence/bundled-resource-2026-09-07/`.
