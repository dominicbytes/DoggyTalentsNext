package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Fix for MC 26.1.2: Entity.setXRot now stores Math.clamp(xRot % 360, -90, 90).
 *
 * Entity.turn() calls setXRot(currentXRot + xDelta) with an unbounded delta.
 * For large deltas, the % 360 can map the value to an angle outside [-90, 90]
 * (e.g., -200 % 360 = 160 → clamped to 90, making upward mouse motion look DOWN).
 * Pre-clamping to [-90, 90] before the first setXRot call in turn() ensures
 * % 360 receives a safe value and the pitch behaves correctly.
 *
 * Only applied for LocalPlayer so server-side and other entities are unaffected.
 */
@Mixin(Entity.class)
public class EntityTurnXRotMixin {

    @Redirect(
        method = "turn",
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/world/entity/Entity;setXRot(F)V",
                 ordinal = 0)
    )
    private void dtn__redirectTurnSetXRot(Entity self, float xRot) {
        if (self instanceof LocalPlayer) {
            self.setXRot(Mth.clamp(xRot, -90.0f, 90.0f));
        } else {
            self.setXRot(xRot);
        }
    }

}
