package org.tastytrash.spatialGUI.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;

//? if >=26.2 {
/*@Mixin(net.minecraft.client.gui.Hud.class)
*///?} else {
@Mixin(net.minecraft.client.gui.Gui.class)
//?}
public class HudMixin {
    //? if >=26.1.2 {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    //?} else {
    /*@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
     *///?}
    private void spatialGUI$hideCrosshair(CallbackInfo ci) {
        if (SpatialGUIClient.isEnabled()
                && SpatialGUIClient.getEffectiveFirstPersonMode()
                && !SpatialGUI.config.useCrosshairForFirstPerson
                && SpatialGUIClient.renderer() != null
                && SpatialGUIClient.renderer().getHookedScreen() != null) {
            ci.cancel();
        }
    }

    //? if >=26.1.2 {
    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    //?} else if >1.20.1 {
//    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    //?} else {
    /*@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    *///?}
    private void spatialGUI$hideHotbar(CallbackInfo ci) {
        if (SpatialGUIClient.isEnabled() && SpatialGUI.config.hideHotbar && SpatialGUIClient.renderer() != null && SpatialGUIClient.renderer().getHookedScreen() != null) {
            ci.cancel();
        }
    }
}