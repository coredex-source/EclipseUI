package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for option widgets (config entries).
 * Displays a label on the left and the control on the right.
 */
public abstract class OptionWidget extends AbstractWidget {
    
    protected Component name;
    protected @Nullable Component description;
    protected boolean modified = false;
    protected boolean requiresRestart = false;
    
    // Layout constants
    protected static final int LABEL_WIDTH_RATIO = 50; // percentage
    protected static final int CONTROL_PADDING = 4;
    protected static final int ROW_HEIGHT = 20;
    
    public OptionWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme);
        this.name = name;
    }
    
    public void setDescription(@Nullable Component description) {
        this.description = description;
    }
    
    public @Nullable Component getDescription() {
        return this.description;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public void setRequiresRestart(boolean requiresRestart) {
        this.requiresRestart = requiresRestart;
    }
    
    public boolean requiresRestart() {
        return this.requiresRestart;
    }
    
    /**
     * Get the area for the label (left side).
     */
    protected Dim2i getLabelDim() {
        int labelWidth = (dim.width() * LABEL_WIDTH_RATIO) / 100;
        return new Dim2i(dim.x(), dim.y(), labelWidth, dim.height());
    }
    
    /**
     * Get the area for the control (right side).
     */
    protected Dim2i getControlDim() {
        int labelWidth = (dim.width() * LABEL_WIDTH_RATIO) / 100;
        int controlWidth = dim.width() - labelWidth - CONTROL_PADDING;
        return new Dim2i(dim.x() + labelWidth + CONTROL_PADDING, dim.y(), controlWidth, dim.height());
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Draw row background on hover
        if (this.hovered) {
            if (theme.useVanillaWidgets()) {
                fillRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), 0x40FFFFFF);
            } else {
                fillRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), 
                    this.theme.categoryHover());
            }
        }
        
        // Draw label
        renderLabel(graphics);
        
        // Draw the control (implemented by subclasses)
        renderControl(graphics, mouseX, mouseY, delta);
        
        // Draw modified indicator
        if (this.modified) {
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, "*", dim.x() + 2, dim.getCenterY() - 4, 
                theme.useVanillaWidgets() ? 0xFFFFFF00 : this.theme.accentPrimary(), theme.useVanillaWidgets());
        }
        
        // Draw restart warning icon
        if (this.requiresRestart) {
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, "⚠", dim.getLimitX() - 12, dim.getCenterY() - 4, 
                0xFFFFAA00, theme.useVanillaWidgets());
        }
    }
    
    /**
     * Render the label on the left side.
     */
    protected void renderLabel(GuiGraphics graphics) {
        var font = Minecraft.getInstance().font;
        Dim2i labelDim = getLabelDim();
        
        int textColor;
        if (theme.useVanillaWidgets()) {
            textColor = this.enabled ? 0xFFFFFFFF : 0xFF707070;
        } else {
            textColor = this.enabled ? this.theme.textPrimary() : this.theme.textDisabled();
        }
        int textY = labelDim.getCenterY() - (font.lineHeight / 2);
        
        // Add padding for modified indicator
        int textX = labelDim.x() + (this.modified ? 12 : 4);
        
        graphics.drawString(font, this.name, textX, textY, textColor, theme.useVanillaWidgets());
    }
    
    /**
     * Override to render the control widget.
     */
    protected abstract void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta);
    
    /**
     * Handle clicks in expanded areas (for dropdowns, color pickers, etc.)
     * These areas extend beyond the row bounds.
     * @return true if the click was handled
     */
    public boolean handleExpandedClick(double mouseX, double mouseY, int button) {
        return false;
    }
    
    /**
     * Check if this widget has an expanded overlay (dropdown menu, color picker, etc.)
     * Used to block clicks on other widgets when one is expanded.
     */
    public boolean isExpanded() {
        return false;
    }
    
    /**
     * Close any expanded overlay.
     */
    public void closeExpanded() {
        // Override in subclasses
    }
    
    /**
     * Reset the value to default.
     */
    public abstract void resetToDefault();
}
