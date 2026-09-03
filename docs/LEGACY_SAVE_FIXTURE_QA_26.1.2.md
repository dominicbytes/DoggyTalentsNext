# Frozen 1.21.1 Dog Save Fixture QA — NeoForge 26.1.2

## Result

`PASS` — `doggytalents:save_01_legacy_dog_fixture_upgrade` loaded a committed 1.21.1-format dog payload in the NeoForge 26.1.2 GameTest server. Six DTN tests plus Minecraft's control test completed with `All 7 required tests passed`.

## Fixture provenance

The fixture is derived directly from the writer and reader contract in `Dog` at the immutable 1.21.1 oracle commit `87532faf2ab3696bc6d57c4502ec2dc22bbe6ea2`. Its normal fields follow `addDTNAdditionalSavedData(CompoundTag)`; `level_dire` and `customSkinHash` intentionally exercise reader-only legacy aliases retained by that oracle. It is a DTN-owned entity payload rather than a captured complete Minecraft world or vanilla entity record.

- Resource: `src/main/resources/data/doggytalents/gametest/fixtures/dog-1.21.1-oracle.snbt`
- SHA-256: `da18b522d67f6fe1bee143ae833f3f4d7a14f7f3b521d44cfcab83741180d88b`
- Integrity enforcement: the GameTest refuses to load the fixture if its checksum changes without an explicit test update.

## Compatibility assertions

The loader-aware test covers core state and behavior flags, normal level, the legacy `level_dire` fallback, the legacy `customSkinHash` fallback, a talent, dyed accessory, artifact, bed and bowl positions, all persisted statistics, dog group, wander restriction, owner-distance state, and petting state. It then writes the loaded dog through the 26.1 serializer and verifies canonical `level_kami` and `doggytalents_dog_skin` output.

This closes the representative frozen 1.21.1 dog-payload subgate of `SAVE-01`. Full save persistence across a stopped and restarted server process remains open.
