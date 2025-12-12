package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A toggle switch widget for boolean options.
 */
public class ToggleWidget extends OptionWidget {
    
    private Supplier<Boolean> getter;
    private Consumer<Boolean> setter;
    private boolean defaultValue;
    private boolean liveUpdate;
    private Consumer<Boolean> onChange;
    
    private Component onText = Component.translatable("eclipseui.toggle.on");
    private Component offText = Component.translatable("eclipseui.toggle.off");
    
    // Animation
    private float animationProgress = 0f;
    private static final float ANIMATION_SPEED = 0.2f;
    
    public ToggleWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public ToggleWidget binding(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this.getter = getter;
        this.setter = setter;
        this.animationProgress = getter.get() ? 1f : 0f;
        return this;
    }
    
    public ToggleWidget defaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public ToggleWidget liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }
    
    public ToggleWidget onChange(Consumer<Boolean> onChange) {
        this.onChange = onChange;
        return this;
    }
    
    public ToggleWidget onText(Component text) {
        this.onText = text;
        return this;
    }
    
    public ToggleWidget offText(Component text) {
        this.offText = text;
        return this;
    }
    
    public boolean getValue() {
        return this.getter != null ? this.getter.get() : false;
    }
    
    public void setValue(boolean value) {
        if (this.setter != null) {
            boolean oldValue = getValue();
            this.setter.accept(value);
            
            if (oldValue != value) {
                this.modified = true;
                
                if (this.onChange != null) {
                    this.onChange.accept(value);
                }
            }
        }
    }
    
    private void toggle() {
        setValue(!getValue());
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Dim2i controlDim = getControlDim();
        boolean value = getValue();
        var font = Minecraft.getInstance().font;
        
        // Update animation
        float targetProgress = value ? 1f : 0f;
        if (animationProgress < targetProgress) {
            animationProgress = Math.min(animationProgress + ANIMATION_SPEED, targetProgress);
        } else if (animationProgress > targetProgress) {
            animationProgress = Math.max(animationProgress - ANIMATION_SPEED, targetProgress);
        }
        
        if (theme.useVanillaWidgets()) {
            // Vanilla button style toggle (like Minecraft's ON/OFF buttons)
            int buttonWidth = 50;
            int buttonHeight = 20;
            int buttonX = controlDim.x();
            int buttonY = controlDim.getCenterY() - (buttonHeight / 2);
            
            // Draw vanilla button using sprites
            net.minecraft.resources.ResourceLocation sprite = this.enabled 
                ? net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button")
                : net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_disabled");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, buttonX, buttonY, buttonWidth, buttonHeight);
            
            // Draw text centered with shadow
            Component text = value ? this.onText : this.offText;
            int textWidth = font.width(text);
            int textX = buttonX + (buttonWidth - textWidth) / 2;
            int textY = buttonY + (buttonHeight - font.lineHeight) / 2;
            int textColor = this.enabled ? (value ? 0xFFFFFFFF : 0xFFAAAAAA) : 0xFF707070;
            graphics.drawString(font, text, textX, textY, textColor, true);
        } else {
            // Modern toggle switch style
            int switchWidth = 36;
            int switchHeight = 16;
            int switchX = controlDim.x();
            int switchY = controlDim.getCenterY() - (switchHeight / 2);
            
            // Draw track
            int trackColor = interpolateColor(theme.toggleOff(), theme.toggleOn(), animationProgress);
            fillRect(graphics, switchX, switchY, switchWidth, switchHeight, trackColor);
            
            // Draw border
            int borderColor = this.focused ? theme.accentPrimary() : theme.buttonBorder();
            drawRect(graphics, switchX, switchY, switchWidth, switchHeight, borderColor);
            
            // Draw handle
            int handleWidth = 14;
            int handleHeight = 12;
            int handleX = switchX + 2 + (int)((switchWidth - handleWidth - 4) * animationProgress);
            int handleY = switchY + 2;
            fillRect(graphics, handleX, handleY, handleWidth, handleHeight, theme.toggleHandle());
            
            // Draw text
            Component text = value ? this.onText : this.offText;
            int textX = switchX + switchWidth + 6;
            int textY = controlDim.getCenterY() - (font.lineHeight / 2);
            int textColor = this.enabled ? theme.textSecondary() : theme.textDisabled();
            graphics.drawString(font, text, textX, textY, textColor, false);
        }
    }
    
    private int interpolateColor(int color1, int color2, float factor) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int)(a1 + (a2 - a1) * factor);
        int r = (int)(r1 + (r2 - r1) * factor);
        int g = (int)(g1 + (g2 - g1) * factor);
        int b = (int)(b1 + (b2 - b1) * factor);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.enabled) {
            toggle();
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused && this.enabled) {
            // Space or Enter to toggle
            if (keyCode == 32 || keyCode == 257) {
                toggle();
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
        output.add(NarratedElementType.USAGE, getValue() ? this.onText : this.offText);
    }
}
