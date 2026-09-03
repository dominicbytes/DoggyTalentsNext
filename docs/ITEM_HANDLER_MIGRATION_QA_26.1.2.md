# NeoForge item-handler migration QA — 26.1.2

Date: 2026-09-03

## Scope

`ITEM-HANDLER-01` replaces the NeoForge item APIs marked for removal with the
26.1.2 transactional resource-handler API. The migration covers dog armor,
Pack Puppy, Doggy Tools, Treat Bags, food bowls, rice mills, menu slots, item
transfer helpers, food consumption, and ranged-weapon ammunition lookup.

The mod's established inventory sizes, validation rules, stack-oriented API,
dog-tool shared stack references, and serialized item keys remain unchanged.
Multiplayer-specific behavior is outside this slice.

## Reproduction and implementation evidence

- Forced baseline compilation emitted 65 removal warnings from
  `IItemHandler`, `IItemHandlerModifiable`, `ItemStackHandler`,
  `SlotItemHandler`, and `InvWrapper`.
- The initial `ITEM-HANDLER-01` contract failed to compile because the planned
  `DTNItemStackHandler` target adapter did not exist.
- `DTNItemStackHandler` now binds mod-owned inventories to
  `ItemStacksResourceHandler`, retains the legacy stack access and Value I/O
  schema needed by saves and addons, and implements legacy insertion and
  extraction through root transactions.
- Menus use `ResourceHandlerSlot`; rice-mill output uses
  `VanillaContainerWrapper`; generic inventory utilities accept
  `ResourceHandler<ItemResource>` and make cross-inventory transfer atomic.
- The code-review pass caught and corrected the copied dog-slot index comment
  and widened shared inventory utilities from the concrete DTN adapter to the
  target resource-handler interface.

## Verification

- `gradlew.bat compileJava --warning-mode all --rerun-tasks --no-daemon`:
  PASS, with zero item-handler removal warnings (down from 65).
- `gradlew.bat clean build --no-daemon`: PASS, 33/33 unit tests.
- `gradlew.bat runGameTestServer --no-daemon`: PASS, all eight required tests
  (seven DTN plus the vanilla control).
- Strict Modmaker validation: PASS with zero artifact findings; the two project
  namespace notices remain the previously reviewed `data/minecraft` and
  `data/neoforge` warnings.
- The exact production JAR loaded under NeoForge `26.1.2.101`, reached
  `Done (0.284s)`, and stopped cleanly in the isolated dedicated server.
- `ITEM-HANDLER-01` proves transaction rollback and commit, legacy validation
  and extraction limits, Treat Bag component flushing, merge-first insertion,
  and atomic rollback when source extraction disagrees with target insertion.
- Existing loader tests continue to prove Pack Puppy, Doggy Tools, dog armor,
  food-bowl, and rice-mill persistence through production serialization.

Fresh production JAR:

- File: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: 11,384,633 bytes
- SHA-256: `797753b65e1ff49bd146f7d63a3785019a0d83ba582df3281a3c407953ed5f9e`
- SHA-512: `2f0b7790e48b79599069952dcb0ba8c9df036930de12f584ea831acdae39e4fbdf58bf9795dec7bcc2b9e22e3c3dac4315a3c395b2a43c7557951e9f93247f13`

Strict validation and exact-JAR dedicated-server evidence are recorded with
the archived build artifacts for this slice.
