package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A dropdown widget for enum/cycling options.
 */
public class DropdownWidget<E extends Enum<E>> extends OptionWidget {
    
    private Class<E> enumClass;
    private Supplier<E> getter;
    private Consumer<E> setter;
    private E defaultValue;
    private boolean liveUpdate;
    private Consumer<E> onChange;
    private Function<E, Component> formatter;
    
    private boolean expanded = false;
    private int hoveredIndex = -1;
    
    public DropdownWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public DropdownWidget<E> enumClass(Class<E> enumClass) {
        this.enumClass = enumClass;
        return this;
    }
    
    public DropdownWidget<E> binding(Supplier<E> getter, Consumer<E> setter) {
        this.getter = getter;
        this.setter = setter;
        return this;
    }
    
    public DropdownWidget<E> defaultValue(E defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DropdownWidget<E> liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }
    
    public DropdownWidget<E> onChange(Consumer<E> onChange) {
        this.onChange = onChange;
        return this;
    }
    
    public DropdownWidget<E> formatter(Function<E, Component> formatter) {
        this.formatter = formatter;
        return this;
    }
    
    public E getValue() {
        return this.getter != null ? this.getter.get() : null;
    }
    
    public void setValue(E value) {
        if (this.setter != null && value != null) {
            E oldValue = getValue();
            this.setter.accept(value);
            
            if (oldValue != value) {
                this.modified = true;
                
                if (this.onChange != null) {
                    this.onChange.accept(value);
                }
            }
        }
    }
    
    private void cycleValue(boolean forward) {
        if (enumClass == null) return;
        
        E[] values = enumClass.getEnumConstants();
        E current = getValue();
        if (current == null && values.length > 0) {
            setValue(values[0]);
            return;
        }
        
        int currentIndex = current.ordinal();
        int nextIndex;
        if (forward) {
            nextIndex = (currentIndex + 1) % values.length;
        } else {
            nextIndex = (currentIndex - 1 + values.length) % values.length;
        }
        setValue(values[nextIndex]);
    }
    
