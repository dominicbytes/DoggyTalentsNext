package doggytalents.common.storage;

import doggytalents.DoggyTalentsNext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;

/** Imports original flat-file indexes once; a current index always takes precedence. */
final class LegacyDogSavedData {
    private LegacyDogSavedData() {
    }

    static <T extends SavedData> T get(ServerLevel level, SavedDataType<T> type, String legacyFile) {
        return get(level, level.getDataStorage(), type,
            level.getServer().getWorldPath(LevelResource.ROOT).resolve("data").resolve(legacyFile));
    }

    static <T extends SavedData> T get(ServerLevel level, SavedDataStorage storage,
            SavedDataType<T> type, Path legacyFile) {
        T current = storage.get(type);
        if (current != null) return current;
        if (!Files.exists(legacyFile)) return storage.computeIfAbsent(type);

        try {
            // This is DTN-owned data, not vanilla random-sequence data. Its existing codecs
            // understand the old payload; only the file location changed in the port.
            var root = storage.readTagFromDisk(legacyFile, null,
                SharedConstants.getCurrentVersion().dataVersion().version());
            var payload = root.getCompound("data").orElseThrow(
                () -> new IllegalStateException("Legacy dog index has no data compound: " + legacyFile));
            T imported = type.codecFactory().create(level)
                .parse(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), payload)
                .getOrThrow(message -> new IllegalStateException("Cannot import " + legacyFile + ": " + message));
            storage.set(type, imported);
            DoggyTalentsNext.LOGGER.info("[SAVE-03-LEGACY-INDEX] Imported {} into {}; original retained",
                legacyFile.getFileName(), type.id());
            return imported;
        } catch (IOException | RuntimeException exception) {
            // Do not replace an unreadable legacy index with a silently empty one.
            throw new IllegalStateException("Cannot read legacy dog index " + legacyFile, exception);
        }
    }
}
