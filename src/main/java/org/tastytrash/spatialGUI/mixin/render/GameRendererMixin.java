package org.tastytrash.spatialGUI.mixin.render;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;

//? if >1.21.1 {
/*import net.minecraft.client.renderer.fog.FogRenderer;
 *///?}

//? if <26.1.2 {
import net.minecraft.client.gui.GuiGraphics;
//?}

//? if >=26.1.2 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Final @Shadow private GameRenderState gameRenderState;
    //? if <26.2 {
    @Final @Shadow private FogRenderer fogRenderer;
    //?}

    @Inject(method = "render", at = @At("HEAD"))
    private void spatialGUI$prepareTargetEarly(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.prepareTarget();
            renderer.onFrameStart();
        }
    }

    @Inject(method = "processBlurEffect()V", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$cancelBlurEffect(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && renderer.shouldCapture() && SpatialGUI.config.enabled) {
            ci.cancel();
        }
    }

    //? if >=26.2 {
    /^@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V"))
     ^///?} else {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
            //?}
    private void spatialGUI$beforeGuiRender(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();

        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
            renderer.clearTarget();

            SpatialGUIRenderer.skipWindowOverride = true;
            this.gameRenderState.windowRenderState.guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        }
    }

    //? if fabric && <26.2 {
    @Redirect(method = "extractGui", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
    ))
    private void spatialGUI$redirectScreenExtraction(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (SpatialGUI.config.enabled && screen instanceof AbstractContainerScreen<?> && screen == renderer.getHookedScreen()) {
            SpatialGUIRenderer.skipWindowOverride = false;
            renderer.extractIsolatedScreen(screen, partialTick);
            SpatialGUIRenderer.skipWindowOverride = true;
        } else {
            screen.extractRenderStateWithTooltipAndSubtitles(graphics, mouseX, mouseY, partialTick);
        }
    }
    //?}

    @Inject(method = "render", at = @At("TAIL"))
    private void spatialGUI$renderIsolatedScreen(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            SpatialGUIRenderer.skipWindowOverride = false;
            this.gameRenderState.windowRenderState.guiScale = Minecraft.getInstance().getWindow().getGuiScale();

            //? if >=26.2 {
            /^renderer.getScreenGuiRenderer().render();
             ^///?} else {
            renderer.getScreenGuiRenderer().render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
            //?}
            renderer.getScreenGuiRenderer().endFrame();

            SpatialGUIRenderer.skipWindowOverride = false;

            renderer.renderInWorldPost();
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$overrideHideHand(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
            //? if >=26.2 {
            /^this.gameRenderState.guiRenderState.isHudHidden = false;
             ^///?}

            if (SpatialGUIClient.getEffectiveFirstPersonMode() && SpatialGUI.config.hideHandsInFirstPerson) {
                ci.cancel();
            }
        }
    }
}
*///?} else if >1.21.1 {
/*@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Final @Shadow private FogRenderer fogRenderer;

    @Inject(method = "render", at = @At("HEAD"))
    private void spatialGUI$prepareTargetEarly(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.prepareTarget();
            renderer.onFrameStart();
        }
    }

    @Inject(method = "processBlurEffect()V", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$cancelBlurEffect(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && renderer.shouldCapture() && SpatialGUI.config.enabled) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
    private void spatialGUI$beforeGuiRender(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();

        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
            renderer.clearTarget();

            SpatialGUIRenderer.skipWindowOverride = true;
        }
    }
    //? if fabric {
    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
    ))
    private void spatialGUI$redirectScreenExtraction(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (SpatialGUI.config.enabled && screen instanceof AbstractContainerScreen<?> && screen == renderer.getHookedScreen()) {
            SpatialGUIRenderer.skipWindowOverride = false;
            renderer.extractIsolatedScreen(screen, partialTick);
            SpatialGUIRenderer.skipWindowOverride = true;
        } else {
            screen.renderWithTooltipAndSubtitles(graphics, mouseX, mouseY, partialTick);
        }
    }
    //?} else {
    /^@Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
    ))
    private void spatialGUI$redirectScreenExtraction(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (SpatialGUI.config.enabled && screen instanceof AbstractContainerScreen<?> && screen == renderer.getHookedScreen()) {
            SpatialGUIRenderer.skipWindowOverride = false;
            renderer.extractIsolatedScreen(screen, partialTick);
            SpatialGUIRenderer.skipWindowOverride = true;
        } else {
            net.neoforged.neoforge.client.ClientHooks.drawScreen(screen, graphics, mouseX, mouseY, partialTick);
        }
    }
    ^///?}

    @Inject(method = "render", at = @At("TAIL"))
    private void spatialGUI$renderIsolatedScreen(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            SpatialGUIRenderer.skipWindowOverride = false;

            renderer.getScreenGuiRenderer().render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
            renderer.getScreenGuiRenderer().incrementFrameNumber();

            SpatialGUIRenderer.skipWindowOverride = false;

            renderer.renderInWorldPost();
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$overrideHideHand(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
            if (SpatialGUIClient.getEffectiveFirstPersonMode() && SpatialGUI.config.hideHandsInFirstPerson) {
                ci.cancel();
            }
        }
    }
}
*///?} else {
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void spatialGUI$prepareTargetEarly(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.prepareTarget();
            renderer.onFrameStart();
        }
        SpatialGUIRenderer.skipWindowOverride = true;
    }

    //? if 1.21.1 {
    /*@Inject(method = "processBlurEffect(F)V", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$cancelBlurEffect(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && renderer.shouldCapture() && SpatialGUI.config.enabled) {
            ci.cancel();
        }
    }
    *///?}

    //? if 1.21.1 {
    /*@Inject(method = "render", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    *///?} else {
    @Inject(method = "render", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    //?}
    private void spatialGUI$drawBeforeHud(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"))
    private void spatialGUI$suppressPostEffect(net.minecraft.client.renderer.PostChain postChain, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer != null && renderer.shouldCapture() && SpatialGUI.config.enabled) {
            return;
        }
        postChain.process(partialTick);
    }

    //? if fabric {
    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
    ))
    private void spatialGUI$redirectScreenExtraction(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (SpatialGUI.config.enabled && screen instanceof AbstractContainerScreen<?> && screen == renderer.getHookedScreen()) {
            SpatialGUIRenderer.skipWindowOverride = false;
            renderer.extractIsolatedScreen(screen, partialTick);
            SpatialGUIRenderer.skipWindowOverride = true;
            renderer.renderInWorldPost();
        } else {
            screen.renderWithTooltip(graphics, mouseX, mouseY, partialTick);
        }
    }
    //?} else {
    /*@Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
    ))
    private void spatialGUI$redirectScreenExtraction(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var renderer = SpatialGUIClient.renderer();
        if (SpatialGUI.config.enabled && screen instanceof AbstractContainerScreen<?> && screen == renderer.getHookedScreen()) {
            SpatialGUIRenderer.skipWindowOverride = false;
            renderer.extractIsolatedScreen(screen, partialTick);
            SpatialGUIRenderer.skipWindowOverride = true;
            renderer.renderInWorldPost();
        } else {
            net.neoforged.neoforge.client.ClientHooks.drawScreen(screen, graphics, mouseX, mouseY, partialTick);
        }
    }
    *///?}

    @Inject(method = "render", at = @At("TAIL"))
    private void spatialGUI$endRender(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            SpatialGUIRenderer.skipWindowOverride = true;
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$overrideHideHand(CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (renderer.shouldCapture() && SpatialGUI.config.enabled) {
            renderer.renderInWorldPost();
            if (SpatialGUIClient.getEffectiveFirstPersonMode() && SpatialGUI.config.hideHandsInFirstPerson) {
                ci.cancel();
            }
        }
    }
}
//?}