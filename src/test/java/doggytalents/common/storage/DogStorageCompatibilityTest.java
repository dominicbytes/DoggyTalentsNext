package doggytalents.common.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import doggytalents.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;

class DogStorageCompatibilityTest {

    @Test
    void loadsOriginalRespawnOwnerUuidAndWritesOriginalFormat() {
        UUID dogId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID ownerId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        CompoundTag entry = respawnEntry(dogId);
        NBTUtil.putUniqueId(entry, "owner_uuid", ownerId);

        DogRespawnStorage storage = decodeRespawnStorage(entry);
        DogRespawnData data = storage.getData(dogId);

        assertNotNull(data);
        assertEquals(ownerId, data.getOwnerId());

        CompoundTag encoded = encodeRespawnStorage(storage);
        CompoundTag encodedEntry = encoded.getListOrEmpty("respawnData").getCompoundOrEmpty(0);
        assertEquals(ownerId, NBTUtil.getUniqueId(encodedEntry, "owner_uuid"));
        assertFalse(encodedEntry.getString("owner_uuid").isPresent());
    }

    @Test
    void loadsInterimStringRespawnOwnerUuid() {
        UUID dogId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID ownerId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        CompoundTag entry = respawnEntry(dogId);
        entry.putString("owner_uuid", ownerId.toString());

        DogRespawnData data = decodeRespawnStorage(entry).getData(dogId);

        assertNotNull(data);
        assertEquals(ownerId, data.getOwnerId());
    }

    @Test
    void loadsLegacyLocationListAndEntityId() {
        UUID dogId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID ownerId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        CompoundTag entry = new CompoundTag();
        NBTUtil.putUniqueId(entry, "entityId", dogId);
        NBTUtil.putUniqueId(entry, "ownerId", ownerId);
        entry.putDouble("x", 1.25);
        entry.putDouble("y", 64.0);
        entry.putDouble("z", -3.5);
        entry.putString("dimension", "minecraft:overworld");
        ListTag entries = new ListTag();
        entries.add(entry);
        CompoundTag root = new CompoundTag();
        root.put("dog_locations", entries);

        DogLocationStorage storage = DogLocationStorage.CODEC.parse(NbtOps.INSTANCE, root).getOrThrow();
        DogLocationData data = storage.getData(dogId);

        assertNotNull(data);
        assertEquals(ownerId, data.getOwnerId());
        assertEquals(1.25, data.getPos().x());
        assertEquals(64.0, data.getPos().y());
        assertEquals(-3.5, data.getPos().z());
        assertEquals("minecraft:overworld", data.getDimension().identifier().toString());
    }

    @Test
    void skipsStorageEntriesWithoutDogUuid() {
        CompoundTag entry = new CompoundTag();
        entry.put("data", new CompoundTag());

        assertEquals(0, decodeRespawnStorage(entry).getAllUUID().size());
    }

    @Test
    void skipsLocationEntriesWithoutDogUuid() {
        ListTag entries = new ListTag();
        entries.add(new CompoundTag());
        CompoundTag root = new CompoundTag();
        root.put("locationData", entries);

        DogLocationStorage storage = DogLocationStorage.CODEC.parse(NbtOps.INSTANCE, root).getOrThrow();

        assertEquals(0, storage.getAllUUID().size());
    }

    private static CompoundTag respawnEntry(UUID dogId) {
        CompoundTag entry = new CompoundTag();
        NBTUtil.putUniqueId(entry, "uuid", dogId);
        entry.put("data", new CompoundTag());
        return entry;
    }

    private static DogRespawnStorage decodeRespawnStorage(CompoundTag entry) {
        ListTag entries = new ListTag();
        entries.add(entry);
        CompoundTag root = new CompoundTag();
        root.put("respawnData", entries);
        return DogRespawnStorage.CODEC.parse(NbtOps.INSTANCE, root).getOrThrow();
    }

    private static CompoundTag encodeRespawnStorage(DogRespawnStorage storage) {
        return (CompoundTag) DogRespawnStorage.CODEC.encodeStart(NbtOps.INSTANCE, storage).getOrThrow();
    }
}
