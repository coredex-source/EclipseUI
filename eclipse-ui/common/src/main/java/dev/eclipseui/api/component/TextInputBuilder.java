package dev.eclipseui.api.component;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Builder for text input options.
 */
public interface TextInputBuilder {
    
    /**
     * Set the option name.
     */
    TextInputBuilder name(Component name);
    
    /**
     * Set the option name using a translation key.
     */
    TextInputBuilder name(String translationKey);
    
    /**
     * Set an optional description/tooltip.
     */
    TextInputBuilder description(Component description);
    
    /**
     * Set description using a translation key.
     */
    TextInputBuilder description(String translationKey);
    
    /**
     * Bind to a getter and setter.
     */
    TextInputBuilder binding(Supplier<String> getter, Consumer<String> setter);
    
    /**
     * Set the default value.
     */
    TextInputBuilder defaultValue(String defaultValue);
    
    /**
     * Set placeholder text shown when empty.
     */
    TextInputBuilder placeholder(Component placeholder);
    
    /**
     * Set placeholder using a translation key.
     */
    TextInputBuilder placeholder(String translationKey);
    
    /**
     * Set maximum input length.
     */
    TextInputBuilder maxLength(int maxLength);
    
    /**
     * Set a validation predicate.
     */
    TextInputBuilder validator(Predicate<String> validator);
    
    /**
     * Enable/disable live updates.
     */
    TextInputBuilder liveUpdate(boolean enabled);
    
    /**
     * Mark if this option requires a game restart.
     */
    TextInputBuilder requiresRestart(boolean required);
    
    /**
     * Set a callback for when the value changes.
     */
    TextInputBuilder onChange(Consumer<String> callback);
    
    /**
     * Set error message shown when validation fails.
     */
    TextInputBuilder errorMessage(Component message);
}
