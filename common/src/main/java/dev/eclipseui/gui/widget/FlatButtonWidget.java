package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * A simple flat-style button widget.
 */
public class FlatButtonWidget extends AbstractWidget {
    
    // Vanilla button sprite locations
    private static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation BUTTON_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_disabled");
    private static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
    
    private Component text;
    private Consumer<FlatButtonWidget> onClick;
    
    public FlatButtonWidget(Dim2i dim, ThemeData theme, Component text) {
        super(dim, theme);
        this.text = text;
    }
    
    public FlatButtonWidget onClick(Consumer<FlatButtonWidget> onClick) {
        this.onClick = onClick;
        return this;
    }
    
    public void setText(Component text) {
        this.text = text;
    }
    
    public Component getText() {
        return this.text;
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.theme.useVanillaWidgets()) {
            renderVanillaButton(graphics, mouseX, mouseY);
        } else {
            renderFlatButton(graphics, mouseX, mouseY);
        }
    }
    
    private void renderVanillaButton(GuiGraphics graphics, int mouseX, int mouseY) {
        var font = Minecraft.getInstance().font;
        
        // Select sprite based on state
        ResourceLocation sprite;
        int textColor;
        if (!this.enabled) {
            sprite = BUTTON_DISABLED_SPRITE;
            textColor = 0xFFA0A0A0;
        } else if (this.hovered) {
            sprite = BUTTON_HIGHLIGHTED_SPRITE;
            textColor = 0xFFFFFFA0; // Yellow tint on hover
        } else {
            sprite = BUTTON_SPRITE;
            textColor = 0xFFFFFFFF;
        }
        
        // Draw the button using vanilla 9-slice sprite
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, dim.x(), dim.y(), dim.width(), dim.height());
        
        // Draw text centered
        int textWidth = font.width(this.text);
        int textX = dim.getCenterX() - (textWidth / 2);
        int textY = dim.getCenterY() - (font.lineHeight / 2);
        graphics.drawString(font, this.text, textX, textY, textColor, true);
    }
    
    private void renderFlatButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int bgColor;
        int textColor;
        
        if (!this.enabled) {
            bgColor = this.theme.buttonBackgroundDisabled();
            textColor = this.theme.textDisabled();
        } else if (this.hovered) {
            bgColor = this.theme.buttonBackgroundHover();
            textColor = this.theme.textPrimary();
        } else {
            bgColor = this.theme.buttonBackground();
            textColor = this.theme.textPrimary();
        }
        
        // Draw background
        fillRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), bgColor);
        
        // Draw border
        if (this.focused) {
            drawRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), this.theme.accentPrimary());
        } else {
            drawRect(graphics, dim.x(), dim.y(), dim.width(), dim.height(), this.theme.buttonBorder());
        }
        
        // Draw text centered
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text);
        int textX = dim.getCenterX() - (textWidth / 2);
        int textY = dim.getCenterY() - (font.lineHeight / 2);
        graphics.drawString(font, this.text, textX, textY, textColor, false);
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.onClick != null) {
            this.onClick.accept(this);
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused && (keyCode == 257 || keyCode == 335)) { // Enter or Numpad Enter
            if (this.onClick != null) {
                this.onClick.accept(this);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.text);
    }
}
