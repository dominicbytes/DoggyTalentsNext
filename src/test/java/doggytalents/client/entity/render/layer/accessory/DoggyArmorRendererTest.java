package doggytalents.client.entity.render.layer.accessory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

class DoggyArmorRendererTest {

    @Test
    void trimUsesTheHumanoidArmorAtlasPath() {
        var trim = new ArmorTrim(
            Holder.direct(new TrimMaterial(MaterialAssetGroup.REDSTONE, Component.empty())),
            Holder.direct(new TrimPattern(Identifier.withDefaultNamespace("sentry"), Component.empty(), false))
        );

        assertEquals(
            Identifier.withDefaultNamespace("trims/entity/humanoid/sentry_redstone"),
            DoggyArmorRenderer.trimSpriteId(trim, EquipmentAssets.IRON)
        );
    }
}
