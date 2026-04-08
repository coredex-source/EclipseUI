package dev.eclipseui.gui.screen;

import dev.eclipseui.api.ThemeData;
import dev.eclipseui.gui.theme.Colors;
import dev.eclipseui.util.MinecraftScreenCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorPickerEditScreen extends Screen {

    private enum FocusedInput {
        NONE,
        ARGB,
        HEX
    }

    private static final float INV_255 = 1f / 255f;
    private static final int INPUT_HEIGHT = 18;
    private static final int INPUT_ROW_STEP = 30;

    private final Screen parent;
    private final ThemeData theme;
    private final Component optionName;
    private final boolean allowAlpha;
    private final boolean showHexInput;
    private final int defaultColor;
    private final int[] presets;
    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;

    private int originalColor;
    private int currentColor;

    private float hue;
    private float saturation;
    private float value;

    private FocusedInput focusedInput = FocusedInput.NONE;
    private String argbInput = "";
    private String hexInput = "";
    private int cursorPosition = 0;
    private int cursorBlinkTicks = 0;

    private boolean draggingSV;
    private boolean draggingHue;
    private boolean draggingAlpha;

    private int openTicks;

    public ColorPickerEditScreen(
        Screen parent,
        ThemeData theme,
        Component optionName,
        boolean allowAlpha,
        boolean showHexInput,
        int defaultColor,
        int[] presets,
        Supplier<Integer> getter,
        Consumer<Integer> setter
    ) {
        super(Component.translatable("eclipseui.colorpicker.title"));
        this.parent = parent;
        this.theme = theme;
        this.optionName = optionName;
        this.allowAlpha = allowAlpha;
        this.showHexInput = showHexInput;
        this.defaultColor = sanitizeColor(defaultColor);
        this.presets = presets;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected void init() {
        super.init();
        this.originalColor = sanitizeColor(getter.get());
        this.currentColor = this.originalColor;
        updateHSVFromColor(this.currentColor);
        syncInputsFromColor();
    }

    @Override
    public void tick() {
        super.tick();
        openTicks++;
        cursorBlinkTicks++;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {

        float progress = Mth.clamp((openTicks + delta) / 10f, 0f, 1f);
        float eased = 1f - (1f - progress) * (1f - progress);

        int overlayAlpha = (int) (0xB0 * eased);
        graphics.fill(0, 0, this.width, this.height, (overlayAlpha << 24));

        PanelLayout layout = computeLayout(eased);
        renderPanel(graphics, mouseX, mouseY, layout);
    }

    private void renderPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, PanelLayout layout) {
        int x = layout.panelX;
        int y = layout.panelY;
        int width = layout.panelWidth;
        int height = layout.panelHeight;

        if (theme.useVanillaWidgets()) {
            fillRect(graphics, x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF);
            fillRect(graphics, x, y, width, height, 0xFF000000);
        } else {
            fillRect(graphics, x, y, width, height, theme.backgroundColor());
            drawRect(graphics, x, y, width, height, theme.buttonBorder());
        }

        var font = Minecraft.getInstance().font;
        int titleColor = theme.useVanillaWidgets() ? 0xFFFFFFFF : theme.textPrimary();
        graphics.text(font, this.title, x + 10, y + 8, titleColor, theme.useVanillaWidgets());
        graphics.text(font, optionName, x + 10, y + 20, theme.useVanillaWidgets() ? 0xFFB0B0B0 : theme.textSecondary(), theme.useVanillaWidgets());

        renderSVArea(graphics, layout.svX, layout.svY, layout.svSize);
        renderHueSlider(graphics, layout.hueX, layout.svY, layout.hueWidth, layout.svSize);
        if (allowAlpha) {
            renderAlphaSlider(graphics, layout.alphaX, layout.svY, layout.alphaWidth, layout.svSize);
        }

        fillRect(graphics, layout.previewX, layout.previewY, layout.previewWidth, layout.previewHeight, currentColor);
        drawRect(graphics, layout.previewX, layout.previewY, layout.previewWidth, layout.previewHeight, theme.buttonBorder());

        renderInputField(
            graphics,
            layout.fieldsX,
            layout.fieldsY,
            layout.fieldWidth,
            INPUT_HEIGHT,
            Component.literal("ARGB"),
            argbInput,
            focusedInput == FocusedInput.ARGB
        );
        if (showHexInput) {
            renderInputField(
                graphics,
                layout.fieldsX,
                layout.fieldsY + INPUT_ROW_STEP,
                layout.fieldWidth,
                INPUT_HEIGHT,
                Component.literal("HEX"),
                hexInput,
                focusedInput == FocusedInput.HEX
            );
        }

        renderPresets(graphics, layout.presetsX, layout.presetsY, layout.presetsWidth);
        renderButtons(graphics, layout, mouseX, mouseY);
    }

    private void renderSVArea(GuiGraphicsExtractor graphics, int x, int y, int size) {
        int[] baseRgb = hsvToRgb(hue, 1f, 1f);
        int baseColor = Colors.rgb(baseRgb[0], baseRgb[1], baseRgb[2]);
        graphics.fill(x, y, x + size, y + size, baseColor);

        for (int ix = 0; ix < size; ix++) {
            int alpha = 255 - (ix * 255 / size);
            int overlay = (alpha << 24) | 0xFFFFFF;
            graphics.fill(x + ix, y, x + ix + 1, y + size, overlay);
        }

        for (int iy = 0; iy < size; iy++) {
            int alpha = iy * 255 / size;
            int overlay = (alpha << 24);
            graphics.fill(x, y + iy, x + size, y + iy + 1, overlay);
        }

        drawRect(graphics, x, y, size, size, theme.buttonBorder());

        int cursorX = x + (int) (saturation * size) - 2;
        int cursorY = y + (int) ((1f - value) * size) - 2;
        drawRect(graphics, cursorX, cursorY, 5, 5, 0xFFFFFFFF);
    }

    private void renderHueSlider(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        for (int iy = 0; iy < height; iy++) {
            float h = (iy / (float) height) * 360f;
            int[] rgb = hsvToRgb(h, 1f, 1f);
            graphics.fill(x, y + iy, x + width, y + iy + 1, Colors.rgb(rgb[0], rgb[1], rgb[2]));
        }
        drawRect(graphics, x, y, width, height, theme.buttonBorder());

        int markerY = y + (int) ((hue / 360f) * height) - 1;
        graphics.fill(x - 2, markerY, x + width + 2, markerY + 3, 0xFFFFFFFF);
    }

    private void renderAlphaSlider(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        int rgbOnly = currentColor & 0x00FFFFFF;
        for (int iy = 0; iy < height; iy++) {
            int alpha = 255 - (iy * 255 / height);
            graphics.fill(x, y + iy, x + width, y + iy + 1, (alpha << 24) | rgbOnly);
        }

        drawRect(graphics, x, y, width, height, theme.buttonBorder());

        int alpha = Colors.getAlpha(currentColor);
        int markerY = y + (int) ((1f - alpha * INV_255) * height) - 1;
        graphics.fill(x - 2, markerY, x + width + 2, markerY + 3, 0xFFFFFFFF);
    }

    private void renderInputField(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int width,
        int height,
        Component label,
        String value,
        boolean focused
    ) {
        var font = Minecraft.getInstance().font;
        graphics.text(font, label, x, y - 9, theme.useVanillaWidgets() ? 0xFFB0B0B0 : theme.textSecondary(), theme.useVanillaWidgets());

        fillRect(graphics, x, y, width, height, theme.inputBackground());
        drawRect(graphics, x, y, width, height, focused ? theme.inputBorderFocused() : theme.inputBorder());

        String shown = value;
        int maxPixel = width - 8;
        while (!shown.isEmpty() && font.width(shown) > maxPixel) {
            shown = shown.substring(1);
        }
        graphics.text(font, shown, x + 4, y + 5, theme.textPrimary(), theme.useVanillaWidgets());

        if (focused && cursorBlinkTicks / 6 % 2 == 0) {
            String before = value.substring(0, Math.min(cursorPosition, value.length()));
            int cx = x + 4 + Math.min(font.width(before), maxPixel);
            graphics.fill(cx, y + 3, cx + 1, y + height - 3, theme.textPrimary());
        }
    }

    private void renderPresets(GuiGraphicsExtractor graphics, int x, int y, int width) {
        if (presets == null || presets.length == 0) {
            return;
        }

        int size = 14;
        int spacing = 3;
        int maxSlots = Math.max(1, width / (size + spacing));
        int count = Math.min(presets.length, maxSlots);

        for (int i = 0; i < count; i++) {
            int px = x + i * (size + spacing);
            fillRect(graphics, px, y, size, size, sanitizeColor(presets[i]));
            drawRect(graphics, px, y, size, size, theme.buttonBorder());
        }
    }

    private void renderButtons(GuiGraphicsExtractor graphics, PanelLayout layout, int mouseX, int mouseY) {
        var font = Minecraft.getInstance().font;
        renderButton(graphics, layout.resetX, layout.buttonY, layout.buttonWidth, 16, Component.translatable("eclipseui.button.reset"), mouseX, mouseY);
        renderButton(graphics, layout.cancelX, layout.buttonY, layout.buttonWidth, 16, Component.translatable("eclipseui.confirm.no"), mouseX, mouseY);
        renderButton(graphics, layout.doneX, layout.buttonY, layout.buttonWidth, 16, Component.translatable("eclipseui.button.done"), mouseX, mouseY);

        String summary = showHexInput ? hexInput : argbInput;
        int summaryColor = theme.useVanillaWidgets() ? 0xFFE0E0E0 : theme.textSecondary();
        graphics.text(font, Component.literal(summary), layout.panelX + 10, layout.buttonY + 4, summaryColor, theme.useVanillaWidgets());
    }

    private void renderButton(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Component text, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        int bg = hovered ? theme.buttonBackgroundHover() : theme.buttonBackground();
        fillRect(graphics, x, y, width, height, bg);
        drawRect(graphics, x, y, width, height, theme.buttonBorder());

        var font = Minecraft.getInstance().font;
        int textX = x + (width - font.width(text)) / 2;
        int textY = y + (height - font.lineHeight) / 2;
        graphics.text(font, text, textX, textY, theme.textPrimary(), theme.useVanillaWidgets());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }

        PanelLayout layout = computeLayout(1f);
        int panelLimitX = layout.panelX + layout.panelWidth;
        int panelLimitY = layout.panelY + layout.panelHeight;

        double mouseX = event.x();
        double mouseY = event.y();

        if (mouseX < layout.panelX || mouseX >= panelLimitX || mouseY < layout.panelY || mouseY >= panelLimitY) {
            applyAndClose();
            return true;
        }

        if (inside(mouseX, mouseY, layout.svX, layout.svY, layout.svSize, layout.svSize)) {
            draggingSV = true;
            focusedInput = FocusedInput.NONE;
            updateSV(mouseX, mouseY, layout.svX, layout.svY, layout.svSize);
            return true;
        }

        if (inside(mouseX, mouseY, layout.hueX, layout.svY, layout.hueWidth, layout.svSize)) {
            draggingHue = true;
            focusedInput = FocusedInput.NONE;
            updateHue(mouseY, layout.svY, layout.svSize);
            return true;
        }

        if (allowAlpha && inside(mouseX, mouseY, layout.alphaX, layout.svY, layout.alphaWidth, layout.svSize)) {
            draggingAlpha = true;
            focusedInput = FocusedInput.NONE;
            updateAlpha(mouseY, layout.svY, layout.svSize);
            return true;
        }

        if (inside(mouseX, mouseY, layout.fieldsX, layout.fieldsY, layout.fieldWidth, INPUT_HEIGHT)) {
            focusedInput = FocusedInput.ARGB;
            cursorPosition = argbInput.length();
            return true;
        }

        if (showHexInput && inside(mouseX, mouseY, layout.fieldsX, layout.fieldsY + INPUT_ROW_STEP, layout.fieldWidth, INPUT_HEIGHT)) {
            focusedInput = FocusedInput.HEX;
            cursorPosition = hexInput.length();
            return true;
        }

        if (handlePresetClick(mouseX, mouseY, layout.presetsX, layout.presetsY, layout.presetsWidth)) {
            focusedInput = FocusedInput.NONE;
            return true;
        }

        if (inside(mouseX, mouseY, layout.doneX, layout.buttonY, layout.buttonWidth, 16)) {
            applyAndClose();
            return true;
        }
        if (inside(mouseX, mouseY, layout.cancelX, layout.buttonY, layout.buttonWidth, 16)) {
            cancelAndClose();
            return true;
        }
        if (inside(mouseX, mouseY, layout.resetX, layout.buttonY, layout.buttonWidth, 16)) {
            setColor(defaultColor);
            return true;
        }

        focusedInput = FocusedInput.NONE;
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingSV = false;
        draggingHue = false;
        draggingAlpha = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        PanelLayout layout = computeLayout(1f);

        if (draggingSV) {
            updateSV(event.x(), event.y(), layout.svX, layout.svY, layout.svSize);
            return true;
        }
        if (draggingHue) {
            updateHue(event.y(), layout.svY, layout.svSize);
            return true;
        }
        if (draggingAlpha) {
            updateAlpha(event.y(), layout.svY, layout.svSize);
            return true;
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();

        if (focusedInput != FocusedInput.NONE) {
            if (handleInputKey(keyCode)) {
                return true;
            }
        }

        if (keyCode == 256) {
            cancelAndClose();
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            applyAndClose();
            return true;
        }
        if (keyCode == 258) {
            if (!showHexInput) {
                focusedInput = FocusedInput.ARGB;
                cursorPosition = argbInput.length();
                return true;
            }

            focusedInput = focusedInput == FocusedInput.ARGB ? FocusedInput.HEX : FocusedInput.ARGB;
            cursorPosition = focusedInput == FocusedInput.ARGB ? argbInput.length() : hexInput.length();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (focusedInput == FocusedInput.NONE) {
            return super.charTyped(event);
        }

        char chr = (char) event.codepoint();
        if (focusedInput == FocusedInput.ARGB) {
            if ((chr >= '0' && chr <= '9') || chr == ',' || chr == ' ' || chr == '-') {
                insertChar(chr, true);
                return true;
            }
            return false;
        }

        if (focusedInput == FocusedInput.HEX) {
            if ((chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F') || chr == '#') {
                insertChar(chr, false);
                return true;
            }
            return false;
        }

        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        cancelAndClose();
    }

    private boolean handleInputKey(int keyCode) {
        String text = focusedInput == FocusedInput.ARGB ? argbInput : hexInput;

        if (keyCode == 257 || keyCode == 335) {
            applyFocusedInput();
            return true;
        }
        if (keyCode == 259) {
            if (cursorPosition > 0 && !text.isEmpty()) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
                setFocusedText(text);
            }
            return true;
        }
        if (keyCode == 261) {
            if (cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                setFocusedText(text);
            }
            return true;
        }
        if (keyCode == 263) {
            if (cursorPosition > 0) {
                cursorPosition--;
            }
            return true;
        }
        if (keyCode == 262) {
            if (cursorPosition < text.length()) {
                cursorPosition++;
            }
            return true;
        }

        return false;
    }

    private void insertChar(char chr, boolean argb) {
        String text = argb ? argbInput : hexInput;
        int maxLength = argb ? 32 : (allowAlpha ? 9 : 7);
        if (text.length() >= maxLength) {
            return;
        }

        String updated = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
        cursorPosition++;
        if (argb) {
            argbInput = updated;
        } else {
            hexInput = updated;
        }
    }

    private void setFocusedText(String value) {
        if (focusedInput == FocusedInput.ARGB) {
            argbInput = value;
        } else if (focusedInput == FocusedInput.HEX) {
            hexInput = value;
        }
    }

    private void applyFocusedInput() {
        try {
            if (focusedInput == FocusedInput.ARGB) {
                setColor(parseArgbInput(argbInput));
            } else if (focusedInput == FocusedInput.HEX) {
                setColor(parseHexInput(hexInput));
            }
        } catch (Exception ignored) {
            // Keep invalid input text, but do not update color until valid.
        }
    }

    private int parseArgbInput(String input) {
        String cleaned = input.replace(" ", "");
        String[] parts = cleaned.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("ARGB requires four components");
        }

        int a = Mth.clamp(Integer.parseInt(parts[0]), 0, 255);
        int r = Mth.clamp(Integer.parseInt(parts[1]), 0, 255);
        int g = Mth.clamp(Integer.parseInt(parts[2]), 0, 255);
        int b = Mth.clamp(Integer.parseInt(parts[3]), 0, 255);
        return Colors.argb(allowAlpha ? a : 255, r, g, b);
    }

    private int parseHexInput(String input) {
        int parsed = Colors.fromHex(input);
        return sanitizeColor(parsed);
    }

    private void updateSV(double mouseX, double mouseY, int svX, int svY, int svSize) {
        saturation = Mth.clamp((float) ((mouseX - svX) / svSize), 0f, 1f);
        value = Mth.clamp(1f - (float) ((mouseY - svY) / svSize), 0f, 1f);
        setColor(hsvToColor());
    }

    private void updateHue(double mouseY, int svY, int svSize) {
        hue = Mth.clamp((float) ((mouseY - svY) / svSize) * 360f, 0f, 360f);
        setColor(hsvToColor());
    }

    private void updateAlpha(double mouseY, int svY, int svSize) {
        float t = Mth.clamp((float) ((mouseY - svY) / svSize), 0f, 1f);
        int alpha = 255 - (int) (t * 255f);
        currentColor = (currentColor & 0x00FFFFFF) | (alpha << 24);
        setter.accept(currentColor);
        syncInputsFromColor();
    }

    private void updateHSVFromColor(int color) {
        int r = Colors.getRed(color);
        int g = Colors.getGreen(color);
        int b = Colors.getBlue(color);
        float[] hsv = rgbToHsv(r, g, b);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    private int hsvToColor() {
        int[] rgb = hsvToRgb(hue, saturation, value);
        int alpha = allowAlpha ? Colors.getAlpha(currentColor) : 255;
        return Colors.argb(alpha, rgb[0], rgb[1], rgb[2]);
    }

    private int sanitizeColor(int color) {
        return allowAlpha ? color : ((color & 0x00FFFFFF) | 0xFF000000);
    }

    private void setColor(int color) {
        this.currentColor = sanitizeColor(color);
        setter.accept(this.currentColor);
        updateHSVFromColor(this.currentColor);
        syncInputsFromColor();
    }

    private void syncInputsFromColor() {
        int a = Colors.getAlpha(currentColor);
        int r = Colors.getRed(currentColor);
        int g = Colors.getGreen(currentColor);
        int b = Colors.getBlue(currentColor);

        argbInput = a + ", " + r + ", " + g + ", " + b;
        if (showHexInput) {
            hexInput = allowAlpha ? Colors.toHexWithHash(currentColor) : String.format("#%06X", currentColor & 0x00FFFFFF);
        }
    }

    private boolean handlePresetClick(double mouseX, double mouseY, int x, int y, int width) {
        if (presets == null || presets.length == 0) {
            return false;
        }

        int size = 14;
        int spacing = 3;
        int maxSlots = Math.max(1, width / (size + spacing));
        int count = Math.min(presets.length, maxSlots);

        for (int i = 0; i < count; i++) {
            int px = x + i * (size + spacing);
            if (inside(mouseX, mouseY, px, y, size, size)) {
                setColor(sanitizeColor(presets[i]));
                return true;
            }
        }

        return false;
    }

    private void applyAndClose() {
        setter.accept(currentColor);
        MinecraftScreenCompat.setScreen(this.minecraft, parent);
    }

    private void cancelAndClose() {
        setter.accept(originalColor);
        MinecraftScreenCompat.setScreen(this.minecraft, parent);
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private PanelLayout computeLayout(float eased) {
        int panelMargin = 12;
        int availableWidth = Math.max(220, this.width - panelMargin * 2);
        int availableHeight = Math.max(170, this.height - panelMargin * 2);

        int panelWidth = Math.min(520, availableWidth);
        if (panelWidth < 300 && availableWidth >= 300) {
            panelWidth = 300;
        }
        panelWidth = Math.min(panelWidth, availableWidth);

        int panelHeight = Math.min(268, availableHeight);
        if (panelHeight < 236 && availableHeight >= 236) {
            panelHeight = 236;
        }
        panelHeight = Math.min(panelHeight, availableHeight);

        int panelX = (this.width - panelWidth) / 2;
        int targetPanelY = (this.height - panelHeight) / 2;
        int panelY = targetPanelY + (int) ((1f - eased) * 14f);

        int contentX = panelX + 10;
        int contentY = panelY + 40;
        int contentWidth = panelWidth - 20;

        int rightGap = 12;
        int rightWidth = Mth.clamp(contentWidth / 3, 112, 184);
        int leftMinWidth = allowAlpha ? 96 : 82;
        int maxRightWidth = Math.max(96, contentWidth - rightGap - leftMinWidth);
        rightWidth = Math.min(rightWidth, maxRightWidth);

        int leftWidth = contentWidth - rightGap - rightWidth;
        int sliderGap = 8;
        int hueWidth = 14;
        int alphaWidth = allowAlpha ? 14 : 0;
        int sliderWidth = hueWidth + (allowAlpha ? sliderGap + alphaWidth : 0);
        int fixedLeftWidth = sliderGap + sliderWidth;

        int maxSvByWidth = Math.max(40, leftWidth - fixedLeftWidth);
        int maxSvByHeight = Math.max(40, panelHeight - 96);
        int svSize = Math.min(150, Math.min(maxSvByWidth, maxSvByHeight));
        if (svSize < 70) {
            svSize = Math.max(40, Math.min(maxSvByWidth, maxSvByHeight));
        }

        int svX = contentX;
        int svY = contentY;
        int hueX = svX + svSize + sliderGap;
        int alphaX = hueX + hueWidth + sliderGap;

        int fieldsX = contentX + leftWidth + rightGap;
        int previewWidth = Math.min(56, rightWidth);
        int previewHeight = 34;
        int previewX = fieldsX + (rightWidth - previewWidth) / 2;
        int previewY = contentY + 4;

        int buttonY = panelY + panelHeight - 24;
        int fieldsY = previewY + previewHeight + 12;
        int fieldsHeight = showHexInput ? INPUT_ROW_STEP + INPUT_HEIGHT : INPUT_HEIGHT;
        int maxFieldsY = buttonY - fieldsHeight - 2;
        if (fieldsY > maxFieldsY) {
            fieldsY = Math.max(contentY + 2, maxFieldsY);
            previewY = Math.max(contentY + 2, fieldsY - previewHeight - 8);
        }

        int buttonWidth = 56;
        int buttonGap = 6;
        int doneX = panelX + panelWidth - 10 - buttonWidth;
        int cancelX = doneX - buttonGap - buttonWidth;
        int resetX = cancelX - buttonGap - buttonWidth;

        int presetsX = panelX + 10;
        int presetsY = panelY + panelHeight - 38;
        int presetsWidth = panelWidth - 20;

        return new PanelLayout(
            panelX,
            panelY,
            panelWidth,
            panelHeight,
            svX,
            svY,
            svSize,
            hueX,
            hueWidth,
            alphaX,
            alphaWidth,
            previewX,
            previewY,
            previewWidth,
            previewHeight,
            fieldsX,
            fieldsY,
            rightWidth,
            presetsX,
            presetsY,
            presetsWidth,
            buttonY,
            buttonWidth,
            doneX,
            cancelX,
            resetX
        );
    }

    private static final class PanelLayout {
        final int panelX;
        final int panelY;
        final int panelWidth;
        final int panelHeight;

        final int svX;
        final int svY;
        final int svSize;
        final int hueX;
        final int hueWidth;
        final int alphaX;
        final int alphaWidth;

        final int previewX;
        final int previewY;
        final int previewWidth;
        final int previewHeight;

        final int fieldsX;
        final int fieldsY;
        final int fieldWidth;

        final int presetsX;
        final int presetsY;
        final int presetsWidth;

        final int buttonY;
        final int buttonWidth;
        final int doneX;
        final int cancelX;
        final int resetX;

        PanelLayout(
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight,
            int svX,
            int svY,
            int svSize,
            int hueX,
            int hueWidth,
            int alphaX,
            int alphaWidth,
            int previewX,
            int previewY,
            int previewWidth,
            int previewHeight,
            int fieldsX,
            int fieldsY,
            int fieldWidth,
            int presetsX,
            int presetsY,
            int presetsWidth,
            int buttonY,
            int buttonWidth,
            int doneX,
            int cancelX,
            int resetX
        ) {
            this.panelX = panelX;
            this.panelY = panelY;
            this.panelWidth = panelWidth;
            this.panelHeight = panelHeight;
            this.svX = svX;
            this.svY = svY;
            this.svSize = svSize;
            this.hueX = hueX;
            this.hueWidth = hueWidth;
            this.alphaX = alphaX;
            this.alphaWidth = alphaWidth;
            this.previewX = previewX;
            this.previewY = previewY;
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.fieldsX = fieldsX;
            this.fieldsY = fieldsY;
            this.fieldWidth = fieldWidth;
            this.presetsX = presetsX;
            this.presetsY = presetsY;
            this.presetsWidth = presetsWidth;
            this.buttonY = buttonY;
            this.buttonWidth = buttonWidth;
            this.doneX = doneX;
            this.cancelX = cancelX;
            this.resetX = resetX;
        }
    }

    private float[] rgbToHsv(int r, int g, int b) {
        float rf = r * INV_255;
        float gf = g * INV_255;
        float bf = b * INV_255;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0f;
        float s;
        float v = max;

        if (max != 0f) {
            s = delta / max;
        } else {
            return new float[] {0f, 0f, 0f};
        }

        if (delta != 0f) {
            if (rf == max) {
                h = (gf - bf) / delta;
            } else if (gf == max) {
                h = 2f + (bf - rf) / delta;
            } else {
                h = 4f + (rf - gf) / delta;
            }
            h *= 60f;
            if (h < 0f) {
                h += 360f;
            }
        }

        return new float[] {h, s, v};
    }

    private int[] hsvToRgb(float h, float s, float v) {
        if (s == 0f) {
            int gray = (int) (v * 255f);
            return new int[] {gray, gray, gray};
        }

        float hh = h / 60f;
        int i = (int) hh;
        float f = hh - i;
        float p = v * (1f - s);
        float q = v * (1f - s * f);
        float t = v * (1f - s * (1f - f));

        float r;
        float g;
        float b;

        switch (i % 6) {
            case 0 -> {
                r = v;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = v;
                b = p;
            }
            case 2 -> {
                r = p;
                g = v;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = v;
            }
            case 4 -> {
                r = t;
                g = p;
                b = v;
            }
            default -> {
                r = v;
                g = p;
                b = q;
            }
        }

        return new int[] {(int) (r * 255f), (int) (g * 255f), (int) (b * 255f)};
    }

    private static void fillRect(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    private static void drawRect(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }
}