package doggytalents.client.entity.render;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;

class DoggyArmorMappingTest {

    @Test
    void armor01SelectsTheFirstHumanoidEquipmentLayer() {
        var first = new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("iron"));
        var second = new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("overlay"));
        var equipmentInfo = new EquipmentClientInfo(Map.of(
            EquipmentClientInfo.LayerType.HUMANOID,
            List.of(first, second)
        ));

        assertSame(first, DoggyArmorMapping.firstHumanoidLayer(equipmentInfo).orElseThrow());
    }

    @Test
    void armor01RejectsEquipmentWithoutAHumanoidLayer() {
        assertTrue(DoggyArmorMapping.firstHumanoidLayer(new EquipmentClientInfo(Map.of())).isEmpty());
    }
}
