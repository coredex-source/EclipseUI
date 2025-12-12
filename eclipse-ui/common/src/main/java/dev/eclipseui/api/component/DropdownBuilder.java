package dev.eclipseui.api.component;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for dropdown/enum options.
 */
public interface DropdownBuilder<E extends Enum<E>> {
    
    /**
     * Set the option name.
     */
    DropdownBuilder<E> name(Component name);
    
    /**
     * Set the option name using a translation key.
     */
    DropdownBuilder<E> name(String translationKey);
    
    /**
     * Set an optional description/tooltip.
     */
    DropdownBuilder<E> description(Component description);
    
    /**
     * Set description using a translation key.
     */
    DropdownBuilder<E> description(String translationKey);
    
    /**
     * Set the enum class for available values.
     */
    DropdownBuilder<E> enumClass(Class<E> enumClass);
    
    /**
     * Bind to a getter and setter.
     */
    DropdownBuilder<E> binding(Supplier<E> getter, Consumer<E> setter);
    
    /**
     * Set the default value.
     */
    DropdownBuilder<E> defaultValue(E defaultValue);
    
    /**
     * Set a custom formatter for enum values.
     */
    DropdownBuilder<E> formatter(Function<E, Component> formatter);
    
    /**
     * Enable/disable live updates.
     */
    DropdownBuilder<E> liveUpdate(boolean enabled);
    
    /**
     * Mark if this option requires a game restart.
     */
    DropdownBuilder<E> requiresRestart(boolean required);
    
    /**
     * Set a callback for when the value changes.
     */
    DropdownBuilder<E> onChange(Consumer<E> callback);
}
