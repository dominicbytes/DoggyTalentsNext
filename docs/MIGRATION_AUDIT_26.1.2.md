# Doggy Talents Next 26.1.2 completion audit

Date: 2026-09-03  
Target: Minecraft Java 26.1.2, NeoForge 26.1.2  
Canonical implementation fork: <https://github.com/dominicbytes/DoggyTalentsNext>  
Canonical branch: `26.1.2`

## Verdict

MiiRaGe's `26.1.2` branch was the correct implementation base, but it was not at feature parity or release-ready. The dominicbytes continuation now compiles, packages, passes 30 unit tests and six DTN GameTests, and reaches a dedicated-server `Done` state against the current stable NeoForge 26.1.2 loader. Client/runtime evidence is still not broad enough to support the original branch's "GUI / client rendering done" claim.

The `26.1.2-beta` branch should not be continued. It is an older, separately developed migration line. The stable branch contains 32 post-forkpoint commits and more than 100 later fixes relative to beta. Keep beta for archaeology only.

Use DashieDev commit `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2` as the immutable 1.21.1 behavior oracle. Newer DashieDev branches may supply fixes or migration patterns, but they are not the authority for scope. Do not wait for upstream merge activity.

## Immutable refs examined

| Ref | SHA | Role |
|---|---|---|
| DashieDev fork point | `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2` | 1.21.1 parity oracle |
| MiiRaGe `26.1.2` | `dc93249f12e79f0c494cece04b08674ed4a1a41d` | implementation base / tag `v26.1.2.24` |
| MiiRaGe `26.1.2-beta` | `c123cb668cad873172487e76e505cda7db314b11` | obsolete migration line |
| DashieDev `1.21-master` inspected tip | `061ab865` | optional fix source |
| DashieDev `1.21.11-master` inspected tip | `b3fa9a52` | optional render/data migration reference |

DashieDev PR #184 is open, non-draft, merge-conflicted (`mergeable_state: dirty`), has no reviews, and reports only server boot/world initialization as testing. It contains the MiiRaGe stable SHA and does not establish talent, GUI, persistence, multiplayer, or rendering parity: <https://github.com/DashieDev/DoggyTalentsNext/pull/184>.

## Verified baseline

- Fresh `clean build` succeeds on MiiRaGe stable with Java 25 and an 8 GiB Gradle heap.
- The original four-GiB setting fails during a clean NeoForm/Vineflower decompile with an out-of-memory error.
- The original project hard-codes a Linux-only `org.gradle.java.home`, making ordinary Windows builds fail without a command-line override.
- The original source has conflicting NeoForge declarations: `.29-beta` in `gradle.properties` and `.70-beta` in `build.gradle`.
- NeoForge's official Maven metadata identifies `26.1.2.101` as the current stable release. The parity branch builds successfully against it.
- `test` passes 30 tests, and the NeoForge GameTest server runs six registered DTN persistence tests successfully. Java 25 CI remains open.
- Static artifact audit reports zero errors. The two namespace warnings (`data/minecraft` and `data/neoforge`) require intent review but are not automatic defects.
- An isolated headless Gradle run loads Doggy Talents Next `26.1.2.24`, Minecraft `26.1.2`, and NeoForge `26.1.2.101`, creates a new world, and reaches server `Done` without a client-classloading failure.
- The exact packaged JAR (`df14ae8bb8960cdd652ee52dc81aa8f63755ffb1499f13d23edc89e818af8f99`) also loads from a clean NeoForge `26.1.2.101` server `mods/` directory and reaches `Done (0.287s)` without a DTN classloading failure. See `docs/PRODUCTION_SERVER_QA_26.1.2.md`.
- The vanilla automation bot is correctly rejected by the NeoForge server because it is not a NeoForge client, so two-sided login/gameplay remains untested.
- All 149 generated item-definition JSON files resolve to existing model JSON files. The apparent missing `rice_crop` and `soy_crop` item definitions are crops without registered block items, so they are not gaps.
- The branch still produces 72 Java warnings against `.101`, dominated by NeoForge item-handler APIs marked for removal. These are maintenance debt, not immediate 26.1.2 blockers.

