package org.tastytrash.spatialGUI.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.joml.Vector2d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
import org.tastytrash.spatialGUI.util.MouseHandlerUtil;
import org.tastytrash.spatialGUI.util.RenderUtil;
import org.tastytrash.spatialGUI.util.RenderUtil.QuadBasis;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Unique private static double lastPosX = Double.NaN;
    @Unique private static double lastPosY = Double.NaN;

    //? if <=1.21.1 {
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;
    //?}

    @Unique
    private static boolean shouldApplyMouseOverride() {
        Minecraft client = Minecraft.getInstance();
        //? if >=26.2 {
        /*Screen screen = client.screen;
         *///?} else {
        Screen screen = client.screen;
        //?}
        return screen instanceof AbstractContainerScreen<?> && org.tastytrash.spatialGUI.SpatialGUI.config.enabled;
    }

    //? if >1.21.1 {
    /*@ModifyReturnValue(method = "getScaledXPos*", at = @At("RETURN"))
    private static double spatialGUI$modifyX(double original) {
        return overrideMousePosition(original, true);
    }

    @ModifyReturnValue(method = "getScaledYPos*", at = @At("RETURN"))
    private static double spatialGUI$modifyY(double original) {
        return overrideMousePosition(original, false);
    }
    *///?} else {
    // 1.21.1 has no getScaledXPos/YPos; vanilla computes xpos * guiScaledWidth / screenWidth inline.
    @ModifyExpressionValue(
            method = {"onPress", "onScroll", "handleAccumulatedMovement"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;xpos:D", opcode = Opcodes.GETFIELD)
    )
    private double spatialGUI$modifyRawX(double original) {
        return overrideRawPosition(original, true);
    }

    @ModifyExpressionValue(
            method = {"onPress", "onScroll", "handleAccumulatedMovement"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ypos:D", opcode = Opcodes.GETFIELD)
    )
    private double spatialGUI$modifyRawY(double original) {
        return overrideRawPosition(original, false);
    }

    @Unique
    private static double overrideRawPosition(double raw, boolean isX) {
        if (!shouldApplyMouseOverride()) {
            return raw;
        }

        Window window = Minecraft.getInstance().getWindow();
        double toScaled = isX
                ? (double) window.getGuiScaledWidth() / (double) window.getScreenWidth()
                : (double) window.getGuiScaledHeight() / (double) window.getScreenHeight();

        double scaled = overrideMousePosition(raw * toScaled, isX);
        if (Double.isNaN(scaled)) {
            return raw;
        }
        return scaled / toScaled;
    }
    //?}

    @Unique
    private static double overrideMousePosition(double original, boolean isX) {
        if (!shouldApplyMouseOverride()) {
            return original;
        }

        double guiScale = SpatialGUI.config.autoCalculateGuiScale
                ? SpatialGUI.config.calculateAutoGuiScale(Minecraft.getInstance().getWindow().getHeight())
                : SpatialGUI.config.guiScale;

        double srcX, srcY;
        if (SpatialGUIRenderer.isCrosshairModeActive()) {
            Minecraft mc = Minecraft.getInstance();
            //? if >1.21.1 {
            /*srcX = mc.getWindow().getScreenWidth() / 2.0;
            srcY = mc.getWindow().getScreenHeight() / 2.0;
            *///?} else {
            srcX = mc.getWindow().getScreenWidth() / 2.0;
            srcY = mc.getWindow().getScreenHeight() / 2.0;
            //?}
        } else {
            srcX = Minecraft.getInstance().mouseHandler.xpos();
            srcY = Minecraft.getInstance().mouseHandler.ypos();
        }

        var renderer = SpatialGUIClient.renderer();
        if (renderer == null) return original;

        QuadBasis quadBasis = renderer.getInventoryRenderer().getQuadBasis();
        if (quadBasis == null) return original;

        Vector2d mouse = RenderUtil.getInventoryMousePositionRay(srcX, srcY, quadBasis, renderer.getTargetManager().getInventoryTarget());

        if (mouse == null) {
            return isX ? lastPosX : lastPosY;
        }

        lastPosX = mouse.x / guiScale;
        lastPosY = mouse.y / guiScale;

        return isX ? lastPosX : lastPosY;
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$cancelPlayerRotation(double mousea, CallbackInfo ci) {
        if (SpatialGUIRenderer.isCrosshairModeActive()) {
            //? if <=1.21.1 {
            // 1.21.1 has no xrel/yrel in onMove, so hand over the accumulated deltas here.
            MouseHandlerUtil.addFreeLookDelta(this.accumulatedDX, this.accumulatedDY);
            //?}
            ci.cancel();
        }
    }

    //? if >26.2 {
    /*@Inject(method = "onMove(JDDDD)V", at = @At("HEAD"))
    private void spatialGUI$captureMouseMotion(long handle, double xpos, double ypos, double xrel, double yrel, CallbackInfo ci) {
        if (SpatialGUIRenderer.isCrosshairModeActive()) {
            MouseHandlerUtil.addFreeLookDelta(xrel, yrel);
        }
    }
    *///?}
}