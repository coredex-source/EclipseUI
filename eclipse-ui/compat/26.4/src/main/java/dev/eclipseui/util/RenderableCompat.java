package dev.eclipseui.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;

public interface RenderableCompat extends Renderable {

    void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta);

    @Override
    default void extractRenderState(GuiGraphicsExtractor graphics) {
        extractRenderState(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());
    }
}
