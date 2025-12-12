package dev.eclipseui.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Builder for creating configuration screens.
 */
public interface ConfigScreenBuilder {
    
    /**
     * Set the title of the config screen.
     */
    ConfigScreenBuilder title(Component title);
    
    /**
     * Set the title using a translation key.
     */
    ConfigScreenBuilder title(String translationKey);
    
    /**
     * Set the parent screen to return to when closing.
     */
    ConfigScreenBuilder parent(Screen parent);
    
    /**
     * Set the visual theme.
     */
    ConfigScreenBuilder theme(Theme theme);
    
    /**
     * Set a custom theme.
     */
    ConfigScreenBuilder theme(ThemeData themeData);
    
    /**
     * Set the config data handler.
     */
    ConfigScreenBuilder config(ConfigData config);
    
    /**
     * Add a category to the config screen.
     */
    ConfigScreenBuilder category(Consumer<CategoryBuilder> categoryBuilder);
    
    /**
     * Set callback for when save button is clicked.
     */
    ConfigScreenBuilder onSave(Runnable callback);
    
    /**
     * Set callback for when reset button is clicked.
     */
    ConfigScreenBuilder onReset(Runnable callback);
    
    /**
     * Set callback for when screen is closed.
     */
    ConfigScreenBuilder onClose(Runnable callback);
    
    /**
     * Enable/disable the save button.
     */
    ConfigScreenBuilder saveButton(boolean enabled);
    
    /**
     * Enable/disable the reset button.
     */
    ConfigScreenBuilder resetButton(boolean enabled);
    
    /**
     * Build and return the configured screen.
     */
    Screen build();
}
