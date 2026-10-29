package dev.eclipseui.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.resources.Identifier;

public final class GuiGraphicsCompat {

    private GuiGraphicsCompat() {
    }

    public static void blitSprite(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int width, int height) {
        graphics.blitSprite(GuiPipeline.TEXTURED, sprite, x, y, width, height);
    }
}
