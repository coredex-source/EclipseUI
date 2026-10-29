package dev.eclipseui.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ScreenCompat extends Screen {

    protected ScreenCompat(Component title) {
        super(title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        extractRenderState(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics);
    }
}
