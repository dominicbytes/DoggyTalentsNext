package doggytalents.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class LocalPlayerXRotMixin {

    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    private static final Logger LOGGER = LogManager.getLogger("DTN/Mouse");
    private int dtn__tick = 0;

    @Inject(at = @At("HEAD"), method = "handleAccumulatedMovement")
    private void dtn__logMouseDelta(CallbackInfo info) {
        dtn__tick++;
        if (dtn__tick < 20) return;
        dtn__tick = 0;

        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        LOGGER.info("[DTN mouse] accDX={} accDY={} | invertY={} smoothCamera={} sensitivity={} grabbed={}",
            String.format("%.3f", accumulatedDX),
            String.format("%.3f", accumulatedDY),
            mc.options.invertMouseY().get(),
            mc.options.smoothCamera,
            String.format("%.2f", mc.options.sensitivity().get()),
            ((MouseHandler)(Object)this).isMouseGrabbed()
        );
    }

}
