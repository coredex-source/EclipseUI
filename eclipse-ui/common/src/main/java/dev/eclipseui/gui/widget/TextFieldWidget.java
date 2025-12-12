package dev.eclipseui.gui.widget;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A text input widget for string options.
 */
public class TextFieldWidget extends OptionWidget {
    
    private Supplier<String> getter;
    private Consumer<String> setter;
    private String defaultValue = "";
    private boolean liveUpdate;
    private Consumer<String> onChange;
    private Predicate<String> validator;
    private Component placeholder;
    private Component errorMessage;
    private int maxLength = 256;
    
    // Text editing state
    private String currentText = "";
    private int cursorPosition = 0;
    private int selectionStart = 0;
    private int scrollOffset = 0;
    private boolean textFocused = false;
    private boolean isValid = true;
    
    // Cursor blink
    private int cursorBlinkTicks = 0;
    
    public TextFieldWidget(Dim2i dim, ThemeData theme, Component name) {
        super(dim, theme, name);
    }
    
    public TextFieldWidget binding(Supplier<String> getter, Consumer<String> setter) {
        this.getter = getter;
        this.setter = setter;
        this.currentText = getter.get();
        this.cursorPosition = currentText.length();
        return this;
    }
    
    public TextFieldWidget defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public TextFieldWidget placeholder(Component placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public TextFieldWidget maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }
    
    public TextFieldWidget validator(Predicate<String> validator) {
        this.validator = validator;
        return this;
    }
    
