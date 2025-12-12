package dev.eclipseui.api.component;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builder for color picker options.
 */
public interface ColorPickerBuilder {
    
    /**
     * Set the option name.
     */
    ColorPickerBuilder name(Component name);
    
    /**
     * Set the option name using a translation key.
     */
    ColorPickerBuilder name(String translationKey);
    
    /**
     * Set an optional description/tooltip.
     */
    ColorPickerBuilder description(Component description);
    
    /**
     * Set description using a translation key.
     */
    ColorPickerBuilder description(String translationKey);
    
    /**
     * Bind to a getter and setter (color as ARGB int).
     */
    ColorPickerBuilder binding(Supplier<Integer> getter, Consumer<Integer> setter);
    
    /**
     * Set the default color value.
     */
    ColorPickerBuilder defaultValue(int defaultColor);
    
    /**
     * Enable/disable alpha (transparency) editing.
     */
    ColorPickerBuilder allowAlpha(boolean allowed);
    
    /**
     * Enable/disable alpha (transparency) editing.
     * Alias for allowAlpha.
     */
    default ColorPickerBuilder showAlpha(boolean show) {
        return allowAlpha(show);
    }
    
    /**
     * Enable/disable live updates.
     */
    ColorPickerBuilder liveUpdate(boolean enabled);
    
    /**
     * Mark if this option requires a game restart.
     */
    ColorPickerBuilder requiresRestart(boolean required);
    
    /**
     * Set a callback for when the color changes.
     */
    ColorPickerBuilder onChange(Consumer<Integer> callback);
    
    /**
     * Show/hide the hex input field.
     */
    ColorPickerBuilder showHexInput(boolean show);
    
    /**
     * Add preset colors to choose from.
     */
    ColorPickerBuilder presets(int... colors);
}
