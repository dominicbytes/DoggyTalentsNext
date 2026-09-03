package doggytalents.client.entity.render.layer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

class DogArmorHelmetAltModelTest {

    @Test
    void playerHelmetModelLeavesOnlyItsTraversalRootAndHeadVisible() {
        var model = new HumanoidModel<HumanoidRenderState>(new ModelPart(List.of(), Map.of(
            "head", new ModelPart(List.of(), Map.of("hat", emptyPart())),
            "body", emptyPart(),
            "right_arm", emptyPart(),
            "left_arm", emptyPart(),
            "right_leg", emptyPart(),
            "left_leg", emptyPart()
        )));

        DogArmorHelmetAltModel.configureForHelmet(model);

        assertTrue(model.root().visible);
        assertTrue(model.head.visible);
        assertFalse(model.hat.visible);
        assertFalse(model.body.visible);
        assertFalse(model.rightArm.visible);
        assertFalse(model.leftArm.visible);
        assertFalse(model.rightLeg.visible);
        assertFalse(model.leftLeg.visible);
    }

    private static ModelPart emptyPart() {
        return new ModelPart(List.of(), Map.of());
    }
}
