package dev.eclipseui.api.component;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for slider (numeric) options.
 */
public interface SliderBuilder {
    
    /**
     * Set the option name.
     */
    SliderBuilder name(Component name);
    
    /**
     * Set the option name using a translation key.
     */
    SliderBuilder name(String translationKey);
    
    /**
     * Set an optional description/tooltip.
     */
    SliderBuilder description(Component description);
    
    /**
     * Set description using a translation key.
     */
    SliderBuilder description(String translationKey);
    
    /**
     * Set the minimum value.
     */
    SliderBuilder min(double min);
    
    /**
     * Set the maximum value.
     */
    SliderBuilder max(double max);
    
    /**
     * Set the step/increment value.
     */
    SliderBuilder step(double step);
    
    /**
     * Configure range in one call.
     */
    SliderBuilder range(double min, double max, double step);
    
    /**
     * Bind to a getter and setter for integer values.
     */
    SliderBuilder bindingInt(Supplier<Integer> getter, Consumer<Integer> setter);
    
    /**
     * Bind to a getter and setter for double values.
     */
    SliderBuilder bindingDouble(Supplier<Double> getter, Consumer<Double> setter);
    
    /**
     * Bind to a getter and setter for float values.
     */
    SliderBuilder bindingFloat(Supplier<Float> getter, Consumer<Float> setter);
    
    /**
     * Set the default value.
     */
    SliderBuilder defaultValue(double defaultValue);
    
    /**
     * Enable/disable live updates.
     */
    SliderBuilder liveUpdate(boolean enabled);
    
    /**
     * Mark if this option requires a game restart.
     */
    SliderBuilder requiresRestart(boolean required);
    
    /**
     * Set a custom value formatter for display.
     */
    SliderBuilder formatter(Function<Double, Component> formatter);
    
    /**
     * Show the numeric value next to the slider.
     */
    SliderBuilder showValue(boolean show);
    
    /**
     * Set a suffix for the value display (e.g., "%", "px").
     */
    SliderBuilder suffix(String suffix);
    
    /**
     * Format the value as a percentage (0-100 range displayed as 0%-100%).
     */
    SliderBuilder percentageFormat();
    
    /**
     * Set a callback for when the value changes.
     */
    SliderBuilder onChange(Consumer<Double> callback);
}
