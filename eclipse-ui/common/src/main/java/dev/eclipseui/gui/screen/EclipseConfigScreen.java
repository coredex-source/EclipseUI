package dev.eclipseui.gui.screen;

import dev.eclipseui.api.ConfigData;
import dev.eclipseui.api.Theme;
import dev.eclipseui.api.ThemeData;
import dev.eclipseui.gui.theme.ThemeRegistry;
import dev.eclipseui.gui.widget.*;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Main configuration screen for EclipseUI.
 */
public class EclipseConfigScreen extends Screen {
    
    private final @Nullable Screen parent;
    private final ThemeData theme;
    private final @Nullable ConfigData configData;
    private final List<CategoryData> categories = new ArrayList<>();
    private final boolean showSaveButton;
    private final boolean showResetButton;
    private final @Nullable Runnable onSave;
    private final @Nullable Runnable onReset;
    private final @Nullable Runnable onClose;
    
    // Widgets
    private CategoryListWidget categoryList;
    private OptionListWidget optionList;
    private FlatButtonWidget saveButton;
    private FlatButtonWidget resetButton;
    private FlatButtonWidget doneButton;
    
    // Layout
    private static final int SIDEBAR_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 80;
    private static final int PADDING = 8;
    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 40;
    
    private int currentCategory = 0;
    
    public EclipseConfigScreen(
        Component title,
        @Nullable Screen parent,
        Theme themeType,
        @Nullable ThemeData customTheme,
        @Nullable ConfigData configData,
        boolean showSaveButton,
        boolean showResetButton,
        @Nullable Runnable onSave,
        @Nullable Runnable onReset,
        @Nullable Runnable onClose
    ) {
        super(title);
        this.parent = parent;
        this.theme = customTheme != null ? customTheme : ThemeRegistry.get(themeType);
        this.configData = configData;
        this.showSaveButton = showSaveButton;
        this.showResetButton = showResetButton;
        this.onSave = onSave;
        this.onReset = onReset;
        this.onClose = onClose;
    }
    
    public void addCategory(CategoryData category) {
        this.categories.add(category);
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Calculate layout dimensions
        int contentTop = HEADER_HEIGHT;
        int contentBottom = this.height - FOOTER_HEIGHT;
        int contentHeight = contentBottom - contentTop;
        
        // Create category sidebar
        categoryList = new CategoryListWidget(
            new Dim2i(0, contentTop, SIDEBAR_WIDTH, contentHeight),
            theme
        );
        
        for (CategoryData category : categories) {
            categoryList.addCategory(category.name(), category.icon(), category.description());
        }
        
        categoryList.setOnCategorySelected(this::onCategorySelected);
        
        // Create option list
        int optionListX = SIDEBAR_WIDTH + PADDING;
        int optionListWidth = this.width - SIDEBAR_WIDTH - PADDING * 2;
        optionList = new OptionListWidget(
            new Dim2i(optionListX, contentTop, optionListWidth, contentHeight),
            theme
        );
        
        // Populate initial category
        if (!categories.isEmpty()) {
            populateOptions(0);
        }
        
        // Create footer buttons
        int buttonY = this.height - FOOTER_HEIGHT + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2;
        int buttonSpacing = 8;
        
        // Done button (always shown, on the right)
        int doneX = this.width - PADDING - BUTTON_WIDTH;
        doneButton = new FlatButtonWidget(
            new Dim2i(doneX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT),
            theme,
            Component.translatable("eclipseui.button.done")
        );
        doneButton.onClick(btn -> this.onClose());
        
        // Reset button
        if (showResetButton) {
            int resetX = doneX - buttonSpacing - BUTTON_WIDTH;
            resetButton = new FlatButtonWidget(
                new Dim2i(resetX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT),
                theme,
                Component.translatable("eclipseui.button.reset")
            );
            resetButton.onClick(btn -> this.handleReset());
        }
        
        // Save button
        if (showSaveButton) {
            int saveX = (showResetButton ? doneX - buttonSpacing * 2 - BUTTON_WIDTH * 2 : doneX - buttonSpacing - BUTTON_WIDTH);
            saveButton = new FlatButtonWidget(
                new Dim2i(saveX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT),
                theme,
                Component.translatable("eclipseui.button.save")
            );
            saveButton.onClick(btn -> this.handleSave());
        }
    }
    
    private void onCategorySelected(int index) {
        if (index != currentCategory && index >= 0 && index < categories.size()) {
            currentCategory = index;
            populateOptions(index);
        }
    }
    
    private void populateOptions(int categoryIndex) {
        optionList.clearOptions();
        
        if (categoryIndex >= 0 && categoryIndex < categories.size()) {
            CategoryData category = categories.get(categoryIndex);
            for (OptionWidget option : category.options()) {
                optionList.addOption(option);
            }
        }
    }
    
    private void handleSave() {
        if (configData != null) {
            configData.save();
        }
        if (onSave != null) {
            onSave.run();
        }
        
        // Clear modified flags
        for (OptionWidget option : optionList.getOptions()) {
            option.setModified(false);
        }
    }
    
    private void handleReset() {
        optionList.resetAllToDefaults();
        if (onReset != null) {
            onReset.run();
        }
    }
    
