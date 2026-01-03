package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A slider widget for numeric options.
 */
public class SliderWidget extends OptionWidget {
    
    private Supplier<Double> getter;
    private Consumer<Double> setter;
    private double defaultValue;
    private double min = 0;
    private double max = 100;
    private double step = 1;
    private boolean liveUpdate;
    private Consumer<Double> onChange;
    private Function<Double, Component> formatter;
    private boolean showValue = true;
    private String suffix = "";
    
    private boolean dragging = false;
    
    // Cache for value text to avoid repeated string formatting
    private double cachedValue = Double.NaN;
    private Component cachedValueText = null;
    
    public SliderWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public SliderWidget bindingDouble(Supplier<Double> getter, Consumer<Double> setter) {
        this.getter = getter;
        this.setter = setter;
        return this;
    }
    
    public SliderWidget bindingInt(Supplier<Integer> getter, Consumer<Integer> setter) {
        this.getter = () -> getter.get().doubleValue();
        this.setter = value -> setter.accept(value.intValue());
        return this;
    }
    
    public SliderWidget bindingFloat(Supplier<Float> getter, Consumer<Float> setter) {
        this.getter = () -> getter.get().doubleValue();
        this.setter = value -> setter.accept(value.floatValue());
        return this;
    }
    
    public SliderWidget range(double min, double max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;
        return this;
    }
    
    public SliderWidget min(double min) {
        this.min = min;
        return this;
    }
    
    public SliderWidget max(double max) {
        this.max = max;
        return this;
    }
    
    public SliderWidget step(double step) {
        this.step = step;
        return this;
    }
    
