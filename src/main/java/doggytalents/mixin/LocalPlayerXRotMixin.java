package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

/**
 * Workaround for WSL2/X11 GLFW raw mouse motion bug: when Raw Input is enabled
 * under WSL2, GLFW reports enormous accumulated deltas (30k+ pixels per frame)
 * that lock the camera pitch at 90. Detect WSL2 on first grab and auto-disable
 * raw input if the delta exceeds a sane threshold.
 */
@Mixin(MouseHandler.class)
public class LocalPlayerXRotMixin {

    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    private static final double RAW_INPUT_SANITY_THRESHOLD = 2000.0;
    private boolean dtn__checkedRawInput = false;

    @Inject(at = @At("HEAD"), method = "handleAccumulatedMovement")
    private void dtn__detectBrokenRawInput(CallbackInfo info) {
        if (dtn__checkedRawInput) return;
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (!((MouseHandler)(Object)this).isMouseGrabbed()) return;

        // If accumulated delta is absurdly large on first grab, raw input is broken
        // (WSL2/X11 GLFW raw mouse motion reports OS-accumulated deltas instead of
        // per-frame hardware deltas, making the camera lock at pitch 90).
        if (Math.abs(accumulatedDX) > RAW_INPUT_SANITY_THRESHOLD
                || Math.abs(accumulatedDY) > RAW_INPUT_SANITY_THRESHOLD) {
            mc.options.rawMouseInput().set(false);
            mc.options.save();
            mc.mouseHandler.releaseMouse();
            mc.mouseHandler.grabMouse();
        }
        dtn__checkedRawInput = true;
    }

}
