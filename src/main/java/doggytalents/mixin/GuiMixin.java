package doggytalents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import doggytalents.client.DTNWolfMountCustomGuiOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Gui.class)
public class GuiMixin {
    
    @Inject(at = @At("HEAD"),  method = "renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", cancellable = true)
    protected void dtn__renderVehicleHealth(GuiGraphicsExtractor graphics, CallbackInfo info) {
        var self = (Gui)(Object)this;
        if (DTNWolfMountCustomGuiOverlay.onRenderVehicleHealth(graphics, self))
            info.cancel();
    }

    @Inject(at = @At("HEAD"),  method = "getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
    protected void dtn__getVehicleMaxHearts(LivingEntity vehicle, CallbackInfoReturnable<Integer> info) {
        var result = DTNWolfMountCustomGuiOverlay.onGetVehicleMaxHearts(vehicle);
        if (result.isPresent()) {
            info.setReturnValue(result.get());
        }
    }
}   
