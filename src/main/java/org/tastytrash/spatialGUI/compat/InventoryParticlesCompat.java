package org.tastytrash.spatialGUI.compat;

import java.lang.reflect.Method;

public class InventoryParticlesCompat {
    private static boolean initialized = false;
    private static boolean available = false;
    private static Method getInstanceMethod;
    private static Method getCursorMethod;
    private static Method setMouseXMethod;
    private static Method setMouseYMethod;

    private static synchronized void init() {
        if (initialized) return;
        initialized = true;
        try {
            Class<?> rendererClass = Class.forName("net.lopymine.ip.renderer.InventoryParticlesRenderer");
            Class<?> cursorClass = Class.forName("net.lopymine.ip.element.mod.InventoryCursor");
            getInstanceMethod = rendererClass.getMethod("getInstance");
            getCursorMethod = rendererClass.getMethod("getCursor");
            setMouseXMethod = findMethod(cursorClass, "setMouseX");
            setMouseYMethod = findMethod(cursorClass, "setMouseY");
            available = getInstanceMethod != null && getCursorMethod != null
                     && setMouseXMethod != null && setMouseYMethod != null;
        } catch (Throwable t) {
            available = false;
        }
    }

    private static Method findMethod(Class<?> clazz, String name) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 1) {
                return m;
            }
        }
        return null;
    }

    private static void invokeSet(Method m, Object target, double val) throws Exception {
        Class<?> param = m.getParameterTypes()[0];
        if (param == int.class || param == Integer.class) {
            m.invoke(target, (int) Math.round(val));
        } else if (param == float.class || param == Float.class) {
            m.invoke(target, (float) val);
        } else {
            m.invoke(target, val);
        }
    }

    public static void updateCursor(double mouseX, double mouseY) {
        if (!initialized) {
            init();
        }
        if (!available) return;
        try {
            Object renderer = getInstanceMethod.invoke(null);
            if (renderer != null) {
                Object cursor = getCursorMethod.invoke(renderer);
                if (cursor != null) {
                    invokeSet(setMouseXMethod, cursor, mouseX);
                    invokeSet(setMouseYMethod, cursor, mouseY);
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
