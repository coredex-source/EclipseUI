package dev.eclipseui.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * A simple confirmation dialog screen.
 */
public class ConfirmationScreen extends Screen {
    
    private final Screen parent;
    private final Component message;
    private final Consumer<Boolean> callback;
    
    private static final int DIALOG_WIDTH = 250;
    private static final int DIALOG_HEIGHT = 100;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 10;
    
    public ConfirmationScreen(Screen parent, Component title, Component message, Consumer<Boolean> callback) {
        super(title);
        this.parent = parent;
        this.message = message;
        this.callback = callback;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int dialogX = (this.width - DIALOG_WIDTH) / 2;
        int dialogY = (this.height - DIALOG_HEIGHT) / 2;
        
        int buttonY = dialogY + DIALOG_HEIGHT - BUTTON_HEIGHT - PADDING;
        int buttonSpacing = 10;
        int totalButtonWidth = 2 * BUTTON_WIDTH + buttonSpacing;
        int buttonStartX = dialogX + (DIALOG_WIDTH - totalButtonWidth) / 2;
        
        // Confirm button
        this.addRenderableWidget(Button.builder(
            Component.translatable("eclipseui.confirm.yes"),
            button -> this.callback.accept(true)
        ).bounds(buttonStartX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        // Cancel button
        this.addRenderableWidget(Button.builder(
            Component.translatable("eclipseui.confirm.no"),
            button -> this.callback.accept(false)
        ).bounds(buttonStartX + BUTTON_WIDTH + buttonSpacing, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Dim background
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        int dialogX = (this.width - DIALOG_WIDTH) / 2;
        int dialogY = (this.height - DIALOG_HEIGHT) / 2;
        
        // Dialog background
        graphics.fill(dialogX, dialogY, dialogX + DIALOG_WIDTH, dialogY + DIALOG_HEIGHT, 0xFF1E1E1E);
        
        // Dialog border
        graphics.fill(dialogX, dialogY, dialogX + DIALOG_WIDTH, dialogY + 1, 0xFF3E3E3E);
        graphics.fill(dialogX, dialogY + DIALOG_HEIGHT - 1, dialogX + DIALOG_WIDTH, dialogY + DIALOG_HEIGHT, 0xFF3E3E3E);
        graphics.fill(dialogX, dialogY, dialogX + 1, dialogY + DIALOG_HEIGHT, 0xFF3E3E3E);
        graphics.fill(dialogX + DIALOG_WIDTH - 1, dialogY, dialogX + DIALOG_WIDTH, dialogY + DIALOG_HEIGHT, 0xFF3E3E3E);
        
        // Title
        var font = Minecraft.getInstance().font;
        int titleX = dialogX + (DIALOG_WIDTH - font.width(this.title)) / 2;
        int titleY = dialogY + PADDING;
        graphics.drawString(font, this.title, titleX, titleY, 0xFFFFFFFF, false);
        
        // Message
        int messageY = titleY + font.lineHeight + 8;
        int messageX = dialogX + (DIALOG_WIDTH - font.width(this.message)) / 2;
        graphics.drawString(font, this.message, messageX, messageY, 0xFFAAAAAA, false);
        
        // Render buttons
        super.render(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    public void onClose() {
        // Treat closing as cancellation
        this.callback.accept(false);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
