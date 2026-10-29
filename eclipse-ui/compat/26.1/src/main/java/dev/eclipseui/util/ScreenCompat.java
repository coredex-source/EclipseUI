package dev.eclipseui.util;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ScreenCompat extends Screen {

    protected ScreenCompat(Component title) {
        super(title);
    }
}
