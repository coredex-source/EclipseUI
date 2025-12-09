package dev.eclipseui.api.component;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builder for toggle (boolean) options.
 */
public interface ToggleBuilder {
    
    /**
     * Set the option name.
     */
    ToggleBuilder name(Component name);
    
    /**
     * Set the option name using a translation key.
     */
    ToggleBuilder name(String translationKey);
    
    /**
     * Set an optional description/tooltip.
     */
    ToggleBuilder description(Component description);
    
    /**
     * Set description using a translation key.
     */
    ToggleBuilder description(String translationKey);
    
    /**
     * Bind to a getter and setter.
     */
    ToggleBuilder binding(Supplier<Boolean> getter, Consumer<Boolean> setter);
    
    /**
     * Set the default value.
     */
    ToggleBuilder defaultValue(boolean defaultValue);
    
    /**
     * Enable/disable live updates (apply immediately without save).
     */
    ToggleBuilder liveUpdate(boolean enabled);
    
    /**
     * Mark if this option requires a game restart.
     */
    ToggleBuilder requiresRestart(boolean required);
    
    /**
     * Set custom text for ON state.
     */
    ToggleBuilder onText(Component text);
    
    /**
     * Set custom text for OFF state.
     */
    ToggleBuilder offText(Component text);
    
    /**
     * Set a callback for when the value changes.
     */
    ToggleBuilder onChange(Consumer<Boolean> callback);
}
