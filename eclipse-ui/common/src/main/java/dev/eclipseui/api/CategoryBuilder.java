package dev.eclipseui.api;

import dev.eclipseui.api.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * Builder for creating config categories.
 */
public interface CategoryBuilder {
    
    /**
     * Set the category name.
     */
    CategoryBuilder name(Component name);
    
    /**
     * Set the category name using a translation key.
     */
    CategoryBuilder name(String translationKey);
    
    /**
     * Set an optional icon for the category.
     */
    CategoryBuilder icon(Identifier icon);
    
    /**
     * Set an optional description/tooltip.
     */
    CategoryBuilder description(Component description);
    
    /**
     * Set description using a translation key.
     */
    CategoryBuilder description(String translationKey);
    
    /**
     * Add a toggle (boolean) option.
     */
    CategoryBuilder toggle(Consumer<ToggleBuilder> builder);
    
    /**
     * Add a slider (numeric) option.
     */
    CategoryBuilder slider(Consumer<SliderBuilder> builder);
    
    /**
     * Add a text input option.
     */
    CategoryBuilder textInput(Consumer<TextInputBuilder> builder);
    
    /**
     * Add a dropdown/enum option.
     */
    <E extends Enum<E>> CategoryBuilder dropdown(Consumer<DropdownBuilder<E>> builder);
    
    /**
     * Add a color picker option.
     */
    CategoryBuilder colorPicker(Consumer<ColorPickerBuilder> builder);
    
    /**
     * Add a visual separator.
     */
    CategoryBuilder separator();
    
    /**
     * Add a label/header text.
     */
    CategoryBuilder label(Component text);
    
    /**
     * Add a label using a translation key.
     */
    CategoryBuilder label(String translationKey);
}
