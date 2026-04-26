package doggytalents.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import doggytalents.client.DTNClientDogSleepOnManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Camera.class)
public class CameraMixin {

    private static final Logger LOGGER = LogManager.getLogger("DTN/Camera");
    private int dtn__logTick = 0;

    @Inject(at = @At("TAIL"), method = "update", cancellable = false)
    protected void dtn__setup(DeltaTracker deltaTracker, CallbackInfo info) {
        var self = (Camera)(Object)this;

        // Log camera state once per second (every 20 calls) to diagnose "camera at feet" bug
        dtn__logTick++;
        if (dtn__logTick >= 20) {
            dtn__logTick = 0;
            var entity = self.entity();
            if (entity != null) {
                float eyeHeight = entity instanceof LivingEntity le ? le.getEyeHeight() : entity.getEyeHeight();
                LOGGER.info("[DTN camera] xRot={} yRot={} | entity.xRot={} entity.getY()={} entity.yo={} entity.eyeHeight={} | camera.pos={}",
                    String.format("%.2f", self.xRot()),
                    String.format("%.2f", self.yaw()),
                    String.format("%.2f", entity.getXRot()),
                    String.format("%.2f", entity.getY()),
                    String.format("%.2f", entity.yo),
                    String.format("%.2f", eyeHeight),
                    self.position()
                );
            } else {
                LOGGER.info("[DTN camera] entity is null");
            }
        }

        DTNClientDogSleepOnManager.get().afterCameraSetup(self, self.entity());
    }

}