Evidence boundary: `COMPILES`, static packaging, 30 unit tests, six DTN in-process persistence GameTests, a representative frozen 1.21.1 dog-payload upgrade, development-classpath server startup, and clean production-JAR dedicated-server startup are proven. Client, process-restart persistence, full-world fixture upgrades, and visual parity are not yet proven. Multiplayer acceptance is explicitly waived for the dominicbytes fork and remains upstream work.

## Confirmed parity gaps

### P0 — release and multiplayer safety

1. **The automated harness is still incomplete.** JUnit covers network and storage compatibility, while NeoForge GameTests prove real-dog core serialization, a frozen 1.21.1 dog-payload upgrade, representative talents, dog-owned inventories, accessory color, artifact state, complete statistics, established food-bowl/dog-bed/rice-mill state, and nonzero rice-mill progress. Add lifecycle and process-restart coverage. Java 25 CI remains required.

2. **Legacy generic network tunnel remains direction-unsafe.** Every message is multiplexed through one `playBidirectional` payload and integer discriminator. `DogPacket` assumes a serverbound sender without first checking direction, while client-only handlers share the same registry. A packet delivered on the wrong flow can dereference a null sender or resolve client classes on the wrong side. Replace it with typed, explicitly directional payload registrations or add strict direction metadata while migrating incrementally.

3. **Packet decode hardening is only partially complete.** The stacked network branch adds non-negative maximum counts to `CanineTrackerPackets`, `ConductingBonePackets`, `DogGroupPackets`, `HeelByGroupPackets`, `DogSyncDataPacket`, `DoggyArtifactsSerializer`, and `Dim2BlockPosSerializer`, bounds tracker names, validates dog-sync state IDs, and exercises those paths with hostile-input tests. Remaining work is explicit per-message size budgeting, truncated/trailing-byte policy, and verified disconnect/reporting behavior at the channel boundary.

4. **Production-JAR dedicated-server classloading is proven.** The exact packaged JAR loads from a clean NeoForge server `mods/` directory and reaches `Done`. No DTN client-class resolution failure occurs; the remaining log errors are environment-level native transport probing and external-service timeouts documented in the QA report.

5. **Persistence and upgrade behavior is only partially proven.** Original 1.21.1 respawn-owner UUIDs, the interim 26.1.2 string UUID format, legacy location-list/entity-ID keys, canonical respawn re-encoding, and malformed-entry rejection have unit coverage. Loader-aware GameTests now prove that a real `Dog` round-trips UUID, owner/tame/name, gender, mode, hunger, incapacitation value, normal/kami levels, behavior flags, bed/bowl locations, representative plain/stateful talents, Pack Puppy and Doggy Tools inventories/options, accessory color, an artifact, and complete statistics through the 26.1 `ValueInput`/`ValueOutput` entity bridge. A checksum-locked 1.21.1 payload additionally proves legacy dire-level and custom-skin fallback plus canonical rewrite. Production block-entity serialization preserves food-bowl inventory/placer, dog-bed materials/owner/names, rice-mill inventory, and canonical nonzero rice-mill progress. A process restart and full-world fixture still need assertions. Remaining silent exception handlers in persistence paths must log enough context to diagnose partial data loss.

### P1 — user-visible parity regressions

6. **Dynamic item and block tinting is disabled.** The client listeners are commented out with an explicit migration note. Dyeable accessories, double-dye accessories, dog plushie, ceremonial garb, MIDI keyboard, dog bath item, and biome-water dog bath block need 26.1 `ItemTintSource` / `BlockTintSource` implementations and generated model tint arrays. DashieDev's newer line contains reusable custom tint codecs and data-generation patterns.

7. **Dog-bed inventory rendering lost component-driven variants.** `DogBedItemOverride` is an empty stub, and the generated item definition points to one plain dog-bed model. Inventory/held/ground/fixed views therefore need a 26.1 special item model that resolves casing and bedding components. The newer DashieDev line contains a partial `SpecialModelRenderer` reference implementation.

8. **Alternative helmet rendering is disabled.** `DogArmorHelmetAltModel.getModel()` and `getDummy()` return null, and the third-party armor hook is explicitly skipped. Restore the default player-helmet projection and the supported NeoForge client-extension hook, or retire the two corresponding config options with an explicit compatibility decision.

9. **Armor trims were dropped.** The 1.21.1 renderer submitted trim sprites for equipped armor; the 26.1.2 renderer only draws the base mapped texture and glint. Port trim rendering through equipment assets/material sprites and add visual checks.

