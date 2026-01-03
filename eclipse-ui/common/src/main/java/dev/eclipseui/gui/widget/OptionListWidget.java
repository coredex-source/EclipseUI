package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A scrollable list of option widgets.
 */
public class OptionListWidget extends AbstractWidget {
    
    private final List<OptionWidget> options = new ArrayList<>();
    private double scrollOffset = 0;
    private double targetScrollOffset = 0;
    private boolean scrolling = false;
    private int scrollbarDragOffset = 0;
    
    private static final int ITEM_HEIGHT = 24;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int ITEM_PADDING = 2;
    private static final float SCROLL_SPEED = 0.3f;
    private static final double SCROLL_THRESHOLD = 0.5;
    
    public OptionListWidget(Dim2i dim, ThemeData theme) {
        super(dim, theme);
    }
    
    public void addOption(OptionWidget option) {
        // Reposition the option within the list
        int y = options.size() * ITEM_HEIGHT;
        int width = dim.width() - SCROLLBAR_WIDTH - 4;
        option.setDim(new Dim2i(dim.x() + 2, dim.y() + y, width, ITEM_HEIGHT - ITEM_PADDING));
        options.add(option);
    }
    
    public void clearOptions() {
        options.clear();
        scrollOffset = 0;
        targetScrollOffset = 0;
    }
    
    public List<OptionWidget> getOptions() {
        return options;
    }
    
    private int getContentHeight() {
        return options.size() * ITEM_HEIGHT;
    }
    
    private int getMaxScroll() {
        return Math.max(0, getContentHeight() - dim.height());
    }
    
    private boolean hasScrollbar() {
        return getContentHeight() > dim.height();
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Smooth scrolling
        if (scrollOffset != targetScrollOffset) {
            scrollOffset = Mth.lerp(SCROLL_SPEED, scrollOffset, targetScrollOffset);
            if (Math.abs(scrollOffset - targetScrollOffset) < SCROLL_THRESHOLD) {
                scrollOffset = targetScrollOffset;
            }
        }
        
        // Enable scissoring for clipping
        graphics.enableScissor(dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY());
        
        // Render visible options
        int scrollOffsetInt = (int) scrollOffset;
        for (int i = 0; i < options.size(); i++) {
            OptionWidget option = options.get(i);
            
            // Calculate actual Y position with scroll offset
            int itemY = dim.y() + (i * ITEM_HEIGHT) - scrollOffsetInt;
            
            // Skip if not visible
            if (itemY + ITEM_HEIGHT < dim.y() || itemY > dim.getLimitY()) {
                continue;
            }
            
            // Update option position for rendering
            Dim2i optionDim = option.getDim();
            option.setDim(new Dim2i(optionDim.x(), itemY, optionDim.width(), optionDim.height()));
            
            option.render(graphics, mouseX, mouseY, delta);
        }
        
        graphics.disableScissor();
        
        // Render scrollbar
        if (hasScrollbar()) {
            renderScrollbar(graphics, mouseX, mouseY);
        }
        
        // Render overlays (dropdowns, popups) on top of everything
        for (OptionWidget option : options) {
            option.renderOverlay(graphics, mouseX, mouseY, delta);
        }
    }
    
    private void renderScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
        int scrollbarX = dim.getLimitX() - SCROLLBAR_WIDTH - 1;
        int scrollbarHeight = dim.height();
        
        // Track
        if (theme.useVanillaWidgets()) {
            fillRect(graphics, scrollbarX, dim.y(), SCROLLBAR_WIDTH, scrollbarHeight, 0xFF000000);
        } else {
            fillRect(graphics, scrollbarX, dim.y(), SCROLLBAR_WIDTH, scrollbarHeight, theme.scrollbarTrack());
        }
        
        // Thumb
        int contentHeight = getContentHeight();
        int viewHeight = dim.height();
        float thumbRatio = (float) viewHeight / contentHeight;
        int thumbHeight = Math.max(20, (int) (viewHeight * thumbRatio));
        
        int maxScroll = getMaxScroll();
        float scrollProgress = maxScroll > 0 ? (float) scrollOffset / maxScroll : 0;
        int thumbY = dim.y() + (int) ((scrollbarHeight - thumbHeight) * scrollProgress);
        
        boolean thumbHovered = mouseX >= scrollbarX && mouseX < scrollbarX + SCROLLBAR_WIDTH
            && mouseY >= thumbY && mouseY < thumbY + thumbHeight;
        
        if (theme.useVanillaWidgets()) {
            // Vanilla style scrollbar (gray with lighter highlight)
            int thumbColor = (scrolling || thumbHovered) ? 0xFFC0C0C0 : 0xFF808080;
            fillRect(graphics, scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight, thumbColor);
        } else {
            int thumbColor = (scrolling || thumbHovered) ? theme.scrollbarThumbHover() : theme.scrollbarThumb();
            fillRect(graphics, scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight, thumbColor);
        }
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        // First, check if any expanded widget (dropdown/colorpicker) should handle the click
        // These can extend beyond their row bounds
        int scrollOffsetInt = (int) scrollOffset;
        OptionWidget expandedWidget = null;
        
