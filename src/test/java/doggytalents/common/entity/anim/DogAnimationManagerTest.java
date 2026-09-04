package doggytalents.common.entity.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import doggytalents.api.anim.DogAnimation;
import org.junit.jupiter.api.Test;

class DogAnimationManagerTest {

    @Test
    void animationBlend01ClassifiesSupportedTransitionTypes() {
        assertEquals(
            DogAnimationManager.BlendState.BLEND_IN,
            DogAnimationManager.computeBlendState(DogAnimation.NONE, DogAnimation.STRETCH));
        assertEquals(
            DogAnimationManager.BlendState.BLEND_OUT,
            DogAnimationManager.computeBlendState(DogAnimation.STRETCH, DogAnimation.NONE));
        assertEquals(
            DogAnimationManager.BlendState.ANIM_TO_ANIM,
            DogAnimationManager.computeBlendState(DogAnimation.STRETCH, DogAnimation.BACKFLIP));
    }

    @Test
    void animationBlend01SelectsDurationFromTheTransitionTargetOrSource() {
        assertEquals(
            3,
            DogAnimationManager.pickBlendDuration(
                DogAnimation.STRETCH,
                DogAnimation.BACKFLIP,
                DogAnimationManager.BlendState.ANIM_TO_ANIM));
        assertEquals(
            3,
            DogAnimationManager.pickBlendDuration(
                DogAnimation.STRETCH,
                DogAnimation.NONE,
                DogAnimationManager.BlendState.BLEND_OUT));
        assertEquals(
            5,
            DogAnimationManager.pickBlendDuration(
                DogAnimation.NONE,
                DogAnimation.STRETCH,
                DogAnimationManager.BlendState.BLEND_IN));
    }
}
