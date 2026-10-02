package org.tastytrash.spatialGUI.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;
import org.tastytrash.spatialGUI.util.MouseHandlerUtil;

@Pseudo
@Mixin(targets = "dev.emi.emi.screen.EmiScreenManager", remap = false)
public class EMIScreenManagerMixin {

    @ModifyVariable(method = "mouseClicked(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static double spatialGUI$clickX(double x) {
        return spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "mouseClicked(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static double spatialGUI$clickY(double y) {
        return spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "mouseReleased(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static double spatialGUI$releaseX(double x) {
        return spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "mouseReleased(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static double spatialGUI$releaseY(double y) {
        return spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "mouseDragged(DDIDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static double spatialGUI$dragX(double x) {
        return spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "mouseDragged(DDIDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static double spatialGUI$dragY(double y) {
        return spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "mouseScrolled(DDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static double spatialGUI$scrollX(double x) {
        return spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "mouseScrolled(DDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static double spatialGUI$scrollY(double y) {
        return spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "render(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static int spatialGUI$renderX(int x) {
        return (int) spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "render(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static int spatialGUI$renderY(int y) {
        return (int) spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "drawBackground(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static int spatialGUI$bgX(int x) {
        return (int) spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "drawBackground(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static int spatialGUI$bgY(int y) {
        return (int) spatialGUI$mapY(y);
    }

    @ModifyVariable(method = "drawForeground(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static int spatialGUI$fgX(int x) {
        return (int) spatialGUI$mapX(x);
    }

    @ModifyVariable(method = "drawForeground(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private static int spatialGUI$fgY(int y) {
        return (int) spatialGUI$mapY(y);
    }

    private static double spatialGUI$mapX(double original) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && SpatialGUI.config.isEnabled() && renderer.shouldCapture()) {
            return MouseHandlerUtil.getLastPos(true);
        }
        return original;
    }

    private static double spatialGUI$mapY(double original) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && SpatialGUI.config.isEnabled() && renderer.shouldCapture()) {
            return MouseHandlerUtil.getLastPos(false);
        }
        return original;
    }
}