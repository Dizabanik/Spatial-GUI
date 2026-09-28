package org.tastytrash.spatialGUI.mixin.gui;

import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;

//? if >=26.2 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

@Mixin(Hud.class)
public class HudMixin {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$hideCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (SpatialGUI.config.enabled 
                && SpatialGUIClient.getEffectiveFirstPersonMode()
                && !SpatialGUI.config.useCrosshairForFirstPerson
                && SpatialGUIClient.renderer() != null
                && SpatialGUIClient.renderer().getHookedScreen() != null) {
            ci.cancel();
        }
    }
}
*///?} else {
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class HudMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$hideCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (SpatialGUI.config.enabled 
                && SpatialGUIClient.getEffectiveFirstPersonMode()
                && !SpatialGUI.config.useCrosshairForFirstPerson
                && SpatialGUIClient.renderer() != null
                && SpatialGUIClient.renderer().getHookedScreen() != null) {
            ci.cancel();
        }
    }
}
//?}