    public SliderWidget defaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public SliderWidget liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }
    
    public SliderWidget onChange(Consumer<Double> onChange) {
        this.onChange = onChange;
        return this;
    }
    
    public SliderWidget formatter(Function<Double, Component> formatter) {
        this.formatter = formatter;
        return this;
    }
    
    public SliderWidget showValue(boolean showValue) {
        this.showValue = showValue;
        return this;
    }
    
    public SliderWidget suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }
    
    public double getValue() {
        return this.getter != null ? this.getter.get() : this.min;
    }
    
    public void setValue(double value) {
        if (this.setter != null) {
            double oldValue = getValue();
            double newValue = snapToStep(Mth.clamp(value, min, max));
            this.setter.accept(newValue);
            
            if (oldValue != newValue) {
                this.modified = true;
                
                if (this.onChange != null) {
                    this.onChange.accept(newValue);
                }
            }
        }
    }
    
    private double snapToStep(double value) {
        if (step <= 0) return value;
        return Math.round(value / step) * step;
    }
    
    private double getProgress() {
        if (max <= min) return 0;
        return (getValue() - min) / (max - min);
    }
    
    private Component getValueText() {
        double value = getValue();
        
        // Return cached text if value hasn't changed
        if (value == cachedValue && cachedValueText != null) {
            return cachedValueText;
        }
        cachedValue = value;
        
        if (this.formatter != null) {
            cachedValueText = this.formatter.apply(value);
            return cachedValueText;
        }
        
        // Format based on step precision
        String formatted;
        if (step >= 1) {
            formatted = String.valueOf((int) value);
        } else if (step >= 0.1) {
            formatted = String.format("%.1f", value);
        } else {
            formatted = String.format("%.2f", value);
        }
        
        cachedValueText = Component.literal(formatted + suffix);
        return cachedValueText;
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        
        if (theme.useVanillaWidgets()) {
            // Vanilla slider style (like Minecraft's FOV slider)
            int sliderHeight = 20;
            int sliderWidth = controlDim.width() - 4;
            int sliderX = controlDim.x();
            int sliderY = controlDim.getCenterY() - (sliderHeight / 2);
            
            // Draw vanilla button background using sprite
            net.minecraft.resources.Identifier sprite = this.enabled 
                ? net.minecraft.resources.Identifier.withDefaultNamespace("widget/button")
                : net.minecraft.resources.Identifier.withDefaultNamespace("widget/button_disabled");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, sliderX, sliderY, sliderWidth, sliderHeight);
            
            // Draw slider handle
            double progress = getProgress();
            int handleWidth = 8;
            int handleX = sliderX + 4 + (int) ((sliderWidth - 8 - handleWidth) * progress);
            
            // Draw highlighted section under handle using a lighter button sprite
            net.minecraft.resources.Identifier handleSprite = net.minecraft.resources.Identifier.withDefaultNamespace("widget/button_highlighted");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, handleSprite, handleX, sliderY, handleWidth, sliderHeight);
            
            // Draw value text centered with shadow
            Component text = getValueText();
            int textWidth = font.width(text);
            int textX = sliderX + (sliderWidth - textWidth) / 2;
            int textY = sliderY + (sliderHeight - font.lineHeight) / 2;
            int textColor = this.enabled ? 0xFFFFFFFF : 0xFF707070;
            graphics.drawString(font, text, textX, textY, textColor, true);
        } else {
            // Modern flat slider style
            int sliderHeight = 8;
            int textWidth = showValue ? font.width(getValueText()) + 8 : 0;
            int sliderWidth = controlDim.width() - textWidth - 4;
            int sliderX = controlDim.x();
            int sliderY = controlDim.getCenterY() - (sliderHeight / 2);
            
            // Draw track background
            fillRect(graphics, sliderX, sliderY, sliderWidth, sliderHeight, theme.sliderTrack());
            
            // Draw filled portion
            double progress = getProgress();
            int filledWidth = (int) (sliderWidth * progress);
            fillRect(graphics, sliderX, sliderY, filledWidth, sliderHeight, theme.sliderFilled());
            
            // Draw track border
            int borderColor = this.focused ? theme.accentPrimary() : theme.buttonBorder();
            drawRect(graphics, sliderX, sliderY, sliderWidth, sliderHeight, borderColor);
            
            // Draw handle
            int handleWidth = 6;
            int handleHeight = 14;
            int handleX = sliderX + filledWidth - (handleWidth / 2);
            int handleY = controlDim.getCenterY() - (handleHeight / 2);
            
            // Clamp handle position
            handleX = Math.max(sliderX, Math.min(handleX, sliderX + sliderWidth - handleWidth));
            
            fillRect(graphics, handleX, handleY, handleWidth, handleHeight, theme.sliderHandle());
            drawRect(graphics, handleX, handleY, handleWidth, handleHeight, theme.buttonBorder());
            
            // Draw value text
            if (showValue) {
                Component text = getValueText();
                int textX = sliderX + sliderWidth + 8;
                int textY = controlDim.getCenterY() - (font.lineHeight / 2);
                int textColor = this.enabled ? theme.textSecondary() : theme.textDisabled();
                graphics.drawString(font, text, textX, textY, textColor, false);
            }
        }
    }
    
    private void updateValueFromMouse(double mouseX) {
        Dim2i controlDim = getControlDim();
        
        double sliderStartX;
        double sliderTrackWidth;
        
        if (theme.useVanillaWidgets()) {
            // Vanilla layout: full width slider with internal padding
            int sliderWidth = controlDim.width() - 4;
            int sliderX = controlDim.x();
            int handleWidth = 8;
            
            // The draggable area is inside the slider with padding
            sliderStartX = sliderX + 4;
            sliderTrackWidth = sliderWidth - 8 - handleWidth;
        } else {
            // Modern layout: slider with text beside it
            var font = Minecraft.getInstance().font;
            int textWidth = showValue ? font.width(getValueText()) + 8 : 0;
            int sliderWidth = controlDim.width() - textWidth - 4;
            
            sliderStartX = controlDim.x();
            sliderTrackWidth = sliderWidth;
        }
        
        double progress = (mouseX - sliderStartX) / sliderTrackWidth;
        progress = Mth.clamp(progress, 0, 1);
        
        double value = min + (max - min) * progress;
        setValue(value);
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.enabled) {
            this.dragging = true;
            this.setFocused(true);
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.dragging = false;
        }
        return false;
    }
    
    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.dragging && this.enabled) {
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused && this.enabled) {
            // Left/Right arrow keys to adjust
            if (keyCode == 263) { // Left
                setValue(getValue() - step);
                return true;
            } else if (keyCode == 262) { // Right
                setValue(getValue() + step);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void resetToDefault() {
        setValue(this.defaultValue);
        this.modified = false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.name);
        output.add(NarratedElementType.USAGE, getValueText());
    }
}
