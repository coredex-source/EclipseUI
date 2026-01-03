package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A simple label widget that displays text without any interactive control.
 * Used for section headers or informational text within a category.
 */
public class LabelWidget extends OptionWidget {
    
    private final Component text;
    private final LabelStyle style;
    
    public enum LabelStyle {
        /** Normal text, same as option labels */
        NORMAL,
        /** Header style with emphasis */
        HEADER,
        /** Subtle/muted text for descriptions */
        MUTED
    }
    
    public LabelWidget(Dim2i dim, ThemeData theme, Component text) {
        this(dim, theme, text, LabelStyle.NORMAL);
    }
    
    public LabelWidget(Dim2i dim, ThemeData theme, Component text, LabelStyle style) {
        super(dim, theme, Component.empty());
        this.text = text;
        this.style = style;
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        var font = Minecraft.getInstance().font;
        
        int textColor = getTextColor();
        int textY = dim.getCenterY() - (font.lineHeight / 2);
        int textX = dim.x() + 4;
        
        if (style == LabelStyle.HEADER) {
            // Draw with slight emphasis - bold effect via double render
            graphics.drawString(font, text, textX, textY, textColor, theme.useVanillaWidgets());
            if (!theme.useVanillaWidgets()) {
                // Add subtle underline for headers
                int textWidth = font.width(text);
                int underlineY = textY + font.lineHeight + 1;
                int underlineColor = (theme.accentPrimary() & 0x00FFFFFF) | 0x60000000;
                fillRect(graphics, textX, underlineY, textWidth, 1, underlineColor);
            }
        } else {
            graphics.drawString(font, text, textX, textY, textColor, theme.useVanillaWidgets());
        }
    }
    
    private int getTextColor() {
        if (theme.useVanillaWidgets()) {
            return switch (style) {
                case HEADER -> 0xFFFFFF55; // Yellow for headers
                case MUTED -> 0xFFAAAAAA;  // Gray for muted
                default -> 0xFFFFFFFF;      // White for normal
            };
        } else {
            return switch (style) {
                case HEADER -> theme.accentPrimary();
                case MUTED -> theme.textDisabled();
                default -> theme.textPrimary();
            };
        }
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // No control to render
    }
    
    @Override
    protected void renderLabel(GuiGraphics graphics) {
        // We override renderWidget entirely, so no label needed
    }
    
    @Override
    public void resetToDefault() {
        // Nothing to reset
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        // Labels are not interactive
        return false;
    }
    
    /**
     * Create a header-style label.
     */
    public static LabelWidget header(Dim2i dim, ThemeData theme, Component text) {
        return new LabelWidget(dim, theme, text, LabelStyle.HEADER);
    }
    
    /**
     * Create a muted/subtle label.
     */
    public static LabelWidget muted(Dim2i dim, ThemeData theme, Component text) {
        return new LabelWidget(dim, theme, text, LabelStyle.MUTED);
    }
}
