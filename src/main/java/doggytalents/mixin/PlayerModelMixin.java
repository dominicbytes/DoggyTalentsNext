package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import doggytalents.client.DTNClientDogSleepOnManager;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    @Inject(at = @At("TAIL"), method = "setupAnim")
    protected void dtn__setupAnim(AvatarRenderState state, CallbackInfo info) {
        var self = (PlayerModel)(Object) this;
        // TODO: AvatarRenderState doesn't directly expose the LivingEntity.
        // The dog-sleep-on-player head tilt feature needs reworking for the new render state system.
        // DTNClientDogSleepOnManager.get().afterPlayerModelSetupAnim(entity, 0, 0, 0, 0, 0, self);
    }

}
