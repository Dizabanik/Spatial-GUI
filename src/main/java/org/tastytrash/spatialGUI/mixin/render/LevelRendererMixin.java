package org.tastytrash.spatialGUI.mixin.render;

//? if >26.2 {
/*import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
*///? } else if >1.21.1 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
//?} else {
/*import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
*///?}
//? if >1.21.11 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
 *///?}
//? if >1.21.1 {
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
//?}
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    //? if >=26.2 {
    /*//? if >26.2 {
    /^@Inject(method = "render", at = @At("TAIL"))
    private void diegeticInventory$renderScreen(GraphicsResourceAllocator resourceAllocator, boolean renderOutline, CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci) {
    ^///?} else {
    @Inject(method = "render", at = @At("TAIL"))
    private void diegeticInventory$renderScreen(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
        //?}
        var renderer = SpatialGUIClient.renderer();
        if (!SpatialGUI.config.enabled || renderer == null || !renderer.shouldCapture()) {
            return;
        }
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(new Quaternionf()
                .rotateX((float) Math.toRadians(camera.xRot()))
                .rotateY((float) Math.toRadians(camera.yRot() + 180.0f))
                .get(new Matrix4f())
        );
        SpatialGUIClient.renderer().renderInWorld(poseStack);
    }
    *///? } else if >1.21.1 {
    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void diegeticInventory$renderScreen(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Matrix4f cullingProjectionMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (!SpatialGUI.config.enabled || renderer == null || !renderer.shouldCapture()) {
            return;
        }
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);
        SpatialGUIClient.renderer().renderInWorld(poseStack);
    }
    //? } else {
    /*// 1.21.1: renderLevel(DeltaTracker, boolean, Camera, GameRenderer, LightTexture, Matrix4f, Matrix4f)
    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void diegeticInventory$renderScreen(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        var renderer = SpatialGUIClient.renderer();
        if (!SpatialGUI.config.enabled || renderer == null || !renderer.shouldCapture()) {
            return;
        }
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);
        SpatialGUIClient.renderer().renderInWorld(poseStack);
    }
    *///? }
}