    public TextFieldWidget liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }
    
    public TextFieldWidget onChange(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }
    
    public TextFieldWidget errorMessage(Component errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
    
    public String getValue() {
        return this.getter != null ? this.getter.get() : this.currentText;
    }
    
    public void setValue(String value) {
        String oldValue = getValue();
        this.currentText = value;
        validateText();
        
        if (this.setter != null && this.isValid) {
            this.setter.accept(value);
        }
        
        if (!oldValue.equals(value)) {
            this.modified = true;
            
            if (this.onChange != null && this.isValid) {
                this.onChange.accept(value);
            }
        }
    }
    
    private void validateText() {
        if (this.validator != null) {
            this.isValid = this.validator.test(this.currentText);
        } else {
            this.isValid = true;
        }
    }
    
    @Override
    protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Dim2i controlDim = getControlDim();
        var font = Minecraft.getInstance().font;
        
        // Input field dimensions
        int fieldHeight = theme.useVanillaWidgets() ? 20 : 16;
        int fieldX = controlDim.x();
        int fieldY = controlDim.getCenterY() - (fieldHeight / 2);
        int fieldWidth = controlDim.width() - 4;
        
        if (theme.useVanillaWidgets()) {
            // Vanilla text field style (black background with white border)
            // Draw outer border (white/gray)
            int outerBorderColor = this.textFocused ? 0xFFFFFFFF : 0xFFA0A0A0;
            if (!this.isValid) {
                outerBorderColor = 0xFFFF5555;
            }
            graphics.fill(fieldX - 1, fieldY - 1, fieldX + fieldWidth + 1, fieldY + fieldHeight + 1, outerBorderColor);
            
            // Draw black background
            graphics.fill(fieldX, fieldY, fieldX + fieldWidth, fieldY + fieldHeight, 0xFF000000);
        } else {
            // Modern flat style
            // Draw background
            fillRect(graphics, fieldX, fieldY, fieldWidth, fieldHeight, theme.inputBackground());
            
            // Draw border
            int borderColor;
            if (!this.isValid) {
                borderColor = 0xFFFF5555;
            } else if (this.textFocused) {
                borderColor = theme.inputBorderFocused();
            } else {
                borderColor = theme.inputBorder();
            }
            drawRect(graphics, fieldX, fieldY, fieldWidth, fieldHeight, borderColor);
        }
        
        // Calculate text rendering area
        int textX = fieldX + 4;
        int textY = fieldY + (fieldHeight - font.lineHeight) / 2;
        int maxTextWidth = fieldWidth - 8;
        
        // Draw text or placeholder
        if (this.currentText.isEmpty() && this.placeholder != null && !this.textFocused) {
            int placeholderColor = theme.useVanillaWidgets() ? 0xFF707070 : theme.textDisabled();
            graphics.drawString(font, this.placeholder, textX, textY, placeholderColor, theme.useVanillaWidgets());
        } else {
            String displayText = this.currentText;
            
            // Handle scrolling for long text
            if (font.width(displayText) > maxTextWidth) {
                // Simple scrolling: show text around cursor
                while (font.width(displayText.substring(0, Math.min(cursorPosition, displayText.length()))) > maxTextWidth - 10) {
                    displayText = displayText.substring(1);
                    scrollOffset++;
                }
            }
            
            int textColor;
            if (theme.useVanillaWidgets()) {
                textColor = this.enabled ? 0xFFE0E0E0 : 0xFF707070;
            } else {
                textColor = this.enabled ? theme.textPrimary() : theme.textDisabled();
            }
            graphics.drawString(font, displayText, textX, textY, textColor, theme.useVanillaWidgets());
            
            // Draw cursor
            if (this.textFocused && this.cursorBlinkTicks / 6 % 2 == 0) {
                int cursorX = textX + font.width(displayText.substring(0, Math.min(cursorPosition - scrollOffset, displayText.length())));
                graphics.fill(cursorX, textY - 1, cursorX + 1, textY + font.lineHeight, theme.textPrimary());
            }
        }
        
        // Update cursor blink
        this.cursorBlinkTicks++;
    }
    
    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.enabled) {
            Dim2i controlDim = getControlDim();
            int fieldHeight = 16;
            int fieldY = controlDim.getCenterY() - (fieldHeight / 2);
            
            // Check if click is within text field
            if (mouseX >= controlDim.x() && mouseX < controlDim.x() + controlDim.width() - 4
                && mouseY >= fieldY && mouseY < fieldY + fieldHeight) {
                this.textFocused = true;
                this.setFocused(true); // Also set widget focus for key event forwarding
                this.cursorBlinkTicks = 0;
                
                // Position cursor based on click
                var font = Minecraft.getInstance().font;
                int textX = controlDim.x() + 4;
                int clickOffset = (int) mouseX - textX;
                
                int pos = 0;
                for (int i = 0; i <= currentText.length(); i++) {
                    if (font.width(currentText.substring(0, i)) >= clickOffset) {
                        pos = i;
                        break;
                    }
                    pos = i;
                }
                this.cursorPosition = pos;
                return true;
            } else {
                this.textFocused = false;
                this.setFocused(false);
            }
        }
        return false;
    }
    
    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.textFocused || !this.enabled) {
            return false;
        }
        
        boolean ctrl = (modifiers & 2) != 0; // GLFW_MOD_CONTROL
        
        switch (keyCode) {
            case 259: // Backspace
                if (cursorPosition > 0) {
                    currentText = currentText.substring(0, cursorPosition - 1) + currentText.substring(cursorPosition);
                    cursorPosition--;
                    setValue(currentText);
                }
                return true;
                
            case 261: // Delete
                if (cursorPosition < currentText.length()) {
                    currentText = currentText.substring(0, cursorPosition) + currentText.substring(cursorPosition + 1);
                    setValue(currentText);
                }
                return true;
                
            case 263: // Left arrow
                if (cursorPosition > 0) {
                    cursorPosition--;
                    cursorBlinkTicks = 0;
                }
                return true;
                
            case 262: // Right arrow
                if (cursorPosition < currentText.length()) {
                    cursorPosition++;
                    cursorBlinkTicks = 0;
                }
                return true;
                
            case 268: // Home
                cursorPosition = 0;
                cursorBlinkTicks = 0;
                return true;
                
            case 269: // End
                cursorPosition = currentText.length();
                cursorBlinkTicks = 0;
                return true;
                
            case 86: // V (paste)
                if (ctrl) {
                    String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                    insertText(clipboard);
                    return true;
                }
                break;
                
            case 67: // C (copy)
                if (ctrl) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(currentText);
                    return true;
                }
                break;
                
            case 65: // A (select all)
                if (ctrl) {
                    cursorPosition = currentText.length();
                    selectionStart = 0;
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    @Override
    protected boolean onCharTyped(char chr, int modifiers) {
        if (!this.textFocused || !this.enabled) {
            return false;
        }
        
        if (Character.isISOControl(chr)) {
            return false;
        }
        
        insertText(String.valueOf(chr));
        return true;
    }
    
    private void insertText(String text) {
        if (currentText.length() + text.length() > maxLength) {
            text = text.substring(0, maxLength - currentText.length());
        }
        
        if (!text.isEmpty()) {
            currentText = currentText.substring(0, cursorPosition) + text + currentText.substring(cursorPosition);
            cursorPosition += text.length();
            setValue(currentText);
            cursorBlinkTicks = 0;
        }
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.textFocused = false;
        }
    }
    
    @Override
    public void resetToDefault() {
        setValue(this.defaultValue);
        this.currentText = this.defaultValue;
        this.cursorPosition = currentText.length();
        this.modified = false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.name);
        if (!currentText.isEmpty()) {
            output.add(NarratedElementType.USAGE, Component.literal(currentText));
        }
    }
}