        // Find any expanded widget first
        for (OptionWidget option : options) {
            if (option.isExpanded()) {
                expandedWidget = option;
                break;
            }
        }
        
        // If there's an expanded widget, it gets priority
        if (expandedWidget != null) {
            // Update its position for click handling
            int expandedIndex = options.indexOf(expandedWidget);
            int itemY = dim.y() + (expandedIndex * ITEM_HEIGHT) - scrollOffsetInt;
            Dim2i optionDim = expandedWidget.getDim();
            expandedWidget.setDim(new Dim2i(optionDim.x(), itemY, optionDim.width(), optionDim.height()));
            
            // Let expanded widget handle the click
            if (expandedWidget.handleExpandedClick(mouseX, mouseY, button)) {
                return true;
            }
            
            // Click was outside the expanded area - close it and consume the click
            expandedWidget.closeExpanded();
            return true;
        }
        
        if (!dim.containsCursor(mouseX, mouseY)) {
            return false;
        }
        
        // Check scrollbar click
        if (hasScrollbar()) {
            int scrollbarX = dim.getLimitX() - SCROLLBAR_WIDTH - 1;
            if (mouseX >= scrollbarX && mouseX < scrollbarX + SCROLLBAR_WIDTH) {
                scrolling = true;
                
                // Calculate thumb position
                int contentHeight = getContentHeight();
                int viewHeight = dim.height();
                float thumbRatio = (float) viewHeight / contentHeight;
                int thumbHeight = Math.max(20, (int) (viewHeight * thumbRatio));
                int maxScroll = getMaxScroll();
                float scrollProgress = maxScroll > 0 ? (float) scrollOffset / maxScroll : 0;
                int thumbY = dim.y() + (int) ((dim.height() - thumbHeight) * scrollProgress);
                
                scrollbarDragOffset = (int) mouseY - thumbY;
                return true;
            }
        }
        
        // Forward to all visible options and let them check if click is within their bounds
        for (int i = 0; i < options.size(); i++) {
            OptionWidget option = options.get(i);
            int itemY = dim.y() + (i * ITEM_HEIGHT) - scrollOffsetInt;
            
            // Skip if not visible
            if (itemY + ITEM_HEIGHT < dim.y() || itemY > dim.getLimitY()) {
                continue;
            }
            
            // Update position for click handling
            Dim2i optionDim = option.getDim();
            option.setDim(new Dim2i(optionDim.x(), itemY, optionDim.width(), optionDim.height()));
            
            if (option.handleMouseClicked(mouseX, mouseY, button)) {
                // Unfocus all other options when one is clicked
                for (OptionWidget other : options) {
                    if (other != option) {
                        other.setFocused(false);
                    }
                }
                return true;
            }
        }
        
        // Click wasn't on any option, unfocus all
        for (OptionWidget option : options) {
            option.setFocused(false);
        }
        
        return false;
    }
    
    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        
        for (OptionWidget option : options) {
            option.handleMouseReleased(mouseX, mouseY, button);
        }
        
        return false;
    }
    
    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrolling) {
            int contentHeight = getContentHeight();
            int viewHeight = dim.height();
            float thumbRatio = (float) viewHeight / contentHeight;
            int thumbHeight = Math.max(20, (int) (viewHeight * thumbRatio));
            int trackHeight = dim.height() - thumbHeight;
            
            int thumbY = (int) mouseY - scrollbarDragOffset - dim.y();
            float scrollProgress = trackHeight > 0 ? (float) thumbY / trackHeight : 0;
            scrollProgress = Mth.clamp(scrollProgress, 0, 1);
            
            targetScrollOffset = scrollProgress * getMaxScroll();
            scrollOffset = targetScrollOffset;
            return true;
        }
        
        for (OptionWidget option : options) {
            if (option.handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (dim.containsCursor(mouseX, mouseY) && hasScrollbar()) {
            targetScrollOffset -= verticalAmount * 20;
            targetScrollOffset = Mth.clamp(targetScrollOffset, 0, getMaxScroll());
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (OptionWidget option : options) {
            if (option.isFocused() && option.handleKeyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean onCharTyped(char chr, int modifiers) {
        for (OptionWidget option : options) {
            if (option.isFocused() && option.handleCharTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    public void resetAllToDefaults() {
        for (OptionWidget option : options) {
            option.resetToDefault();
        }
    }
    
    public boolean hasModifiedOptions() {
        for (OptionWidget option : options) {
            if (option.isModified()) {
                return true;
            }
        }
        return false;
    }
}
