package org.tastytrash.spatialGUI.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
import org.tastytrash.spatialGUI.SpatialGUI;
//? if fabric {
 import net.fabricmc.api.ClientModInitializer;
 import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
//? } else if neoforge {
/*import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
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
        SpatialGUIKeybinds.register();

        ScreenEvents.BEFORE_INIT.register((clientArg, screen, scaledWidth, scaledHeight) -> {
            if (SpatialGUIClient.shouldHookScreen(screen)) {
                renderer.hookScreen(screen);
            }
        });
    }
    //? } else if neoforge {
    /*public SpatialGUIClient(net.neoforged.bus.api.IEventBus modBus) {
        renderer = new SpatialGUIRenderer();

        modBus.addListener((RegisterKeyMappingsEvent e) -> SpatialGUIKeybinds.register(e));

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
        //? if >1.21.1 {
        if (screen instanceof BookViewScreen || screen instanceof BookEditScreen || screen instanceof BookSignScreen) return SpatialGUI.config.books;
//        //?} else {
//        if (screen instanceof BookViewScreen || screen instanceof BookEditScreen) return SpatialGUI.config.books;
        //?}
        if (screen instanceof CraftingScreen) return SpatialGUI.config.crafting;
        if (screen instanceof FurnaceScreen || screen instanceof SmokerScreen || screen instanceof BlastFurnaceScreen) return SpatialGUI.config.furnaces;
        if (screen instanceof AnvilScreen) return SpatialGUI.config.anvils;
        if (screen instanceof EnchantmentScreen) return SpatialGUI.config.enchanting;
        if (screen instanceof BeaconScreen) return SpatialGUI.config.beacons;
        if (screen instanceof BrewingStandScreen) return SpatialGUI.config.brewing;
        if (screen instanceof MerchantScreen) return SpatialGUI.config.villagerTrading;
        if (screen instanceof ContainerScreen) return SpatialGUI.config.chests;
        if (screen instanceof ShulkerBoxScreen) return SpatialGUI.config.shulkerBoxes;
        if (screen instanceof HopperScreen) return SpatialGUI.config.hoppers;
        if (screen instanceof DispenserScreen) return SpatialGUI.config.dispensers;
        if (screen instanceof GrindstoneScreen) return SpatialGUI.config.grindstone;
        if (screen instanceof SmithingScreen) return SpatialGUI.config.smithing;
        if (screen instanceof CartographyTableScreen) return SpatialGUI.config.cartography;
        if (screen instanceof LoomScreen) return SpatialGUI.config.loom;
        if (screen instanceof StonecutterScreen) return SpatialGUI.config.stonecutter;
        if (screen instanceof LecternScreen) return SpatialGUI.config.lectern;
        if (screen instanceof InventoryScreen) return SpatialGUI.config.inventory;
        if (screen instanceof CreativeModeInventoryScreen) return SpatialGUI.config.creativeInventory;

        if (SpatialGUI.config.mostContainers && screen instanceof AbstractContainerScreen<?>) return true;
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