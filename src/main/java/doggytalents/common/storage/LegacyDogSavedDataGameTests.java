package doggytalents.common.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.SavedDataStorage;

public final class LegacyDogSavedDataGameTests {
    private static final UUID OFFLINE_DOG = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID DEAD_DOG = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID OWNER = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private LegacyDogSavedDataGameTests() {
    }

    /** REVIEW-UPGRADE-01-INDEX: actual original files import, persist, and never resurrect consumed records. */
    public static void originalWorldIndexes(GameTestHelper helper) {
        Path directory = null;
        try {
            directory = Files.createTempDirectory("dtn-original-index-");
            Path locations = fixture(directory, "doggytalentsDogLocations.dat");
            Path respawns = fixture(directory, "doggytalentsDeadDogs.dat");
            var level = helper.getLevel();
            Path canonical = Files.createDirectory(directory.resolve("current"));
            try (var disk = new SavedDataStorage(level, canonical, level.getServer().getFixerUpper(), level.registryAccess())) {
                var locationStore = LegacyDogSavedData.get(level, disk, DogLocationStorage.TYPE, locations);
                var location = locationStore.getData(OFFLINE_DOG);
                require(helper, location != null && OWNER.equals(location.getOwnerId())
                    && "Offline Pup".equals(location.getDogName()) && location.getPos().x == 1026.5,
                    "original offline location was not imported");
                var respawnStore = LegacyDogSavedData.get(level, disk, DogRespawnStorage.TYPE, respawns);
                var stored = respawnStore.getData(DEAD_DOG);
                require(helper, stored != null && OWNER.equals(stored.getOwnerId())
                    && "Revive Upgrade Pup".equals(stored.getDogName()), "original respawn record was not imported");
                var dog = stored.respawn(level, helper.makeMockPlayer(GameType.SURVIVAL),
                    helper.absolutePos(new BlockPos(1, 2, 1)));
                require(helper, dog != null && OWNER.equals(dog.getOwnerUUID())
                    && "Revive Upgrade Pup".equals(dog.getName().getString()), "imported record did not revive correctly");
                verifyLegacyAttributes(helper, dog);
                dog.discard();
                DogRespawnStorage.get(level).remove(DEAD_DOG);
                DogLocationStorage.get(level).remove(DEAD_DOG);
                respawnStore.remove(DEAD_DOG);
                locationStore.remove(OFFLINE_DOG);
            }
            try (var disk = new SavedDataStorage(level, canonical, level.getServer().getFixerUpper(), level.registryAccess())) {
                require(helper, LegacyDogSavedData.get(level, disk, DogRespawnStorage.TYPE, respawns).getData(DEAD_DOG) == null,
                    "restart re-imported an already consumed revival record");
                require(helper, LegacyDogSavedData.get(level, disk, DogLocationStorage.TYPE, locations).getData(OFFLINE_DOG) == null,
                    "restart replaced the current location index with its old backup");
            }
            require(helper, Files.exists(locations) && Files.exists(respawns), "migration deleted its source evidence");
            Path bad = Files.createFile(directory.resolve("unreadable.dat"));
            Path empty = Files.createDirectory(directory.resolve("failed-import"));
            try (var disk = new SavedDataStorage(level, empty, level.getServer().getFixerUpper(), level.registryAccess())) {
                boolean rejected = false;
                try {
                    LegacyDogSavedData.get(level, disk, DogRespawnStorage.TYPE, bad);
                } catch (IllegalStateException expected) {
                    rejected = true;
                }
                require(helper, rejected && disk.get(DogRespawnStorage.TYPE) == null,
                    "unreadable original index was replaced with an empty current one");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Original-world index fixture failed", exception);
        } finally {
            if (directory != null) {
                try (var paths = Files.walk(directory)) {
                    for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.delete(path);
                } catch (IOException exception) {
                    throw new IllegalStateException("Could not clean the task-created index fixture", exception);
                }
            }
        }
        helper.succeed();
    }

    private static Path fixture(Path directory, String name) throws IOException {
        Path file = directory.resolve(name);
        String resource = name.equals("doggytalentsDeadDogs.dat") ? "dead_dogs.dat" : "dog_locations.dat";
        try (var input = LegacyDogSavedDataGameTests.class.getResourceAsStream(
                "/data/doggytalents/gametest/fixtures/1.21.1-world/" + resource)) {
            if (input == null) throw new IOException("Missing original-world fixture " + name);
            byte[] bytes = input.readAllBytes();
            String expected = name.equals("doggytalentsDeadDogs.dat")
                ? "bf2cf93f7f684d3ac07bcdfba21bfbee91c2c7d82460652174bedd2f45908cb8"
                : "5690596fd0f245c4fbae7937665af745e522fc462b580483bf9f9fc2136da2f4";
            if (!com.google.common.hash.Hashing.sha256().hashBytes(bytes).toString().equals(expected)) {
                throw new IOException("Original-world fixture hash changed: " + name);
            }
            Files.write(file, bytes);
        }
        return file;
    }

    private static void verifyLegacyAttributes(GameTestHelper helper, doggytalents.common.entity.Dog dog) {
        var attribute = dog.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        attribute.setBaseValue(0.47);
        var modifier = net.minecraft.resources.Identifier.fromNamespaceAndPath("doggytalents", "upgrade_test");
        attribute.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
            modifier, 0.13, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        var output = net.minecraft.world.level.storage.TagValueOutput.createWithoutContext(net.minecraft.util.ProblemReporter.DISCARDING);
        dog.saveWithoutId(output);
        var tag = output.buildResult();
        for (var entry : tag.getListOrEmpty("attributes")) {
            if (entry instanceof net.minecraft.nbt.CompoundTag attr
                    && "minecraft:movement_speed".equals(attr.getStringOr("id", ""))) {
                attr.putString("id", "minecraft:generic.movement_speed");
            }
        }
        attribute.removeModifier(modifier);
        attribute.setBaseValue(0.3);
        dog.load(net.minecraft.world.level.storage.TagValueInput.create(
            net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        require(helper, Math.abs(attribute.getBaseValue() - 0.47) < 0.00001
            && attribute.hasModifier(modifier) && Math.abs(attribute.getValue() - 0.60) < 0.00001,
            "legacy attribute rename lost a custom base value or permanent modifier");
        require(helper, tag.getListOrEmpty("attributes").stream().anyMatch(entry ->
            entry instanceof net.minecraft.nbt.CompoundTag attr
                && "minecraft:generic.movement_speed".equals(attr.getStringOr("id", ""))),
            "attribute migration mutated its source NBT");
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) helper.fail(message);
    }
}
