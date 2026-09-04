# ITEM-DATA-01 QA — NeoForge 26.1.2

## Contract

Legacy item custom data must preserve nested compounds, reject a field with the wrong NBT type, and clear without leaving readable custom data.

## Reproduction

Before the fix, `doggytalents:item_data_01_custom_data_compatibility` was the only failing required GameTest:

`wrongly typed nested custom data was accepted as an empty compound`

Minecraft 26.1 removed the typed `CompoundTag.contains(String, int)` overload. The port used an untyped presence check followed by `getCompoundOrEmpty`, changing malformed-data behavior from absent (`null`) to an empty compound.

## Fix

`ItemUtil.getTagElement` now verifies that the stored tag is actually a `CompoundTag` before returning it.

## Verification

- Clean Gradle build: passed.
- JUnit: 37 tests, 0 failures, 0 errors.
- GameTest: 12/12 required tests passed (11 DTN and one vanilla control).
- Strict Modmaker validation: 0 errors, 0 final warnings.
- Production dedicated server: exact JAR reached `Done (0.338s)` and stopped cleanly.
- JAR SHA-256: `e367097491db8334008782f6c1e52e0db2bcc096edb0b792275993af88b07e04`.

The Windows Netty epoll/kqueue debug-appender messages are native transport probes; the server selected its supported transport and completed startup.