    @Override
    public void onClose() {
        // Check for unsaved changes
        if (optionList != null && optionList.hasModifiedOptions()) {
            // Show confirmation dialog
            this.minecraft.setScreen(new ConfirmationScreen(
                this,
                Component.translatable("eclipseui.confirm.unsaved_changes.title"),
                Component.translatable("eclipseui.confirm.unsaved_changes.message"),
                confirmed -> {
                    if (confirmed) {
                        if (onClose != null) {
                            onClose.run();
                        }
                        this.minecraft.setScreen(parent);
                    } else {
                        this.minecraft.setScreen(this);
                    }
                }
            ));
            return;
        }
        
        if (onClose != null) {
            onClose.run();
        }
        this.minecraft.setScreen(parent);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Render blurred background (Minecraft 1.21+ default behavior)
        super.render(graphics, mouseX, mouseY, delta);
        
        // Draw semi-transparent overlay on top of blur
        if (theme.useVanillaWidgets()) {
            // Vanilla style - lighter overlay to let blur show through
            graphics.fill(0, 0, this.width, this.height, 0x80101010);
        } else {
            // Modern style - use theme's semi-transparent background
            graphics.fill(0, 0, this.width, this.height, 0x90000000);
        }
        
        // Draw header
        renderHeader(graphics);
        
        // Draw footer background
        if (theme.useVanillaWidgets()) {
            // Semi-transparent dark overlay for footer
            graphics.fill(0, this.height - FOOTER_HEIGHT, this.width, this.height, 0x80000000);
        } else {
            graphics.fill(0, this.height - FOOTER_HEIGHT, this.width, this.height, theme.backgroundSecondary());
            graphics.fill(0, this.height - FOOTER_HEIGHT, this.width, this.height - FOOTER_HEIGHT + 1, theme.divider());
        }
        
        // Render widgets
        categoryList.render(graphics, mouseX, mouseY, delta);
        optionList.render(graphics, mouseX, mouseY, delta);
        
        if (saveButton != null) {
            saveButton.render(graphics, mouseX, mouseY, delta);
        }
        if (resetButton != null) {
            resetButton.render(graphics, mouseX, mouseY, delta);
        }
        doneButton.render(graphics, mouseX, mouseY, delta);
        
        // Render tooltip for hovered option
        renderOptionTooltip(graphics, mouseX, mouseY);
    }
    
    private void renderHeader(GuiGraphics graphics) {
        if (theme.useVanillaWidgets()) {
            // Semi-transparent dark overlay for header
            graphics.fill(0, 0, this.width, HEADER_HEIGHT, 0x80000000);
        } else {
            // Header background
            graphics.fill(0, 0, this.width, HEADER_HEIGHT, theme.backgroundSecondary());
            graphics.fill(0, HEADER_HEIGHT - 1, this.width, HEADER_HEIGHT, theme.divider());
        }
        
        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, (HEADER_HEIGHT - this.font.lineHeight) / 2, theme.textPrimary());
    }
    
    private void renderOptionTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        // Quick bounds check before iterating
        if (mouseX < SIDEBAR_WIDTH || mouseY < HEADER_HEIGHT || mouseY > this.height - FOOTER_HEIGHT) {
            return;
        }
        
        for (OptionWidget option : optionList.getOptions()) {
            if (option.isHovered() && option.getDescription() != null) {
                // Simple tooltip - draw as wrapped text
                List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(option.getDescription(), 200);
                int tooltipHeight = lines.size() * (this.font.lineHeight + 2);
                int tooltipWidth = 200;
                int x = Math.min(mouseX + 12, this.width - tooltipWidth - 4);
                int y = Math.min(mouseY + 12, this.height - tooltipHeight - 4);
                
                // Background
                graphics.fill(x - 3, y - 3, x + tooltipWidth + 3, y + tooltipHeight + 3, 0xF0100010);
                graphics.fill(x - 4, y - 3, x - 3, y + tooltipHeight + 3, 0x505000FF);
                graphics.fill(x + tooltipWidth + 3, y - 3, x + tooltipWidth + 4, y + tooltipHeight + 3, 0x5028007F);
                graphics.fill(x - 3, y - 4, x + tooltipWidth + 3, y - 3, 0x505000FF);
                graphics.fill(x - 3, y + tooltipHeight + 3, x + tooltipWidth + 3, y + tooltipHeight + 4, 0x5028007F);
                
                // Text
                for (int i = 0; i < lines.size(); i++) {
                    graphics.drawString(this.font, lines.get(i), x, y + i * (this.font.lineHeight + 2), 0xFFFFFFFF);
                }
                break;
            }
        }
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (categoryList.mouseClicked(event, doubleClick)) return true;
        if (optionList.mouseClicked(event, doubleClick)) return true;
        if (saveButton != null && saveButton.mouseClicked(event, doubleClick)) return true;
        if (resetButton != null && resetButton.mouseClicked(event, doubleClick)) return true;
        if (doneButton.mouseClicked(event, doubleClick)) return true;
        return super.mouseClicked(event, doubleClick);
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        optionList.mouseReleased(event);
        return super.mouseReleased(event);
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (optionList.mouseDragged(event, deltaX, deltaY)) return true;
        return super.mouseDragged(event, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (optionList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (optionList.keyPressed(event)) return true;
        if (categoryList.keyPressed(event)) return true;
        return super.keyPressed(event);
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (optionList.charTyped(event)) return true;
        return super.charTyped(event);
    }
    
    /**
     * Data class for a category with its options.
     */
    public record CategoryData(
        Component name,
        @Nullable net.minecraft.resources.ResourceLocation icon,
        @Nullable Component description,
        List<OptionWidget> options
    ) {
        public CategoryData(Component name) {
            this(name, null, null, new ArrayList<>());
        }
        
        public CategoryData(Component name, @Nullable net.minecraft.resources.ResourceLocation icon) {
            this(name, icon, null, new ArrayList<>());
        }
    }
}
