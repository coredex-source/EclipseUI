package dev.eclipseui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Cross-version accessors for opening and reading the active Minecraft screen.
 */
public final class MinecraftScreenCompat {

    private static final @Nullable Method SET_SCREEN_AND_SHOW = findMethod("setScreenAndShow");
    private static final @Nullable Method SET_SCREEN = findMethod("setScreen");
    private static final @Nullable Method GET_SCREEN = findNoArgMethod("getScreen", "screen", "currentScreen");
    private static final @Nullable Field SCREEN_FIELD = findField("screen");

    private MinecraftScreenCompat() {
    }

    public static void setScreen(Minecraft minecraft, @Nullable Screen screen) {
        try {
            if (SET_SCREEN_AND_SHOW != null) {
                SET_SCREEN_AND_SHOW.invoke(minecraft, screen);
                return;
            }

            if (SET_SCREEN != null) {
                SET_SCREEN.invoke(minecraft, screen);
                return;
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to set Minecraft screen", e);
        }

        throw new IllegalStateException("No compatible Minecraft screen setter found");
    }

    public static @Nullable Screen getCurrentScreen(Minecraft minecraft) {
        try {
            if (GET_SCREEN != null) {
                Object value = GET_SCREEN.invoke(minecraft);
                return value instanceof Screen screen ? screen : null;
            }

            if (SCREEN_FIELD != null) {
                Object value = SCREEN_FIELD.get(minecraft);
                return value instanceof Screen screen ? screen : null;
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to get current Minecraft screen", e);
        }

        return null;
    }

    private static @Nullable Method findMethod(String name) {
        try {
            return Minecraft.class.getMethod(name, Screen.class);
        } catch (ReflectiveOperationException ignored) {
            try {
                Method method = Minecraft.class.getDeclaredMethod(name, Screen.class);
                method.setAccessible(true);
                return method;
            } catch (ReflectiveOperationException ignoredDeclared) {
                return null;
            }
        }
    }

    private static @Nullable Method findNoArgMethod(String... names) {
        for (String name : names) {
            try {
                return Minecraft.class.getMethod(name);
            } catch (ReflectiveOperationException ignored) {
                try {
                    Method method = Minecraft.class.getDeclaredMethod(name);
                    method.setAccessible(true);
                    return method;
                } catch (ReflectiveOperationException ignoredDeclared) {
                    // try next
                }
            }
        }
        return null;
    }

    private static @Nullable Field findField(String name) {
        try {
            return Minecraft.class.getField(name);
        } catch (ReflectiveOperationException ignored) {
            try {
                Field field = Minecraft.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (ReflectiveOperationException ignoredDeclared) {
                return null;
            }
        }
    }
}