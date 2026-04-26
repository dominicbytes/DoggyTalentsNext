package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import doggytalents.client.DTNClientPettingManager;
import doggytalents.client.PettingArmPose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    @Inject(at = @At("TAIL"), method = "setupAnim")
    protected void dtn__setupAnim(AvatarRenderState state, CallbackInfo info) {
        if (state.leftArmPose != PettingArmPose.VALUE && state.rightArmPose != PettingArmPose.VALUE)
            return;
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var entity = level.getEntity(state.id);
        if (!(entity instanceof LivingEntity living)) return;
        var self = (PlayerModel)(Object) this;
        var manager = DTNClientPettingManager.get();
        if (state.rightArmPose == PettingArmPose.VALUE)
            manager.applyTransform(self, living, HumanoidArm.RIGHT);
        if (state.leftArmPose == PettingArmPose.VALUE)
            manager.applyTransform(self, living, HumanoidArm.LEFT);
    }

}
