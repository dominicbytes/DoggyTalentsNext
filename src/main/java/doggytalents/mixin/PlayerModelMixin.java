package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    @Inject(at = @At("TAIL"), method = "setupAnim")
    protected void dtn__setupAnim(AvatarRenderState state, CallbackInfo info) {
        // TODO: dog-sleep-on-player head tilt needs reworking for the render state system
    }

}
