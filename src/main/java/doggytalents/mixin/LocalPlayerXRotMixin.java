package doggytalents.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class LocalPlayerXRotMixin {

    private static final Logger LOGGER = LogManager.getLogger("DTN/XRot");

    @Inject(at = @At("HEAD"), method = "setXRot")
    private void dtn__setXRotHead(float xRot, CallbackInfo info) {
        if (!(((Object)this) instanceof LocalPlayer)) return;
        var self = (Entity)(Object)this;
        float old = self.getXRot();
        if (Math.abs(xRot - old) > 0.01f) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < Math.min(6, trace.length); i++) {
                sb.append("\n  at ").append(trace[i]);
            }
            LOGGER.info("[DTN xRot PRE ] stored={} arg={} thread={}{}",
                String.format("%.2f", old),
                String.format("%.2f", xRot),
                Thread.currentThread().getName(),
                sb);
        }
    }

    @Inject(at = @At("TAIL"), method = "setXRot")
    private void dtn__setXRotTail(float xRot, CallbackInfo info) {
        if (!(((Object)this) instanceof LocalPlayer)) return;
        var self = (Entity)(Object)this;
        float stored = self.getXRot();
        // Log whenever the stored value changes (catches the % 360 + clamp result)
        LOGGER.info("[DTN xRot POST] stored={} (arg={})",
            String.format("%.2f", stored),
            String.format("%.2f", xRot));
    }

}
