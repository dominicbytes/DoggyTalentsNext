package doggytalents.client.entity.model.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import doggytalents.api.anim.DogAnimation;
import doggytalents.api.anim.DogAnimation.DogAnimBlendInMode;
import doggytalents.api.anim.DogAnimation.DogAnimBlendOutMode;
import doggytalents.client.entity.model.dog.AnimSnapshot;
import doggytalents.client.entity.model.dog.DogModel;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

class DogAnimationBlendConfigTest {

    @Test
    void animationBlend01PreservesDefaultCrossFadeContract() {
        assertEquals(5, DogAnimation.BlendInConfig.DEFAULT.blendTick());
        assertEquals(3, DogAnimation.BlendOutConfig.DEFAULT.blendTick());

        assertEquals(DogAnimBlendInMode.BLEND, DogAnimation.STRETCH.blendIn().mode());
        assertEquals(5, DogAnimation.STRETCH.blendIn().blendTick());
        assertEquals(DogAnimBlendOutMode.CAPTURE_POSE, DogAnimation.STRETCH.blendOut().mode());
        assertEquals(3, DogAnimation.STRETCH.blendOut().blendTick());
        assertTrue(DogAnimation.STRETCH.hasBlendIn());
        assertTrue(DogAnimation.STRETCH.hasBlendOut());
    }

    @Test
    void animationBlend01PreservesSpecialTransitionTuning() {
        assertEquals(
            DogAnimBlendInMode.HEAD_ROT_AND_CHILDREN_ONLY,
            DogAnimation.STAND_UP.blendIn().mode());
        assertEquals(5, DogAnimation.STAND_UP.blendIn().blendTick());

        assertEquals(DogAnimBlendInMode.BLEND, DogAnimation.STAND_QUICK.blendIn().mode());
        assertEquals(3, DogAnimation.STAND_QUICK.blendIn().blendTick());
        assertEquals(DogAnimBlendInMode.BLEND, DogAnimation.BACKFLIP.blendIn().mode());
        assertEquals(3, DogAnimation.BACKFLIP.blendIn().blendTick());

        assertFalse(DogAnimation.STAND_QUICK.blendIn().blendHeadRotAndChildrenOnly());
        assertFalse(DogAnimation.BACKFLIP.blendIn().blendHeadRotAndChildrenOnly());
    }

    @Test
    void animationBlend01InterpolatesRotationsInRadiansAcrossTheShortestArc() {
        var first = new AnimSnapshot();
        var second = new AnimSnapshot();
        first.root.xrot = 170 * Mth.DEG_TO_RAD;
        second.root.xrot = -170 * Mth.DEG_TO_RAD;
        var model = new DogModel(DogModel.createBodyLayer().bakeRoot());

        AnimSnapshot.blendAndApply(0.5f, first, second, model);

        assertEquals(Mth.PI, Math.abs(model.root.xRot), 0.0001f);
    }
}
