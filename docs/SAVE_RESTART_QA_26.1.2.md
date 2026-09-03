# Doggy Talents Next 26.1.2 save-restart QA

Date: 2026-09-03

Gate: `SAVE-01-RESTART`

Result: **PASS**

## Runtime contract

Two separate Java 25 dedicated-server processes loaded the same isolated world, `dtn-save-restart-683eb326`, from a production NeoForge `26.1.2.101` installation. The server's `mods/` directory contained the exact audited release candidate and no development classes:

- JAR: `DoggyTalentsNext-26.1.2-26.1.2.24.jar`
- Size: `11,374,476` bytes
- SHA-256: `df14ae8bb8960cdd652ee52dc81aa8f63755ffb1499f13d23edc89e818af8f99`

Process A created a persistent `doggytalents:dog` tagged `dtn_save_restart`, asserted its live state, ran `save-all flush`, and stopped cleanly. Process B then started in a new JVM against the same world and repeated every assertion before another clean save and stop.

## Persisted state asserted

The marker groups intentionally span independent DTN persistence paths:

| Marker suffix | State |
|---|---|
| `CORE_PASS` | gender, mode, hunger, incapacitation, size, normal level, and kami level |
| `LOADOUT_PASS` | stateful talent, dyed accessory, and artifact |
| `LOCATION_PASS` | bed, wander center, home position, and home radius |
| `STATS_PASS` | walking, sprinting, swimming, water-surface, ridden, damage, and per-entity kill counters |
| `POLICY_PASS` | group, friendly-fire/team policy, obedience, sitting/duty, patrol, mounting, armor visibility, teleport, and combat strategies |

Process A emitted:

```text
SAVE01_A_CORE_PASS
SAVE01_A_LOADOUT_PASS
SAVE01_A_LOCATION_PASS
SAVE01_A_STATS_PASS
SAVE01_A_POLICY_PASS
```

After the first process had fully exited, process B emitted:

```text
SAVE01_B_CORE_PASS
SAVE01_B_LOADOUT_PASS
SAVE01_B_LOCATION_PASS
SAVE01_B_STATS_PASS
SAVE01_B_POLICY_PASS
```

The explicit `SAVE01_B_ENTITY_FAIL` negative marker did not occur. A full post-restart `data get entity` also showed the expected values, including the same entity UUID.

## Evidence

- Process A log: `save01-restart-process-a.log`, 13,347 bytes, SHA-256 `1695f6ec2aaa51a424bfd01a07ffceceae63a37fbcf986f725a7812916b0c50c`
- Process B log: `save01-restart-process-b.log`, 12,547 bytes, SHA-256 `952f45e54fe4768bf304e635b293cac779e858a080816482277b2f0affb776b2`
- Local evidence root: `bytecraftpack/qa-work/doggy-talents-save01-server/logs/`

Both logs identify Doggy Talents Next `26.1.2.24`, Minecraft `26.1.2`, and NeoForge `26.1.2.101`, then reach server `Done`. The Windows console's optional Netty kqueue/epoll debug-appender exceptions are host transport probes, not DTN load or persistence failures; they do not appear in the persisted server logs and did not prevent either clean lifecycle.

## Evidence boundary

Together with the checksum-locked 1.21.1 entity fixture and loader-aware persistence GameTests, this closes `SAVE-01` for the tested dog-owned state and production process restart. It is not a captured full 1.21.1 world upgrade and does not claim client-rendering or multiplayer coverage. Multiplayer remains explicitly waived for the dominicbytes fork.