    private Component getDisplayText(E value) {
        if (value == null) {
            return Component.literal("---");
        }
        if (this.formatter != null) {
            return this.formatter.apply(value);
        }
        // Default: capitalize enum name
        String name = value.name().replace('_', ' ');
        return Component.literal(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        
        if (theme.useVanillaWidgets()) {
            // Vanilla button style dropdown
            int buttonHeight = 20;
            int buttonX = controlDim.x();
            int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
            int buttonWidth = controlDim.width() - 4;
            
            // Draw vanilla button using sprites
            net.minecraft.resources.ResourceLocation sprite = this.enabled 
                ? (this.hovered ? net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_highlighted") 
                    : net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button"))
                : net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_disabled");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, buttonX, buttonY, buttonWidth, buttonHeight);
            
            // Draw current value centered with shadow
            Component displayText = getDisplayText(getValue());
            int textWidth = font.width(displayText);
            int textX = buttonX + (buttonWidth - textWidth) / 2;
            int textY = buttonY + (buttonHeight - font.lineHeight) / 2;
            int textColor = this.enabled ? 0xFFFFFFFF : 0xFF707070;
            graphics.drawString(font, displayText, textX, textY, textColor, true);
        } else {
            // Modern flat style dropdown
            int buttonHeight = 16;
            int buttonX = controlDim.x();
            int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
            int buttonWidth = controlDim.width() - 4;
            
            // Draw button background
            int bgColor = this.hovered ? theme.buttonBackgroundHover() : theme.buttonBackground();
            fillRect(graphics, buttonX, buttonY, buttonWidth, buttonHeight, bgColor);
            
            // Draw border
            int borderColor = this.focused ? theme.accentPrimary() : theme.buttonBorder();
            drawRect(graphics, buttonX, buttonY, buttonWidth, buttonHeight, borderColor);
            
            // Draw current value
            Component displayText = getDisplayText(getValue());
            int textColor = this.enabled ? theme.textPrimary() : theme.textDisabled();
            int textY = buttonY + (buttonHeight - font.lineHeight) / 2;
            graphics.drawString(font, displayText, buttonX + 6, textY, textColor, false);
            
            // Draw dropdown arrow
            String arrow = this.expanded ? "▲" : "▼";
            int arrowX = buttonX + buttonWidth - font.width(arrow) - 6;
            graphics.drawString(font, arrow, arrowX, textY, theme.textSecondary(), false);
        }
        
        // Dropdown items are rendered in renderOverlay to appear on top of other widgets
    }
    
    /**
     * Determines if the dropdown should render upward (flip) to avoid clipping.
     */
    private boolean shouldFlipUpward() {
        if (enumClass == null) return false;
        
        Dim2i controlDim = getControlDim();
        int buttonHeight = theme.useVanillaWidgets() ? 20 : 16;
        int itemHeight = theme.useVanillaWidgets() ? 20 : 14;
        int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
        
        E[] values = enumClass.getEnumConstants();
        int dropdownHeight = values.length * itemHeight;
        int dropdownY = buttonY + buttonHeight;
        
        // Get screen height
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        
        // Check if dropdown would extend beyond the screen bottom
        // Leave some margin (10 pixels)
        return (dropdownY + dropdownHeight) > (screenHeight - 10);
    }
    
    @Override
    public void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.expanded || this.enumClass == null) return;
        
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        
        E[] values = enumClass.getEnumConstants();
        boolean flipUpward = shouldFlipUpward();
        
        if (theme.useVanillaWidgets()) {
            // Vanilla style dropdown overlay
            int buttonHeight = 20;
            int buttonX = controlDim.x();
            int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
            int buttonWidth = controlDim.width() - 4;
            
            int itemHeight = 20;
            int dropdownHeight = values.length * itemHeight;
            int dropdownY = flipUpward ? (buttonY - dropdownHeight) : (buttonY + buttonHeight);
            
            // Draw background using solid color (dark gray like vanilla)
            fillRect(graphics, buttonX, dropdownY, buttonWidth, dropdownHeight, 0xFF000000);
            
            // Items
            E currentValue = getValue();
            for (int i = 0; i < values.length; i++) {
                E value = values[i];
                int itemY = dropdownY + i * itemHeight;
                
                // Highlight hovered or selected
                boolean isHovered = mouseX >= buttonX && mouseX < buttonX + buttonWidth
                    && mouseY >= itemY && mouseY < itemY + itemHeight;
                boolean isSelected = value == currentValue;
                
                if (isHovered) {
                    this.hoveredIndex = i;
                    // Use vanilla button highlighted sprite for hovered items
                    net.minecraft.resources.ResourceLocation sprite = net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_highlighted");
                    graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, buttonX, itemY, buttonWidth, itemHeight);
                } else if (isSelected) {
                    // Use regular button for selected
                    net.minecraft.resources.ResourceLocation sprite = net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button");
                    graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, buttonX, itemY, buttonWidth, itemHeight);
                } else {
                    // Draw dark background for unselected items
                    fillRect(graphics, buttonX, itemY, buttonWidth, itemHeight, 0xFF2B2B2B);
                }
                
                // Draw border
                drawRect(graphics, buttonX, itemY, buttonWidth, itemHeight, 0xFF555555);
                
                Component itemText = getDisplayText(value);
                int textWidth = font.width(itemText);
                int textX = buttonX + (buttonWidth - textWidth) / 2;
                int textY2 = itemY + (itemHeight - font.lineHeight) / 2;
                int itemTextColor = isSelected ? 0xFFFFFFFF : 0xFFAAAAAA;
                graphics.drawString(font, itemText, textX, textY2, itemTextColor, true);
            }
        } else {
            // Modern flat style dropdown overlay
            int buttonHeight = 16;
            int buttonX = controlDim.x();
            int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
            int buttonWidth = controlDim.width() - 4;
            
            int itemHeight = 14;
            int dropdownHeight = values.length * itemHeight;
            int dropdownY = flipUpward ? (buttonY - dropdownHeight) : (buttonY + buttonHeight);
            
            // Background
            fillRect(graphics, buttonX, dropdownY, buttonWidth, dropdownHeight, theme.backgroundColor());
            drawRect(graphics, buttonX, dropdownY, buttonWidth, dropdownHeight, theme.buttonBorder());
            
            // Items
            E currentValue = getValue();
            for (int i = 0; i < values.length; i++) {
                E value = values[i];
                int itemY = dropdownY + i * itemHeight;
                
                // Highlight hovered or selected
                boolean isHovered = mouseX >= buttonX && mouseX < buttonX + buttonWidth
                    && mouseY >= itemY && mouseY < itemY + itemHeight;
                boolean isSelected = value == currentValue;
                
                if (isHovered) {
                    this.hoveredIndex = i;
                    fillRect(graphics, buttonX + 1, itemY, buttonWidth - 2, itemHeight, theme.categoryHover());
                } else if (isSelected) {
                    fillRect(graphics, buttonX + 1, itemY, buttonWidth - 2, itemHeight, theme.categorySelected());
                }
                
                Component itemText = getDisplayText(value);
                int itemTextColor = isSelected ? theme.textPrimary() : theme.textSecondary();
                graphics.drawString(font, itemText, buttonX + 6, itemY + (itemHeight - font.lineHeight) / 2, itemTextColor, false);
            }
        }
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.enabled) return false;
        
        Dim2i controlDim = getControlDim();
        // Use correct dimensions based on theme
        int buttonHeight = theme.useVanillaWidgets() ? 20 : 16;
        int itemHeight = theme.useVanillaWidgets() ? 20 : 14;
        int buttonX = controlDim.x();
        int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
        int buttonWidth = controlDim.width() - 4;
        
        // Check if clicking on dropdown items
        if (this.expanded && this.enumClass != null) {
            E[] values = enumClass.getEnumConstants();
            int dropdownHeight = values.length * itemHeight;
            boolean flipUpward = shouldFlipUpward();
            int dropdownY = flipUpward ? (buttonY - dropdownHeight) : (buttonY + buttonHeight);
            
            for (int i = 0; i < values.length; i++) {
                int itemY = dropdownY + i * itemHeight;
                if (mouseX >= buttonX && mouseX < buttonX + buttonWidth
                    && mouseY >= itemY && mouseY < itemY + itemHeight) {
                    setValue(values[i]);
                    this.expanded = false;
                    return true;
                }
            }
        }
        
        // Check if clicking on button
        if (mouseX >= buttonX && mouseX < buttonX + buttonWidth
            && mouseY >= buttonY && mouseY < buttonY + buttonHeight) {
            this.expanded = !this.expanded;
            this.setFocused(true);
            return true;
        }
        
        // Clicked outside
        if (this.expanded) {
            this.expanded = false;
            return true;
        }
        
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused && this.enabled) {
            if (keyCode == 32 || keyCode == 257) { // Space or Enter
                if (this.expanded) {
                    if (hoveredIndex >= 0 && enumClass != null) {
                        E[] values = enumClass.getEnumConstants();
                        if (hoveredIndex < values.length) {
                            setValue(values[hoveredIndex]);
                        }
                    }
                    this.expanded = false;
                } else {
                    this.expanded = true;
                }
                return true;
            } else if (keyCode == 263) { // Left
                cycleValue(false);
                return true;
            } else if (keyCode == 262) { // Right
                cycleValue(true);
                return true;
            } else if (keyCode == 256) { // Escape
                if (this.expanded) {
                    this.expanded = false;
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean handleExpandedClick(double mouseX, double mouseY, int button) {
        if (!this.expanded || this.enumClass == null || button != 0 || !this.enabled) {
            return false;
        }
        
        Dim2i controlDim = getControlDim();
        // Use correct dimensions based on theme
        int buttonHeight = theme.useVanillaWidgets() ? 20 : 16;
        int itemHeight = theme.useVanillaWidgets() ? 20 : 14;
        int buttonX = controlDim.x();
        int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
        int buttonWidth = controlDim.width() - 4;
        
        E[] values = enumClass.getEnumConstants();
        int dropdownY = buttonY + buttonHeight;
        int dropdownHeight = values.length * itemHeight;
        
        // Check if click is in the dropdown menu area
        if (mouseX >= buttonX && mouseX < buttonX + buttonWidth
            && mouseY >= dropdownY && mouseY < dropdownY + dropdownHeight) {
            
            for (int i = 0; i < values.length; i++) {
                int itemY = dropdownY + i * itemHeight;
                if (mouseY >= itemY && mouseY < itemY + itemHeight) {
                    setValue(values[i]);
                    this.expanded = false;
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.expanded = false;
        }
    }
    
    @Override
    public boolean isExpanded() {
        return this.expanded;
    }
    
    @Override
    public void closeExpanded() {
        this.expanded = false;
    }
    
    @Override
    public void resetToDefault() {
        if (this.defaultValue != null) {
            setValue(this.defaultValue);
        }
        this.modified = false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.name);
        output.add(NarratedElementType.USAGE, getDisplayText(getValue()));
    }
}
