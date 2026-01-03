package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A visual separator widget that draws a horizontal line.
 * Used to visually separate groups of options within a category.
 */
public class SeparatorWidget extends OptionWidget {
    
    private static final int SEPARATOR_HEIGHT = 1;
    private static final int VERTICAL_PADDING = 8;
    
    public SeparatorWidget(Dim2i dim, ThemeData theme) {
        super(dim, theme, Component.empty());
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Draw the separator line
        int lineY = dim.getCenterY();
        int lineStartX = dim.x() + 8;
        int lineEndX = dim.getLimitX() - 8;
        
        int lineColor;
        if (theme.useVanillaWidgets()) {
            lineColor = 0x80FFFFFF;
        } else {
            lineColor = (theme.textDisabled() & 0x00FFFFFF) | 0x60000000;
        }
        
        fillRect(graphics, lineStartX, lineY, lineEndX - lineStartX, SEPARATOR_HEIGHT, lineColor);
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // No control to render
    }
    
    @Override
    protected void renderLabel(GuiGraphics graphics) {
        // No label to render
    }
    
    @Override
    public void resetToDefault() {
        // Nothing to reset
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        // Separators are not interactive
        return false;
    }
}
