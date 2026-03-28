package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.gui.theme.Colors;
import dev.eclipseui.gui.screen.ColorPickerEditScreen;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A color picker widget for color options.
 */
public class ColorPickerWidget extends OptionWidget {
    
    private Supplier<Integer> getter;
    private Consumer<Integer> setter;
    private int defaultValue = 0xFFFFFFFF;
    private boolean liveUpdate;
    private Consumer<Integer> onChange;
    private boolean allowAlpha = false;
    private boolean showHexInput = true;
    private int[] presets;
    
    public ColorPickerWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public ColorPickerWidget binding(Supplier<Integer> getter, Consumer<Integer> setter) {
        this.getter = getter;
        this.setter = setter;
        return this;
    }
    
    public ColorPickerWidget defaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public ColorPickerWidget allowAlpha(boolean allowAlpha) {
        this.allowAlpha = allowAlpha;
        return this;
    }
    
    public ColorPickerWidget liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }
    
    public ColorPickerWidget onChange(Consumer<Integer> onChange) {
        this.onChange = onChange;
        return this;
    }
    
    public ColorPickerWidget showHexInput(boolean show) {
        this.showHexInput = show;
        return this;
    }
    
    public ColorPickerWidget presets(int... presets) {
        this.presets = presets;
        return this;
    }
    
    public int getValue() {
        return this.getter != null ? this.getter.get() : this.defaultValue;
    }
    
    public void setValue(int value) {
        if (this.setter != null) {
            int oldValue = getValue();
            if (!allowAlpha) {
                value = (value & 0x00FFFFFF) | 0xFF000000;
            }
            this.setter.accept(value);
            
            if (oldValue != value) {
                this.modified = true;
                
                if (this.onChange != null) {
                    this.onChange.accept(value);
                }
            }
        }
    }

    private String getDisplayHex(int color) {
        if (allowAlpha) {
            return Colors.toHexWithHash(color);
        }
        return String.format("#%06X", color & 0x00FFFFFF);
    }

    private void openPickerScreen() {
        var minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            return;
        }

        minecraft.setScreen(new ColorPickerEditScreen(
            minecraft.screen,
            theme,
            name,
            allowAlpha,
            showHexInput,
            defaultValue,
            presets,
            this::getValue,
            this::setValue
        ));
    }

    @Override
    protected void renderControl(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        
        // Color preview button
        int previewSize = theme.useVanillaWidgets() ? 20 : 16;
        int previewX = controlDim.x();
        int previewY = controlDim.getCenterY() - (previewSize / 2);
        
        // Draw color preview with border
        int color = getValue();
        
        if (theme.useVanillaWidgets()) {
            // Draw button background first
            net.minecraft.resources.Identifier sprite = this.hovered 
                ? net.minecraft.resources.Identifier.withDefaultNamespace("widget/button_highlighted")
                : net.minecraft.resources.Identifier.withDefaultNamespace("widget/button");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, previewX, previewY, previewSize, previewSize);
            // Draw color swatch inside with 2px padding
            fillRect(graphics, previewX + 2, previewY + 2, previewSize - 4, previewSize - 4, color);
        } else {
            fillRect(graphics, previewX, previewY, previewSize, previewSize, color);
            drawRect(graphics, previewX, previewY, previewSize, previewSize, theme.buttonBorder());
        }
        
        // Draw hex value
        String hexText = getDisplayHex(color);
        int textX = previewX + previewSize + 6;
        int textY = controlDim.getCenterY() - (font.lineHeight / 2);
        int textColor = theme.useVanillaWidgets() ? 0xFFE0E0E0 : theme.textSecondary();
        graphics.text(font, hexText, textX, textY, textColor, theme.useVanillaWidgets());
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.enabled) return false;
        
        Dim2i controlDim = getControlDim();
        int previewSize = theme.useVanillaWidgets() ? 20 : 16;
        int previewX = controlDim.x();
        int previewY = controlDim.getCenterY() - (previewSize / 2);
        
        // Check preview click
        if (mouseX >= previewX && mouseX < previewX + previewSize
            && mouseY >= previewY && mouseY < previewY + previewSize) {
            this.setFocused(true);
            openPickerScreen();
            return true;
        }

        return false;
    }

    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused && this.enabled) {
            if (keyCode == 32 || keyCode == 257) { // Space or Enter
                openPickerScreen();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }
    
    @Override
    public boolean isExpanded() {
        return false;
    }
    
    @Override
    public void closeExpanded() {
        // No inline overlay in the new color picker flow.
    }
    
    @Override
    public void resetToDefault() {
        setValue(this.defaultValue);
        this.modified = false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.name);
        output.add(NarratedElementType.USAGE, Component.literal(getDisplayHex(getValue())));
    }
}
