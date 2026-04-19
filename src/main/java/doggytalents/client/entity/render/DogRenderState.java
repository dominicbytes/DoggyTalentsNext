package doggytalents.client.entity.render;

import doggytalents.common.entity.Dog;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class DogRenderState extends LivingEntityRenderState {
    // Store entity reference for layer access (pragmatic approach)
    // This is safe as long as rendering stays single-threaded
    public Dog dog;
    // Animation data needed for model setup
    public float walkAnimSpeed;
    public float walkAnimPos;
    public float ageInTicksForAnim;
    public float headYawForAnim;
    public float headPitchForAnim;
}
