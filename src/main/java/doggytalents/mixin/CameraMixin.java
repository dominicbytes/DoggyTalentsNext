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
    private float dtn__lastXRot = Float.NaN;

    @Inject(at = @At("TAIL"), method = "update", cancellable = false)
    protected void dtn__setup(DeltaTracker deltaTracker, CallbackInfo info) {
        var self = (Camera)(Object)this;
        var entity = self.entity();

        // Immediately log whenever entity xRot changes (catches external forcing)
        if (entity != null) {
            float currentXRot = entity.getXRot();
            if (!Float.isNaN(dtn__lastXRot) && Math.abs(currentXRot - dtn__lastXRot) > 0.01f) {
                var mc2 = net.minecraft.client.Minecraft.getInstance();
                String sn = mc2.screen != null ? mc2.screen.getClass().getSimpleName() : "none";
                var vehicle = entity.getVehicle();
                var passengers = entity.getPassengers();
                LOGGER.info("[DTN camera] xRot changed: {} -> {} screen={} vehicle={} passengers={}",
                    String.format("%.2f", dtn__lastXRot),
                    String.format("%.2f", currentXRot),
                    sn,
                    vehicle != null ? vehicle.getType().toShortString() + "/xRot=" + String.format("%.1f", vehicle.getXRot()) : "none",
                    passengers.stream().map(p -> p.getType().toShortString()).toList());
            }
            dtn__lastXRot = currentXRot;
        }

        // Log full state once per second
        dtn__logTick++;
        if (dtn__logTick >= 20) {
            dtn__logTick = 0;
            if (entity != null) {
                float eyeHeight = entity instanceof LivingEntity le ? le.getEyeHeight() : entity.getEyeHeight();
                var mc = net.minecraft.client.Minecraft.getInstance();
                String screenName = mc.screen != null ? mc.screen.getClass().getName() : "none";
                LOGGER.info("[DTN camera] xRot={} yRot={} | entity.xRot={} entity.getY()={} entity.yo={} eyeHeight={} | pos={} | screen={}",
                    String.format("%.2f", self.xRot()),
                    String.format("%.2f", self.yaw()),
                    String.format("%.2f", entity.getXRot()),
                    String.format("%.2f", entity.getY()),
                    String.format("%.2f", entity.yo),
                    String.format("%.2f", eyeHeight),
                    self.position(),
                    screenName
                );
            } else {
                LOGGER.info("[DTN camera] entity is null");
            }
        }

        DTNClientDogSleepOnManager.get().afterCameraSetup(self, self.entity());
    }

}
