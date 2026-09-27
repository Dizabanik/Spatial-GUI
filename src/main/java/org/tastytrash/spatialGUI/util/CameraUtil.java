package org.tastytrash.spatialGUI.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;

public class CameraUtil {
    public static void checkBlockCollision(Entity entity) {
        if (Minecraft.getInstance().level == null) return;

        float yawRadians = (float) Math.toRadians(entity.getYRot());
        double camX = entity.getX() + Math.sin(yawRadians) * SpatialGUI.config.cameraDistance + Math.cos(yawRadians) * SpatialGUI.config.cameraSideOffset;
        double camY = entity.getY() + SpatialGUI.config.cameraHeightOffset;
        double camZ = entity.getZ() - Math.cos(yawRadians) * SpatialGUI.config.cameraDistance + Math.sin(yawRadians) * SpatialGUI.config.cameraSideOffset;

        Vec3 playerEyePos = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 targetCamPos = new Vec3(camX, camY, camZ);
        BlockPos blockPos = BlockPos.containing(camX, camY, camZ);

        var blockState = Minecraft.getInstance().level.getBlockState(blockPos);
        var collisionShape = blockState.getCollisionShape(Minecraft.getInstance().level, blockPos);
        boolean isInsideBlock = !blockState.isAir() && !collisionShape.isEmpty() && collisionShape.bounds().move(blockPos).inflate(0.001).contains(targetCamPos);

        var clipContext = new ClipContext(playerEyePos, targetCamPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        boolean pathBlocked = Minecraft.getInstance().level.clip(clipContext).getType() != HitResult.Type.MISS;

        if (isInsideBlock || pathBlocked) {
            SpatialGUIClient.setSwitchedToFirstPersonDueToBlock(true);
        }
    }

    public static float calculateFovMultiplier() {
        if (!SpatialGUI.config.autoScaleByFov) return 1.0f;

        //? if >26.2 {
        float currentFov = Minecraft.getInstance().gameRenderer.mainCamera().getFov();
        //?} else {
        /*float currentFov = (float) (int) Minecraft.getInstance().options.fov().get();
        *///?}
        float baselineFov = (float) SpatialGUI.config.autoFovTuning.autoScaleBaselineFov;

        return currentFov / baselineFov;
    }

    public static float calculateAutoFovDistance(float baseDistance, boolean isFirstPerson) {
        if (isFirstPerson) return 0.0f;
        
        float fovMultiplier = calculateFovMultiplier();
        float distanceMultiplier = (float) SpatialGUI.config.autoFovTuning.autoScaleDistanceMultiplier;
        float fovAdjustment = (1.0f / fovMultiplier) - 1.0f;
        float distanceFovAdjustment = fovAdjustment * distanceMultiplier;
        
        return Math.clamp(baseDistance * (1.0f + distanceFovAdjustment), -4, 4);
    }

    public static float calculateAutoFovSideOffset(float baseSideOffset, boolean isFirstPerson) {
        if (isFirstPerson) return 0.0f;
        
        float fovMultiplier = calculateFovMultiplier();
        float sideOffsetMultiplier = (float) SpatialGUI.config.autoFovTuning.autoScaleSideOffsetMultiplier;
        float fovAdjustment = (1.0f / fovMultiplier) - 1.0f;
        float sideOffsetFovAdjustment = fovAdjustment * sideOffsetMultiplier;
        
        return Math.clamp(baseSideOffset * (1.0f + sideOffsetFovAdjustment), -4, 4);
    }

    public static double calculateMouseSensitivity() {
        double ss = Minecraft.getInstance().options.sensitivity().get() * 0.6 + 0.2;
        return ss * ss * ss;
    }
}
