package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.gui.theme.Colors;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

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
    
    private boolean expanded = false;
    private boolean editingHex = false;
    private String hexInput = "";
    private int cursorPosition = 0;
    private int cursorBlinkTicks = 0;
    
    // HSV values for picker
    private float hue = 0;
    private float saturation = 1;
    private float value = 1;
    
    public ColorPickerWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public ColorPickerWidget binding(Supplier<Integer> getter, Consumer<Integer> setter) {
        this.getter = getter;
        this.setter = setter;
        updateHSVFromColor(getter.get());
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
                updateHSVFromColor(value);
                
                if (this.onChange != null) {
                    this.onChange.accept(value);
                }
            }
        }
    }
    
    private void updateHSVFromColor(int color) {
        int r = Colors.getRed(color);
        int g = Colors.getGreen(color);
        int b = Colors.getBlue(color);
        
        float[] hsv = rgbToHsv(r, g, b);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
    }
    
    private int hsvToColor() {
        int[] rgb = hsvToRgb(hue, saturation, value);
        int alpha = allowAlpha ? Colors.getAlpha(getValue()) : 255;
        return Colors.argb(alpha, rgb[0], rgb[1], rgb[2]);
    }
    
    private static final float INV_255 = 1f / 255f;
    
    // Reusable array to avoid allocations in hot path
    private final float[] hsvTemp = new float[3];
    
    private float[] rgbToHsv(int r, int g, int b) {
        float rf = r * INV_255;
        float gf = g * INV_255;
        float bf = b * INV_255;
        
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;
        
        float h = 0, s, v = max;
        
        if (max != 0) {
            s = delta / max;
        } else {
            hsvTemp[0] = 0; hsvTemp[1] = 0; hsvTemp[2] = 0;
            return hsvTemp;
        }
        
        if (delta != 0) {
            if (rf == max) {
                h = (gf - bf) / delta;
            } else if (gf == max) {
                h = 2 + (bf - rf) / delta;
            } else {
                h = 4 + (rf - gf) / delta;
            }
            h *= 60;
            if (h < 0) h += 360;
        }
        
        hsvTemp[0] = h; hsvTemp[1] = s; hsvTemp[2] = v;
        return hsvTemp;
    }
    
    // Reusable array for RGB output to avoid allocations
    private final int[] rgbTemp = new int[3];
    
    private int[] hsvToRgb(float h, float s, float v) {
        if (s == 0) {
            int gray = (int) (v * 255);
            rgbTemp[0] = gray; rgbTemp[1] = gray; rgbTemp[2] = gray;
            return rgbTemp;
        }
        
        h = h / 60f;
        int i = (int) h;  // floor for positive numbers
        float f = h - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));
        
        float r, g, b;
        switch (i % 6) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default -> { r = v; g = p; b = q; }
        }
        
        rgbTemp[0] = (int) (r * 255);
        rgbTemp[1] = (int) (g * 255);
        rgbTemp[2] = (int) (b * 255);
        return rgbTemp;
    }

    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
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
            net.minecraft.resources.ResourceLocation sprite = this.hovered 
                ? net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                : net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button");
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, previewX, previewY, previewSize, previewSize);
            // Draw color swatch inside with 2px padding
            fillRect(graphics, previewX + 2, previewY + 2, previewSize - 4, previewSize - 4, color);
        } else {
            fillRect(graphics, previewX, previewY, previewSize, previewSize, color);
            drawRect(graphics, previewX, previewY, previewSize, previewSize, theme.buttonBorder());
        }
        
        // Draw hex value
        String hexText = Colors.toHexWithHash(color);
        int textX = previewX + previewSize + 6;
        int textY = controlDim.getCenterY() - (font.lineHeight / 2);
        int textColor = theme.useVanillaWidgets() ? 0xFFE0E0E0 : theme.textSecondary();
        graphics.drawString(font, hexText, textX, textY, textColor, theme.useVanillaWidgets());
        
        // Expanded picker is rendered in renderOverlay to appear on top
        
        cursorBlinkTicks++;
    }
    
    /**
     * Determines the Y position for the picker, flipping upward if needed to avoid clipping.
     */
    private int getPickerY(int pickerHeight) {
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int normalY = dim.getLimitY() + 2;
        
        // Check if picker would extend beyond screen bottom (leave 10px margin)
        if ((normalY + pickerHeight) > (screenHeight - 10)) {
            // Render above the widget instead
            return dim.y() - pickerHeight - 2;
        }
        return normalY;
    }
    
    @Override
    public void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.expanded) return;
        
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        renderExpandedPicker(graphics, controlDim, mouseX, mouseY, font);
    }
    
    private void renderExpandedPicker(GuiGraphics graphics, Dim2i controlDim, int mouseX, int mouseY, net.minecraft.client.gui.Font font) {
        int pickerWidth = 150;
        int pickerHeight = 120;
        int pickerX = controlDim.x();
        int pickerY = getPickerY(pickerHeight);
        
        // Background
        if (theme.useVanillaWidgets()) {
            // Vanilla style - dark background with white border
            fillRect(graphics, pickerX - 1, pickerY - 1, pickerWidth + 2, pickerHeight + 2, 0xFFFFFFFF);
            fillRect(graphics, pickerX, pickerY, pickerWidth, pickerHeight, 0xFF000000);
        } else {
            fillRect(graphics, pickerX, pickerY, pickerWidth, pickerHeight, theme.backgroundColor());
            drawRect(graphics, pickerX, pickerY, pickerWidth, pickerHeight, theme.buttonBorder());
        }
        
        // Saturation/Value gradient (main color area)
        int svWidth = 100;
        int svHeight = 80;
        int svX = pickerX + 5;
        int svY = pickerY + 5;
        
        // Draw SV gradient - optimized to render rows instead of individual pixels
        // First fill with the base hue color
        int[] baseRgb = hsvToRgb(hue, 1, 1);
        int baseColor = Colors.rgb(baseRgb[0], baseRgb[1], baseRgb[2]);
        graphics.fill(svX, svY, svX + svWidth, svY + svHeight, baseColor);
        
        // Draw white-to-transparent gradient (left to right) for saturation
        for (int x = 0; x < svWidth; x++) {
            int alpha = 255 - (x * 255 / svWidth);
            int whiteOverlay = (alpha << 24) | 0xFFFFFF;
            graphics.fill(svX + x, svY, svX + x + 1, svY + svHeight, whiteOverlay);
        }
        
        // Draw black-to-transparent gradient (bottom to top) for value
        for (int y = 0; y < svHeight; y++) {
            int alpha = y * 255 / svHeight;
            int blackOverlay = (alpha << 24) | 0x000000;
            graphics.fill(svX, svY + y, svX + svWidth, svY + y + 1, blackOverlay);
        }
        drawRect(graphics, svX, svY, svWidth, svHeight, theme.buttonBorder());
        
        // Draw SV cursor
        int cursorX = svX + (int) (saturation * svWidth) - 2;
        int cursorY = svY + (int) ((1 - value) * svHeight) - 2;
        drawRect(graphics, cursorX, cursorY, 5, 5, 0xFFFFFFFF);
        
        // Hue slider
        int hueWidth = 15;
        int hueHeight = svHeight;
        int hueX = svX + svWidth + 5;
        int hueY = svY;
        
        // Draw hue gradient - one line per row (80 lines total)
        for (int y = 0; y < hueHeight; y++) {
            float h = (y / (float) hueHeight) * 360;
            int[] rgb = hsvToRgb(h, 1, 1);
            int c = Colors.rgb(rgb[0], rgb[1], rgb[2]);
            graphics.fill(hueX, hueY + y, hueX + hueWidth, hueY + y + 1, c);
        }
        drawRect(graphics, hueX, hueY, hueWidth, hueHeight, theme.buttonBorder());
        
        // Draw hue cursor
        int hueCursorY = hueY + (int) ((hue / 360) * hueHeight) - 1;
        graphics.fill(hueX - 2, hueCursorY, hueX + hueWidth + 2, hueCursorY + 3, 0xFFFFFFFF);
        
        // Hex input
        if (showHexInput) {
            int inputX = pickerX + 5;
            int inputY = svY + svHeight + 5;
            int inputWidth = svWidth + hueWidth + 5;
            int inputHeight = 14;
            
            fillRect(graphics, inputX, inputY, inputWidth, inputHeight, theme.inputBackground());
            int borderColor = editingHex ? theme.inputBorderFocused() : theme.inputBorder();
            drawRect(graphics, inputX, inputY, inputWidth, inputHeight, borderColor);
            
            String text = editingHex ? hexInput : Colors.toHexWithHash(getValue());
            graphics.drawString(font, text, inputX + 4, inputY + 3, theme.textPrimary(), false);
            
            // Cursor
            if (editingHex && cursorBlinkTicks / 6 % 2 == 0) {
                int cx = inputX + 4 + font.width(hexInput.substring(0, cursorPosition));
                graphics.fill(cx, inputY + 2, cx + 1, inputY + inputHeight - 2, theme.textPrimary());
            }
        }
        
        // Presets
        if (presets != null && presets.length > 0) {
            int presetsY = pickerY + pickerHeight - 18;
            int presetSize = 12;
            int spacing = 2;
            
            for (int i = 0; i < presets.length && i < 10; i++) {
                int px = pickerX + 5 + i * (presetSize + spacing);
                fillRect(graphics, px, presetsY, presetSize, presetSize, presets[i]);
                drawRect(graphics, px, presetsY, presetSize, presetSize, theme.buttonBorder());
            }
        }
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.enabled) return false;
        
        Dim2i controlDim = getControlDim();
        int previewSize = 16;
        int previewX = controlDim.x();
        int previewY = controlDim.getCenterY() - (previewSize / 2);
        
        // Check preview click
        if (mouseX >= previewX && mouseX < previewX + previewSize
            && mouseY >= previewY && mouseY < previewY + previewSize) {
            this.expanded = !this.expanded;
            this.setFocused(true);
            return true;
        }
        
        if (this.expanded) {
            int pickerX = controlDim.x();
            int pickerHeight = 120;
            int pickerY = getPickerY(pickerHeight);
            int svWidth = 100;
            int svHeight = 80;
            int svX = pickerX + 5;
            int svY = pickerY + 5;
            
            // SV area click
            if (mouseX >= svX && mouseX < svX + svWidth
                && mouseY >= svY && mouseY < svY + svHeight) {
                saturation = Mth.clamp((float) (mouseX - svX) / svWidth, 0, 1);
                value = Mth.clamp(1 - (float) (mouseY - svY) / svHeight, 0, 1);
                setValue(hsvToColor());
                return true;
            }
            
            // Hue slider click
            int hueX = svX + svWidth + 5;
            int hueWidth = 15;
            if (mouseX >= hueX && mouseX < hueX + hueWidth
                && mouseY >= svY && mouseY < svY + svHeight) {
                hue = Mth.clamp((float) (mouseY - svY) / svHeight * 360, 0, 360);
                setValue(hsvToColor());
                return true;
            }
            
            // Hex input click
            if (showHexInput) {
                int inputX = pickerX + 5;
                int inputY = svY + svHeight + 5;
                int inputWidth = svWidth + 15 + 5;
                int inputHeight = 14;
                
                if (mouseX >= inputX && mouseX < inputX + inputWidth
                    && mouseY >= inputY && mouseY < inputY + inputHeight) {
                    editingHex = true;
                    hexInput = Colors.toHexWithHash(getValue());
                    cursorPosition = hexInput.length();
                    return true;
                }
            }
            
            // Preset clicks
            if (presets != null) {
                int presetsY = pickerY + 120 - 18;
                int presetSize = 12;
                int spacing = 2;
                
                for (int i = 0; i < presets.length && i < 10; i++) {
                    int px = pickerX + 5 + i * (presetSize + spacing);
                    if (mouseX >= px && mouseX < px + presetSize
                        && mouseY >= presetsY && mouseY < presetsY + presetSize) {
                        setValue(presets[i]);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.expanded || !this.enabled) return false;
        
        Dim2i controlDim = getControlDim();
        int pickerX = controlDim.x();
        int pickerHeight = 120;
        int pickerY = getPickerY(pickerHeight);
        int svWidth = 100;
        int svHeight = 80;
        int svX = pickerX + 5;
        int svY = pickerY + 5;
        
        // SV area drag
        if (mouseX >= svX - 5 && mouseX < svX + svWidth + 5
            && mouseY >= svY - 5 && mouseY < svY + svHeight + 5) {
            saturation = Mth.clamp((float) (mouseX - svX) / svWidth, 0, 1);
            value = Mth.clamp(1 - (float) (mouseY - svY) / svHeight, 0, 1);
            setValue(hsvToColor());
            return true;
        }
        
        // Hue slider drag
        int hueX = svX + svWidth + 5;
        int hueWidth = 15;
        if (mouseX >= hueX - 5 && mouseX < hueX + hueWidth + 5) {
            hue = Mth.clamp((float) (mouseY - svY) / svHeight * 360, 0, 360);
            setValue(hsvToColor());
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean handleExpandedClick(double mouseX, double mouseY, int button) {
        if (!this.expanded || button != 0 || !this.enabled) {
            return false;
        }
        
        Dim2i controlDim = getControlDim();
        int pickerX = controlDim.x();
        int pickerY = dim.getLimitY() + 2;
        int pickerWidth = 130;
        int pickerHeight = 120;
        
        // Check if click is in the picker area
        if (mouseX >= pickerX && mouseX < pickerX + pickerWidth
            && mouseY >= pickerY && mouseY < pickerY + pickerHeight) {
            
            int svWidth = 100;
            int svHeight = 80;
            int svX = pickerX + 5;
            int svY = pickerY + 5;
            
            // SV area click
            if (mouseX >= svX && mouseX < svX + svWidth
                && mouseY >= svY && mouseY < svY + svHeight) {
                saturation = Mth.clamp((float) (mouseX - svX) / svWidth, 0, 1);
                value = Mth.clamp(1 - (float) (mouseY - svY) / svHeight, 0, 1);
                setValue(hsvToColor());
                return true;
            }
            
            // Hue slider click
            int hueX = svX + svWidth + 5;
            int hueWidth = 15;
            if (mouseX >= hueX && mouseX < hueX + hueWidth
                && mouseY >= svY && mouseY < svY + svHeight) {
                hue = Mth.clamp((float) (mouseY - svY) / svHeight * 360, 0, 360);
                setValue(hsvToColor());
                return true;
            }
            
            // Hex input click
            if (showHexInput) {
                int inputX = pickerX + 5;
                int inputY = svY + svHeight + 5;
                int inputWidth = svWidth + 15 + 5;
                int inputHeight = 14;
                
                if (mouseX >= inputX && mouseX < inputX + inputWidth
                    && mouseY >= inputY && mouseY < inputY + inputHeight) {
                    editingHex = true;
                    hexInput = Colors.toHexWithHash(getValue());
                    cursorPosition = hexInput.length();
                    return true;
                }
            }
            
            // Preset clicks
            if (presets != null) {
                int presetsY = pickerY + 120 - 18;
                int presetSize = 12;
                int spacing = 2;
                
                for (int i = 0; i < presets.length && i < 10; i++) {
                    int px = pickerX + 5 + i * (presetSize + spacing);
                    if (mouseX >= px && mouseX < px + presetSize
                        && mouseY >= presetsY && mouseY < presetsY + presetSize) {
                        setValue(presets[i]);
                        return true;
                    }
                }
            }
            
            return true; // Consumed click even if not on a specific element
        }
        
        return false;
    }

    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingHex) {
            if (keyCode == 257 || keyCode == 335) { // Enter
                try {
                    int color = Colors.fromHex(hexInput);
                    setValue(color);
                } catch (Exception ignored) {}
                editingHex = false;
                return true;
            } else if (keyCode == 256) { // Escape
                editingHex = false;
                return true;
            } else if (keyCode == 259) { // Backspace
                if (cursorPosition > 0 && hexInput.length() > 0) {
                    hexInput = hexInput.substring(0, cursorPosition - 1) + hexInput.substring(cursorPosition);
                    cursorPosition--;
                }
                return true;
            } else if (keyCode == 263) { // Left
                if (cursorPosition > 0) cursorPosition--;
                return true;
            } else if (keyCode == 262) { // Right
                if (cursorPosition < hexInput.length()) cursorPosition++;
                return true;
            }
        }
        
        if (this.focused && this.enabled) {
            if (keyCode == 32 || keyCode == 257) { // Space or Enter
                this.expanded = !this.expanded;
                return true;
            } else if (keyCode == 256 && this.expanded) { // Escape
                this.expanded = false;
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean onCharTyped(char chr, int modifiers) {
        if (editingHex) {
            if ((chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'f') 
                || (chr >= 'A' && chr <= 'F') || chr == '#') {
                if (hexInput.length() < 9) {
                    hexInput = hexInput.substring(0, cursorPosition) + chr + hexInput.substring(cursorPosition);
                    cursorPosition++;
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.expanded = false;
            this.editingHex = false;
        }
    }
    
    @Override
    public boolean isExpanded() {
        return this.expanded;
    }
    
    @Override
    public void closeExpanded() {
        this.expanded = false;
        this.editingHex = false;
    }
    
    @Override
    public void resetToDefault() {
        setValue(this.defaultValue);
        this.modified = false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.name);
        output.add(NarratedElementType.USAGE, Component.literal(Colors.toHexWithHash(getValue())));
    }
}
