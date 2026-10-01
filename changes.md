# Changes Summary (Since Last Pull from Upstream `origin/main` at `9aed496`)

This document summarizes all modifications made to the codebase compared to the last pull from `origin/main` (commit `9aed496`: *"Add shouldHookScreen(), add PauseScreenMixin"*).

---

### 1. Fix Crosshair and Hands Rendering Behind 3D Screen in First-Person Mode

#### Problem
* **Crosshair Rendering Behind:** Deferring the 3D screen quad draw to `@At("TAIL")` of `GameRenderer.render()` caused it to execute after `GuiRenderer.render()` (which draws the HUD and crosshair), painting the 3D quad over the crosshair.
* **Hands Rendering Behind (Vanilla & Vitrail Shaders):**
  * Vitrail hooks and diverts hand rendering inside `GameRenderer.renderItemInHand` / `render3dHud` whenever shaders are enabled (`HandDraw.diverted()`), which canceled hands from being submitted outside the level pass.
  * In vanilla, hand submission is skipped when `isHudHidden` is true during level rendering.
  * Attempting to render the screen in-world resulted in hands either being hidden or drawn behind the screen quad due to level rendering order.

#### Solution
* **[GameRendererMixin.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/render/GameRendererMixin.java):**
  * **Render Hands In Front (`spatialGUI$renderHandsInFront`):**
    * Directly invokes `firstPersonHandsAndItemsRenderer.submitHandsWithItems(...)` into `handAndScreenSubmitNodeStorage`.
    * Sets up the first-person perspective projection via `hud3dProjectionMatrixBuffer`.
    * Clears `mainRenderTarget` depth texture to `0.0` (Minecraft 26.x Reverse-Z).
    * Dispatches the render pass via `featureRenderDispatcher.renderAllFeatures(renderPass, frame)` directly onto `mainRenderTarget.getColorTextureView()`.
    * This completely bypasses Vitrail's diversion wrapper and draws the player's hands on top of the 3D screen quad with correct depth testing.
  * **Suppress In-World Hand Submission:**
    * In `spatialGUI$overrideHideHand`, cancels in-world hand rendering during `renderLevel()` (`renderItemInHand` on `<26.3`, `render3dHud` on `>=26.3`) when first-person Spatial GUI is active to prevent hands from being drawn twice or behind the quad.
  * **Crosshair on Top:**
    * The 3D screen quad is drawn in `beforeGuiRender`.
    * Vanilla `GuiRenderer.render()` runs immediately after `beforeGuiRender`, rendering the HUD crosshair cleanly in front of both the 3D screen quad and hands.
    * Removed obsolete `@At("TAIL")` draw calls that previously covered the crosshair.

---

### 2. Fix Sky-Color / Atmospheric Fog on Inventory Items & Blocks

#### Problem
* In Minecraft 26.2 and 26.3, `GuiRenderer` draws slot items and 3D blocks into `GuiItemAtlas` via `drawToSlot()`.
* `drawToSlot()` calls `RenderSystem.bindDefaultUniforms(renderPass)`, which attaches the shader fog buffer from `RenderSystem.getShaderFog()` to the `"Fog"` uniform.
* `LevelRenderer.render()` leaves the global shader fog buffer set to `FogMode.WORLD` (colored with the current sky/atmosphere color).
* Consequently, slot and block shaders in the GUI evaluated `fogFactor = 1.0` and blended 100% with `FogColor`, rendering all items and blocks in inventories and chests as solid sky-blue silhouettes.

#### Solution
* **[GameRendererMixin.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/render/GameRendererMixin.java):**
  * Added `RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE))` immediately before calling `getScreenGuiRenderer().render()`.
  * This guarantees that GUI and inventory item shaders execute with fog disabled, restoring normal item textures, colors, and lighting.

---

### 3. Dynamic High-DPI & Retina Display GUI Scaling (Relative Scaling with 1–6 Slider)

#### Problem
* `SpatialGUIConfig.calculateAutoGuiScale()` previously used hardcoded height tiers capped at `4` for any display height > 800px.
* On MacBook Retina displays (~1600p–2234p) and 4K monitors (2160p), a 4x scale produced a tiny GUI that was difficult to interact with.
* Furthermore, setting the manual `guiScale` slider to `4` used absolute pixel scaling, which meant scale 4 on Retina was physically half the size of scale 4 on a Full HD screen.

