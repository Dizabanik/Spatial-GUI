package org.tastytrash.spatialGUI.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;

//? if fabric && >1.21.1 {
import java.util.Map;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiRenderer.class)
public interface GuiRendererAccessor {
    @Accessor("pictureInPictureRenderers")
    Map<?, PictureInPictureRenderer<?>> spatialGUI$getPictureInPictureRenderers();
}
//?} else {
/*@Mixin(net.minecraft.client.Minecraft.class)
public interface GuiRendererAccessor {}
*///?}