package doggytalents.common.block;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import doggytalents.common.util.Util;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

class DogBedMaterialManagerTest {

    @Test
    void item01IntegratedClientTagUpdateKeepsSharedServerMaterials() {
        var beddings = DogBedMaterialManager.getBeddings();
        var casings = DogBedMaterialManager.getCasings();
        var originalBeddings = new HashMap<>(beddings);
        var originalCasings = new HashMap<>(casings);
        var key = Util.getResource("test_integrated_tag_update");
        var bedding = new DogBedMaterialManager.NaniBedding(key);
        var casing = new DogBedMaterialManager.NaniCasing(key);

        try {
            beddings.clear();
            casings.clear();
            beddings.put(key, bedding);
            casings.put(key, casing);

            DogBedMaterialManager.onTagsUpdated(
                new TagsUpdatedEvent.ClientPacketReceived(RegistryAccess.EMPTY, true));

            assertSame(bedding, beddings.get(key));
            assertSame(casing, casings.get(key));
        } finally {
            beddings.clear();
            casings.clear();
            beddings.putAll(originalBeddings);
            casings.putAll(originalCasings);
        }
    }
}
