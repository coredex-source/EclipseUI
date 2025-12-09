package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A sidebar widget for category navigation.
 */
public class CategoryListWidget extends AbstractWidget {
    
    private final List<CategoryEntry> categories = new ArrayList<>();
    private int selectedIndex = 0;
    private Consumer<Integer> onCategorySelected;
    
    private static final int ITEM_HEIGHT = 24;
    private static final int ICON_SIZE = 16;
    private static final int PADDING = 4;
    
    public CategoryListWidget(Dim2i dim, ThemeData theme) {
        super(dim, theme);
    }
    
    public void addCategory(Component name, @Nullable ResourceLocation icon, @Nullable Component description) {
        categories.add(new CategoryEntry(name, icon, description));
    }
    
    public void clearCategories() {
        categories.clear();
        selectedIndex = 0;
    }
    
    public void setOnCategorySelected(Consumer<Integer> onCategorySelected) {
        this.onCategorySelected = onCategorySelected;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < categories.size()) {
            this.selectedIndex = index;
            if (onCategorySelected != null) {
                onCategorySelected.accept(index);
            }
        }
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Draw background
        if (theme.useVanillaWidgets()) {
            // Vanilla style - dark transparent background
            fillRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), 0xC0101010);
        } else {
            fillRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), theme.categoryBackground());
        }
        
        // Draw categories
        var font = Minecraft.getInstance().font;
        
        for (int i = 0; i < categories.size(); i++) {
            CategoryEntry category = categories.get(i);
            int itemY = dim.y() + i * ITEM_HEIGHT;
            
            boolean isHovered = mouseX >= dim.x() && mouseX < dim.getLimitX()
                && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;
            boolean isSelected = i == selectedIndex;
            
            if (theme.useVanillaWidgets()) {
                // Vanilla style - use button sprites for selected/hovered
                if (isSelected) {
                    net.minecraft.resources.ResourceLocation sprite = net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button");
                    graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, dim.x() + 2, itemY + 1, dim.width() - 4, ITEM_HEIGHT - 2);
                } else if (isHovered) {
                    net.minecraft.resources.ResourceLocation sprite = net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_highlighted");
                    graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, dim.x() + 2, itemY + 1, dim.width() - 4, ITEM_HEIGHT - 2);
                }
                
                // Text centered with shadow
                int textX = dim.x() + (dim.width() - font.width(category.name)) / 2;
                int textY = itemY + (ITEM_HEIGHT - font.lineHeight) / 2;
                int textColor = isSelected ? 0xFFFFFFFF : (isHovered ? 0xFFFFFFA0 : 0xFFE0E0E0);
                graphics.drawString(font, category.name, textX, textY, textColor, true);
            } else {
                // Modern flat style
                // Background
                if (isSelected) {
                    fillRect(graphics, dim.x(), itemY, dim.width(), ITEM_HEIGHT, theme.categorySelected());
                } else if (isHovered) {
                    fillRect(graphics, dim.x(), itemY, dim.width(), ITEM_HEIGHT, theme.categoryHover());
                }
                
                // Selection indicator
                if (isSelected) {
                    fillRect(graphics, dim.x(), itemY, 3, ITEM_HEIGHT, theme.accentPrimary());
                }
                
                // Icon
                int iconX = dim.x() + PADDING + 3;
                int iconY = itemY + (ITEM_HEIGHT - ICON_SIZE) / 2;
                
                if (category.icon != null) {
                    // TODO: Render icon texture
                    // For now, draw a placeholder square
                    fillRect(graphics, iconX, iconY, ICON_SIZE, ICON_SIZE, theme.accentPrimary());
                }
                
                // Text
                int textX = iconX + (category.icon != null ? ICON_SIZE + PADDING : 0);
                int textY = itemY + (ITEM_HEIGHT - font.lineHeight) / 2;
                int textColor = isSelected ? theme.textPrimary() : theme.textSecondary();
                graphics.drawString(font, category.name, textX, textY, textColor, false);
            }
        }
        
        // Draw divider on right edge
        if (!theme.useVanillaWidgets()) {
            fillRect(graphics, dim.getLimitX() - 1, dim.y(), 1, dim.height(), theme.divider());
        }
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        for (int i = 0; i < categories.size(); i++) {
            int itemY = dim.y() + i * ITEM_HEIGHT;
            
            if (mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT) {
                setSelectedIndex(i);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused) {
            if (keyCode == 265) { // Up
                setSelectedIndex(Math.max(0, selectedIndex - 1));
                return true;
            } else if (keyCode == 264) { // Down
                setSelectedIndex(Math.min(categories.size() - 1, selectedIndex + 1));
                return true;
            }
        }
        return false;
    }
    
    public record CategoryEntry(
        Component name,
        @Nullable ResourceLocation icon,
        @Nullable Component description
    ) {}
}
