package org.tastytrash.spatialGUI.mixin.screens;

import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    //? if >=26.2 {
//    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
//    private void spatialGUI$removeBlur(CallbackInfo ci) {
//        if (SpatialGUIRenderer.isExtractingScreen) {
//            ci.cancel();
//        }
//    }
    //?} else {
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$removeBlur(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (SpatialGUIRenderer.isExtractingScreen) {
            ci.cancel();
        }
    }
    //?}
}
