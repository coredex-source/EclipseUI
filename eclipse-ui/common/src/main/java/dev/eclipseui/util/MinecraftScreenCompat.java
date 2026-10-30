package dev.eclipseui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * Accessors for opening and reading the active Minecraft screen.
 */
public final class MinecraftScreenCompat {

    private MinecraftScreenCompat() {
    }

    public static void setScreen(Minecraft minecraft, @Nullable Screen screen) {
        minecraft.setScreen(screen);
    }

    public static @Nullable Screen getCurrentScreen(Minecraft minecraft) {
        return minecraft.screen;
    }
}