10. **Puppy head scaling needs visual acceptance.** The stacked render-state branch now feeds inherited `isBaby` state into dog, accessory, and head-item render contexts while preserving custom-model opt-out behavior. Unit coverage passes; default/custom model and accessory combinations still need fixed client captures.

11. **Howling is silent.** `Dog.getHowlSound()` changed from `SoundEvents.WOLF_HOWL` to null, and `howl()` now exits. Resolve the 26.1 wolf sound variant/holder correctly and add a server-visible trigger plus manual audio check.

12. **Custom dog nameplates were removed.** The old renderer supplied owner-sensitive name visibility, through-wall behavior, mode/hunger/gender details, duty indication, health-on-sneak, and nearby owner text. The port retains helper methods but never submits them. Four config options are now declaration-only: `BLOCK_THIRD_PARTY_NAMETAG`, `DONT_RENDER_DIFFOWNER_NAME`, `RENDER_DIFFOWNER_NAME_DIFFERENT`, and `SHOW_DOG_NAME_THRU_WALL`. Rebuild this on the 26.1 name-tag submit pipeline and test each config branch.

13. **MultiLineFlatButton right-side drawing remains empty.** Determine whether any live screen uses the right-aligned branch; either implement it with a visual test or remove the unreachable API and prove no parity loss.

### P2 — completion and maintenance debt

14. **Item custom data is only partially migrated.** Most legacy item NBT is now wrapped in `DataComponents.CUSTOM_DATA`; this is functional compatibility, not a typed data-component migration. Inventory-like items already use native container/equippable components. Introduce typed components only where they add validation, networking, or persistence value, with old-key migration tests. Do not rewrite entity save NBT merely to satisfy the README wording.

15. **Deprecated NeoForge item-handler surface.** Migrate the 72 warnings after parity-critical work so the change does not obscure behavior fixes. Cover transfer, slot validation, Pack Puppy, Treat Bag, food bowl, rice mill, armor, and dog-tool inventory semantics first.

16. **README status is inaccurate.** It labels GUI/client rendering done and references an old beta loader. Update it only after the corresponding acceptance scenarios pass.

17. **Publication metadata still targets DashieDev.** Keep original authorship and license attribution, but point issue/update links at the canonical release repository when a dominicbytes release is actually ready. Do not claim public-release readiness until the repository's LGPL files and README's narrower all-rights-reserved asset/code statement have been reconciled for redistribution.

## PR-sized implementation sequence

1. **Build/release foundation** — portable Java 25 discovery, one stable NeoForge property, deterministic JAR, Java 25 CI, basic validation. Verify two clean builds and identical artifact hashes.
2. **Network hardening** — explicit directions, stable typed IDs/codecs, bounded decode, authority checks, dedicated-server separation, hostile-input tests.
3. **Persistence contract** — freeze 1.21.1 fixtures; round-trip dog/talent/accessory/inventory/location/respawn state; log partial failures; verify restart and upgrade.
4. **Tint and item-model restoration** — custom tint codecs, block tint source, generated tint arrays, dog-bed special item renderer; visual matrix.
5. **Dog render-state parity** — puppy head/accessory scaling, custom nameplates/configs, armor helmets/trims, render-side isolation.
6. **Audio/UI cleanup** — howl sound and the live MultiLineFlatButton branch; targeted manual checks.
7. **Gameplay parity sweep** — representative tests for every talent category, commands, interactions, food/crops/recipes, dog beds/baths, trackers/whistles, mounting, incapacitation/respawn, accessories, and custom skins.
8. **Release acceptance** — clean client and dedicated server, save restart, clean production-JAR install, log audit, artifact hash, documentation, and independent review. Multiplayer acceptance is waived for this fork and left to upstream.

Each PR should start from a failing test or fixed reproduction, make the smallest relevant change, and rerun `clean build`, the narrow regression test, and the applicable runtime gate.

## Acceptance test IDs