#### Solution
* **[SpatialGUIConfig.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/client/SpatialGUIConfig.java):**
  * **Dynamic Auto-Scale Calculation:** Replaced the hardcoded height brackets in `calculateAutoGuiScale(windowWidth, windowHeight)` with standard Minecraft step scaling:
    ```java
    int scale = 1;
    while (windowWidth / (scale + 1) >= 320 && windowHeight / (scale + 1) >= 240) {
        scale++;
    }
    return Math.max(1, scale);
    ```
    * 1080p (Full HD): yields **4x** (maintains existing scale).
    * ~1964p (MacBook Pro Retina): yields **8x** (matches native physical UI size at 2x pixel density).
    * 2160p (4K): yields **9x**.
  * **Relative Manual Scaling (1–6 Slider):**
    * Kept the manual slider bounded at `min = 1, max = 6` with default `4`.
    * Implemented **relative scaling** in `getEffectiveGuiScale()`:
      ```java
      int clampedGuiScale = Math.max(1, Math.min(guiScale, 6));
      int autoScale = calculateAutoGuiScale(windowWidth, windowHeight);
      int relativeScale = Math.round(clampedGuiScale * (autoScale / 4.0f));
      return Math.max(1, relativeScale);
      ```
    * On **Full HD** (`autoScale = 4`), slider values `1..6` map 1:1 to scales `1..6` (scale 4 = 100%).
    * On **Retina** (`autoScale = 8`), slider values `1..6` scale proportionally to `2, 4, 6, 8, 10, 12`. Setting scale `4` on Retina produces scale `8`, giving the **exact same physical size and feel as scale 4 on Full HD**.
  * Added overloaded `getEffectiveGuiScale(int windowWidth, int windowHeight)`.
* **[WindowMixin.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/gui/WindowMixin.java):**
  * Passed window width and height to `getEffectiveGuiScale(self.getWidth(), self.getHeight())`.
* **[MouseHandlerMixin.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/gui/MouseHandlerMixin.java):**
  * Passed window width and height to `getEffectiveGuiScale(mc.getWindow().getWidth(), mc.getWindow().getHeight())`.
* **[ScreenExtractor.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/render/ScreenExtractor.java):**
  * Passed window width and height to `getEffectiveGuiScale(mc.getWindow().getWidth(), mc.getWindow().getHeight())`.

---

### 4. Fix UI Not Appearing on Minecraft 1.21.11

#### Problem
* In Minecraft 1.21.11 (`>1.21.1` Stonecutter branch), the UI was completely invisible when opening inventories or containers.
* **Root Cause:**
  * In 1.21.11, screen rendering is separated into extraction (`Screen.renderWithTooltipAndSubtitles` / `extractIsolatedScreen`) and rasterization (`ScreenGuiRenderer.render(...)`).
  * `renderer.renderInWorldPost()` had been inadvertently added to `spatialGUI$overrideHideHand` (`renderItemInHand`) and `spatialGUI$beforeGuiRender`.
  * During level rendering, `renderItemInHand` executed first and called `renderInWorldPost()`. At this stage, the screen had not yet been extracted or drawn into the framebuffer target, and `hasDrawnThisFrame` was set to `true`.
  * When `@At("TAIL")` ran and `ScreenGuiRenderer.render(...)` finally drew the UI into the target buffer, the subsequent `renderInWorldPost()` call returned immediately because `hasDrawnThisFrame` was already `true`.
  * In the next frame, `clearTarget()` wiped the texture buffer clean. Consequently, the UI was never projected into the world, rendering it 100% invisible.

#### Solution
* **[GameRendererMixin.java](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/render/GameRendererMixin.java):**
  * Removed premature `renderer.renderInWorldPost()` calls from `spatialGUI$beforeGuiRender` and `spatialGUI$overrideHideHand` in the `>1.21.1` block.
  * Restored `renderer.getScreenGuiRenderer().render(...)`, `incrementFrameNumber()`, and `renderer.renderInWorldPost()` at `@At("TAIL")` of `GameRenderer.render()`.
  * Restored `renderer.renderInWorldPost()` inside `spatialGUI$redirectScreenExtraction` for `<=1.21.1`.
  * Kept the 26.x-specific in-front hand redraw pipeline strictly isolated within the `>=26.1.2` Stonecutter block.

---

### 5. Modified Files Summary
* [`src/main/java/org/tastytrash/spatialGUI/mixin/render/GameRendererMixin.java`](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/render/GameRendererMixin.java): Fixed hands and crosshair layering in 26.3, bypassed Vitrail suppression, resolved sky-fogged inventory items, and restored the isolated screen render pipeline in 1.21.11 and 1.20.1/1.21.1.
* [`src/main/java/org/tastytrash/spatialGUI/client/SpatialGUIConfig.java`](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/client/SpatialGUIConfig.java): Relative GUI scaling with 1–6 slider (scale 4 on Retina feels identical to scale 4 on Full HD) and dynamic auto-scaling.
* [`src/main/java/org/tastytrash/spatialGUI/mixin/gui/WindowMixin.java`](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/gui/WindowMixin.java): Updated effective GUI scale query.
* [`src/main/java/org/tastytrash/spatialGUI/mixin/gui/MouseHandlerMixin.java`](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/mixin/gui/MouseHandlerMixin.java): Updated effective GUI scale query.
* [`src/main/java/org/tastytrash/spatialGUI/render/ScreenExtractor.java`](file:///Users/diz/projects/min_mods/src/main/java/org/tastytrash/spatialGUI/render/ScreenExtractor.java): Updated effective GUI scale query.
* [`changes.md`](file:///Users/diz/projects/min_mods/changes.md): Complete summary of changes since last pull.
