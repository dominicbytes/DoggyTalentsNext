# Skin Catalog QA (NeoForge 26.1.2)

## Scope

- Contract: `SKIN-CATALOG-01`
- Ports DashieDev's curated dog-skin catalog cleanup and ordering from `1.21-master`.
- Removes the retired `arcanine_shiro`, `otter`, `ammy_divine_rebirth`, and `borzoi_long` entries.
- Adds the `wangwang` and `wangwang_mouthclosed` entries with their final model and texture resources.

Catalog upstream commits: `40ccbd454`, `c378e9f2a`, and `5b2fcc060`.
WangWang resource lineage: `0ef5fdb55`, `235680556`, and `164ef6ad6`.

## Compatibility adaptations

- The reorganized catalog conflicted with earlier NeoForge catalog edits, so the resource was resolved to DashieDev's curated data snapshot with one trailing-whitespace cleanup.
- Only the four final WangWang resource files were imported from `164ef6ad6`; replaying its full history would duplicate catalog changes already present in this slice.
- No renderer or runtime Java behavior is changed.

## Regression proof

The focused resource contract initially failed with 143 catalog entries instead of the curated 141. After the catalog cleanup, it exposed the missing `wangwang` model dependency. The completed test verifies:

- the curated catalog has exactly 141 entries;
- the first four entries retain their expected order, including both WangWang variants;
- skin identifiers are unique;
- all catalog `use_model` values resolve to bundled model JSON;
- both WangWang textures are bundled;
- all four retired skin identifiers are absent.

## Automated verification

- Focused skin-catalog regression: **passed**.
- Clean unit build: **49/49 passed** across 21 test classes.
- GameTest server: **14/14 required tests passed** (13 DTN tests plus the vanilla test).
- Strict release validator: **0 errors**.
  - Project warnings: 2 known shared-namespace warnings.
  - Built artifact: 0 errors, 0 warnings.
- Exact tested artifact: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- SHA-256: `23e343bc840a456e1a18d9b0cf469aad45954be9fdc68ea6f8d4cea1617594e7`
- Production-style dedicated server reached `Done (0.261s)!` with the exact tested artifact, then stopped cleanly with all dimensions saved.

## Manual gate

No new rendering code is introduced. The two imported model and texture pairs are exact upstream resources; optional in-client visual confirmation remains a manual follow-up and is not required for this data-integrity slice.

## Verdict

**PUBLIC_RELEASE_READY** for the `SKIN-CATALOG-01` slice. All applicable automated, artifact, and production-server gates passed; multiplayer remains outside the user-approved scope.
