package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Base widget class for EclipseUI, inspired by Sodium's widget architecture.
 * Uses Dim2i for positioning instead of separate x/y/width/height fields.
 */
public abstract class AbstractWidget implements Renderable, GuiEventListener, NarratableEntry {
    
    protected Dim2i dim;
    protected ThemeData theme;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    
    public AbstractWidget(Dim2i dim, ThemeData theme) {
        this.dim = dim;
        this.theme = theme;
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        
        this.hovered = this.dim.containsCursor(mouseX, mouseY);
        this.renderWidget(graphics, mouseX, mouseY, delta);
    }
    
    /**
     * Render overlay elements that should appear on top of all other widgets.
     * Called after all widgets have been rendered.
     */
    public void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Override in subclasses that need overlay rendering (dropdowns, popups, etc.)
    }
    
    /**
     * Override this to render the widget content.
     */
    protected abstract void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta);
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.visible || !this.enabled) {
            return false;
        }
        
        if (this.dim.containsCursor(event.x(), event.y())) {
            return this.onMouseClicked(event.x(), event.y(), event.button());
        }
        
        return false;
    }
    
    /**
     * Override this to handle mouse clicks within the widget bounds.
     */
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return this.onMouseReleased(event.x(), event.y(), event.button());
    }
    
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return this.onMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY);
    }
    
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        return this.onKeyPressed(event.key(), event.scancode(), event.modifiers());
    }
    
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        return this.onKeyReleased(event.key(), event.scancode(), event.modifiers());
    }
    
    protected boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        return this.onCharTyped((char) event.codepoint(), event.modifiers());
    }
    
    protected boolean onCharTyped(char chr, int modifiers) {
        return false;
    }
    
    // Public helper methods for forwarding events with primitive parameters
    public boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible || !this.enabled) {
            return false;
        }
        if (this.dim.containsCursor(mouseX, mouseY)) {
            return this.onMouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
    
    public boolean handleMouseReleased(double mouseX, double mouseY, int button) {
        return this.onMouseReleased(mouseX, mouseY, button);
    }
    
    public boolean handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    public boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.onKeyPressed(keyCode, scanCode, modifiers);
    }
    
    public boolean handleCharTyped(char chr, int modifiers) {
        return this.onCharTyped(chr, modifiers);
    }
    
    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    @Override
    public boolean isFocused() {
        return this.focused;
    }
    
    @Override
    public NarrationPriority narrationPriority() {
        if (this.focused) {
            return NarrationPriority.FOCUSED;
        }
        return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        // Subclasses should override for accessibility
    }
    
    // Getters and setters
    
    public Dim2i getDim() {
        return this.dim;
    }
    
    public void setDim(Dim2i dim) {
        this.dim = dim;
    }
    
    public int getX() {
        return this.dim.x();
    }
    
    public int getY() {
        return this.dim.y();
    }
    
    public int getWidth() {
        return this.dim.width();
    }
    
    public int getHeight() {
        return this.dim.height();
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isHovered() {
        return this.hovered;
    }
    
    public ThemeData getTheme() {
        return this.theme;
    }
    
    public void setTheme(ThemeData theme) {
        this.theme = theme;
    }
    
    // Utility rendering methods
    
    /**
     * Fill a rectangle with the given color.
     */
    protected void fillRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }
    
    /**
     * Draw a rectangle outline.
     */
    protected void drawRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        // Top
        graphics.fill(x, y, x + width, y + 1, color);
        // Bottom
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        // Left
        graphics.fill(x, y, x + 1, y + height, color);
        // Right
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }
    
    /**
     * Fill a rounded rectangle (approximated with a regular rectangle for now).
     */
    protected void fillRoundedRect(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
        // For now, just fill a regular rectangle
        // TODO: Implement actual rounded corners if needed
        fillRect(graphics, x, y, width, height, color);
    }
}
