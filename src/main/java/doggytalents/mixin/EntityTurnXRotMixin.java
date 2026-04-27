package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * MC 26.1.2 changed Entity.setXRot to Math.clamp(xRot % 360, -90, 90).
 * The % 360 is correct for yaw but breaks pitch: a large negative delta
 * (mouse moving up) can land at e.g. -200 % 360 = 160 → clamped to 90 (down).
 *
 * For the local player, skip the % 360 and clamp directly to [-90, 90].
 */
@Mixin(Entity.class)
public abstract class EntityTurnXRotMixin {

    @Shadow private float xRot;

    @Inject(at = @At("HEAD"), method = "setXRot", cancellable = true)
    private void dtn__fixLocalPlayerSetXRot(float xRot, CallbackInfo ci) {
        if (!(((Object)this) instanceof LocalPlayer)) return;
        if (!Float.isFinite(xRot)) return;
        this.xRot = Mth.clamp(xRot, -90.0f, 90.0f);
        ci.cancel();
    }

}
