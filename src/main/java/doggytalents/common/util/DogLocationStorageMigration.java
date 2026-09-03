package doggytalents.common.util;

import doggytalents.DoggyTalentsNext;
import doggytalents.common.lib.Constants;
import doggytalents.common.storage.DogLocationStorage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedDataType;

public class DogLocationStorageMigration {

    // A type using the old storage ID, for migration purposes only
    private static final SavedDataType<DogLocationStorage> OLD_TYPE = new SavedDataType<>(
        Identifier.parse(Constants.STORAGE_DOG_LOCATION_OLD),
        DogLocationStorage::new,
        DogLocationStorage.CODEC,
        DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
    );

    public static void checkAndMigrate(ServerLevel level) {
        var savedData = level.getDataStorage();

        DogLocationStorage newStorage = null;
        try {
            newStorage = savedData.get(DogLocationStorage.TYPE);
        } catch (Exception e) {
            DoggyTalentsNext.LOGGER.warn(
                "[SAVE-02-LOCATION-MIGRATION-DIAGNOSTIC] Failed to read current dog location storage in dimension {}",
                level.dimension().identifier(), e
            );
        }
        if (newStorage != null)
            return;

        DogLocationStorage oldStorage = null;
        try {
            oldStorage = savedData.get(OLD_TYPE);
        } catch (Exception e) {
            DoggyTalentsNext.LOGGER.warn(
                "[SAVE-02-LOCATION-MIGRATION-DIAGNOSTIC] Failed to read legacy dog location storage {} in dimension {}",
                Constants.STORAGE_DOG_LOCATION_OLD, level.dimension().identifier(), e
            );
        }
        if (oldStorage == null)
            return;
        if (oldStorage.getAll().isEmpty())
            return;

        savedData.set(DogLocationStorage.TYPE, oldStorage);
        oldStorage.setDirty();

        var dummy = new DogLocationStorage();
        savedData.set(OLD_TYPE, dummy);
        dummy.setDirty();
    }

}