| ID | Blocking scenario | Evidence |
|---|---|---|
| BUILD-01 | Clean Java 25 build on Windows and Linux without machine-specific paths | CI + local wrapper logs |
| BUILD-02 | Two clean builds produce the same JAR hash | artifact hashes |
| NET-01 | Wrong-direction, unknown, negative, oversized, duplicate, and truncated payloads are rejected safely | unit/loader tests |
| NET-02 | Non-owner cannot mutate another player's dog; owner can | two-client GameTest/manual |
| SERVER-01 | Production JAR starts on a clean dedicated NeoForge server without client class resolution | PASS — `docs/PRODUCTION_SERVER_QA_26.1.2.md` |
| SAVE-01 | 1.21.1 fixture upgrades with dog identity, talents, accessories, inventories, locations, and respawn state intact | PARTIAL — unit compatibility tests plus six in-process persistence GameTests, including a frozen 1.21.1 dog payload, statistics, established block-entity state, and rice-mill progress; full-world restart remains |
| RENDER-01 | Adult/puppy, default/custom skin, armor/trim/helmet, wet/incapacitated, and accessory matrix matches oracle | fixed captures + manual approval |
| RENDER-02 | Every nameplate config changes only its documented behavior | client test/manual captures |
| ITEM-01 | Every registered item has a definition/model; dye colors and dog-bed material variants survive save/reload | datagen validation + client captures |
| GAME-01 | Representative happy, boundary, invalid, authority, and persistence case for every talent family passes | GameTests + manual gaps |
| RELEASE-01 | Exact production JAR passes clean client/server install, restart, log, license, and metadata gates | release QA report; multiplayer waived for this fork |

## Current implementation change

The parity branch removes the Linux-only Java path, raises the clean-build heap to 8 GiB, makes `neoforge_version` the single source of truth, updates it to stable `26.1.2.101`, and configures deterministic archive ordering/timestamps while removing the wall-clock manifest timestamp. Two fresh Java 25 builds produced the same SHA-256 (`b7bc05bd7d32b5a65619c400ab3fd64b388409cbf4d1c14978e22512e7cdb286`), GitHub Actions passed, and the isolated development server reached `Done`. The server run configuration honors the modmaker runtime directory and leased port when those environment variables are present.

The stacked network-hardening branch enables NeoForge's JUnit environment and adds bounded collection decoding/encoding for tracker, conducting-bone, group, dog-sync, artifact, and dimension-position payloads. It also bounds transmitted dog names and rejects unknown dog-sync state IDs. Eight hostile/boundary tests and a clean build pass. Explicit packet directions and client/server handler separation remain open work.

The stacked render-state branch restores puppy head scaling from `LivingEntityRenderState.isBaby`, propagates it to copied dog and synchronized accessory models, and covers adult, puppy, custom-model opt-out, and accessory propagation behavior. Client visual acceptance remains required.

The persistence compatibility branch restores the original UUID NBT contract for dead-dog owners while continuing to read the interim string format emitted by the 26.1.2 draft. It also restores component-preserving custom-name storage, rejects malformed location/respawn records before constructing data objects, and freezes the original location keys in tests. Five persistence compatibility tests and the 30-test suite pass; full-world restart coverage remains open.

The dog persistence branches add the NeoForge `gameTestServer` run and six stable `SAVE-01` subgates. The harness registers explicit 26.1 `GameTestInstance`s, correcting the earlier function-only registration that did not create runnable tests. The core test asserts vanilla identity, core DTN state, and location maps. The extended test asserts talent levels, Pack Puppy and Doggy Tools inventory/options, dyed accessory state, and an artifact. The block-entity tests reconstruct production food-bowl, dog-bed, and rice-mill block entities and assert their established identity/material/name/inventory contracts plus canonical nonzero rice-mill progress. The statistics test covers every persisted counter and entity-kill entry, catches stale derived totals, and proves replacement-load semantics. The checksum-locked oracle fixture covers broad 1.21.1 DTN payload loading, legacy field fallbacks, and canonical rewrite. All six DTN tests pass in the loader-aware server; process restart remains open.

The production-server gate installs the official checksum-verified NeoForge `26.1.2.101` server into a clean disposable directory, copies only the freshly built production JAR into `mods/`, and reaches `Done`. The strict Modmaker artifact validator also passes with zero findings. This closes dedicated-server packaging/classloading acceptance; it does not substitute for client or save-restart testing.

## Bytecraft state

This audit intentionally does not mutate the Bytecraft registry. The subject remains `individual_test_pending`, owned by Modtester under the current protocol. This document is an implementation audit under the user's explicit override to continue the fork, not a formal Modplanner READY transition.
