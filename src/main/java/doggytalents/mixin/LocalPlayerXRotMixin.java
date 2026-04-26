package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Fix for MC 26.1.2: Entity.setXRot now does Math.clamp(xRot % 360, -90, 90).
 * The % 360 converts large turn deltas to arbitrary mid-range angles instead of
 * properly clamping to ±90. Pre-clamp the xRot before it reaches setXRot so that
 * downward overshoots stay at 90 and upward overshoots stay at -90, matching
 * the 1.21 behaviour where setXRot stored raw values and turn() clamped explicitly.
 */
@Mixin(Entity.class)
public class LocalPlayerXRotMixin {

    @ModifyArg(
        method = "turn",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setXRot(F)V", ordinal = 0)
    )
    private float dtn__clampTurnXRot(float xRot) {
        if (!(((Object)this) instanceof LocalPlayer)) return xRot;
        // Clamp before setXRot so the % 360 in 26.1.2 setXRot cannot wrap values outside [-90,90]
        return Mth.clamp(xRot, -90.0f, 90.0f);
    }

}
