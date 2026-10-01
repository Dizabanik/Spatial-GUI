package org.tastytrash.spatialGUI.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
import org.tastytrash.spatialGUI.SpatialGUI;
//? if fabric {
 import net.fabricmc.api.ClientModInitializer;
 import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
//? } else if neoforge {
/*import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
*///? }

//? if fabric {
 public class SpatialGUIClient implements ClientModInitializer {
//? } else if neoforge {
/*@Mod(value = SpatialGUI.MOD_ID, dist = Dist.CLIENT)
public class SpatialGUIClient {
*///? }

    private static SpatialGUIRenderer renderer;
    private static boolean effectiveFirstPersonMode = false;
    private static boolean switchedToFirstPersonDueToBlock = false;
    //? if fabric {
    @Override
    public void onInitializeClient() {
        renderer = new SpatialGUIRenderer();

        ScreenEvents.BEFORE_INIT.register((clientArg, screen, scaledWidth, scaledHeight) -> {
            if (SpatialGUIClient.shouldHookScreen(screen)) {
                renderer.hookScreen(screen);
            }
        });
    }
    //? } else if neoforge {
    /*public SpatialGUIClient() {
        renderer = new SpatialGUIRenderer();

        NeoForge.EVENT_BUS.addListener(this::onScreenInit);
    }

    private void onScreenInit(ScreenEvent.Init.Pre event) {
        var screen = event.getScreen();

        if (SpatialGUIClient.shouldHookScreen(screen)) {
            renderer.hookScreen(screen);
        }
    }
    *///?}

    public static boolean shouldHookScreen(Screen screen) {
        if (screen == null) return false;
        if (screen.getClass().getName().contains("TitleScreen")) return false;
        if (screen.getClass().getName().contains("ReceivingLevelScreen")) return false;
        if (screen.getClass().getName().contains("LevelLoadingScreen")) return false;
        if (screen.getClass().getName().contains("ChatScreen")) return false;
        if (SpatialGUI.config.allScreens && Minecraft.getInstance().level != null) return true;
        if (screen instanceof AbstractContainerScreen<?>) return true;
        if (screen instanceof PauseScreen) return SpatialGUI.config.pauseScreen;
        return false;
    }

    public static SpatialGUIRenderer renderer() {
        return renderer;
    }

    public static boolean getEffectiveFirstPersonMode() {
        return effectiveFirstPersonMode;
    }

    public static void setEffectiveFirstPersonMode(boolean value) {
        effectiveFirstPersonMode = value;
    }

    public static boolean getSwitchedToFirstPersonDueToBlock() {
        return switchedToFirstPersonDueToBlock;
    }

    public static void setSwitchedToFirstPersonDueToBlock(boolean value) {
        switchedToFirstPersonDueToBlock = value;
    }
}