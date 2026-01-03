package dev.eclipseui.impl;

import dev.eclipseui.api.*;
import dev.eclipseui.api.component.*;
import dev.eclipseui.gui.screen.EclipseConfigScreen;
import dev.eclipseui.gui.theme.ThemeRegistry;
import dev.eclipseui.gui.widget.*;
import dev.eclipseui.util.Dim2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of ConfigScreenBuilder.
 */
public class ConfigScreenBuilderImpl implements ConfigScreenBuilder {
    
    private Component title = Component.literal("Configuration");
    private @Nullable Screen parent;
    private Theme theme = Theme.MODERN;
    private @Nullable ThemeData customTheme;
    private @Nullable ConfigData config;
    private final List<CategoryBuilderImpl> categories = new ArrayList<>();
    private @Nullable Runnable onSave;
    private @Nullable Runnable onReset;
    private @Nullable Runnable onClose;
    private boolean saveButton = true;
    private boolean resetButton = true;
    
    @Override
    public ConfigScreenBuilder title(Component title) {
        this.title = title;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder title(String translationKey) {
        this.title = Component.translatable(translationKey);
        return this;
    }
    
    @Override
    public ConfigScreenBuilder parent(Screen parent) {
        this.parent = parent;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder theme(Theme theme) {
        this.theme = theme;
        this.customTheme = null;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder theme(ThemeData themeData) {
        this.theme = Theme.CUSTOM;
        this.customTheme = themeData;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder config(ConfigData config) {
        this.config = config;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder category(Consumer<CategoryBuilder> categoryBuilder) {
        CategoryBuilderImpl builder = new CategoryBuilderImpl();
        categoryBuilder.accept(builder);
        categories.add(builder);
        return this;
    }
    
    @Override
    public ConfigScreenBuilder onSave(Runnable callback) {
        this.onSave = callback;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder onReset(Runnable callback) {
        this.onReset = callback;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder onClose(Runnable callback) {
        this.onClose = callback;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder saveButton(boolean enabled) {
        this.saveButton = enabled;
        return this;
    }
    
    @Override
    public ConfigScreenBuilder resetButton(boolean enabled) {
        this.resetButton = enabled;
        return this;
    }
    
    @Override
    public Screen build() {
        ThemeData themeData = customTheme != null ? customTheme : ThemeRegistry.get(theme);
        
        EclipseConfigScreen screen = new EclipseConfigScreen(
            title,
            parent,
            theme,
            customTheme,
            config,
            saveButton,
            resetButton,
            onSave,
            onReset,
            onClose
        );
        
        // Build and add categories
        for (CategoryBuilderImpl categoryBuilder : categories) {
            EclipseConfigScreen.CategoryData categoryData = categoryBuilder.build(themeData);
            screen.addCategory(categoryData);
        }
        
        return screen;
    }
    
    /**
     * Implementation of CategoryBuilder.
     */
    public static class CategoryBuilderImpl implements CategoryBuilder {
        
        private Component name = Component.literal("Category");
        private @Nullable ResourceLocation icon;
        private @Nullable Component description;
        private final List<OptionBuilderData> options = new ArrayList<>();
        
        @Override
        public CategoryBuilder name(Component name) {
            this.name = name;
            return this;
        }
        
        @Override
        public CategoryBuilder name(String translationKey) {
            this.name = Component.translatable(translationKey);
            return this;
        }
        
        @Override
        public CategoryBuilder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }
        
        @Override
        public CategoryBuilder description(Component description) {
            this.description = description;
            return this;
        }
        
        @Override
        public CategoryBuilder description(String translationKey) {
            this.description = Component.translatable(translationKey);
            return this;
        }
        
        @Override
        public CategoryBuilder toggle(Consumer<ToggleBuilder> builder) {
            ToggleBuilderImpl impl = new ToggleBuilderImpl();
            builder.accept(impl);
            options.add(impl);
            return this;
        }
        
        @Override
        public CategoryBuilder slider(Consumer<SliderBuilder> builder) {
            SliderBuilderImpl impl = new SliderBuilderImpl();
            builder.accept(impl);
            options.add(impl);
            return this;
        }
        
        @Override
        public CategoryBuilder textInput(Consumer<TextInputBuilder> builder) {
            TextInputBuilderImpl impl = new TextInputBuilderImpl();
            builder.accept(impl);
            options.add(impl);
            return this;
        }
        
        @Override
        public <E extends Enum<E>> CategoryBuilder dropdown(Consumer<DropdownBuilder<E>> builder) {
            DropdownBuilderImpl<E> impl = new DropdownBuilderImpl<>();
            builder.accept(impl);
            options.add(impl);
            return this;
        }
        
        @Override
        public CategoryBuilder colorPicker(Consumer<ColorPickerBuilder> builder) {
            ColorPickerBuilderImpl impl = new ColorPickerBuilderImpl();
            builder.accept(impl);
            options.add(impl);
            return this;
        }
        
        @Override
        public CategoryBuilder separator() {
            options.add(new SeparatorData());
            return this;
        }
        
        @Override
        public CategoryBuilder label(Component text) {
            options.add(new LabelData(text));
            return this;
        }
        
        @Override
        public CategoryBuilder label(String translationKey) {
            return label(Component.translatable(translationKey));
        }
        
        public EclipseConfigScreen.CategoryData build(ThemeData theme) {
            List<OptionWidget> widgets = new ArrayList<>();
            Dim2i defaultDim = new Dim2i(0, 0, 300, 20);
            
            for (OptionBuilderData optionData : options) {
                OptionWidget widget = optionData.build(defaultDim, theme);
                if (widget != null) {
                    widgets.add(widget);
                }
            }
            
            return new EclipseConfigScreen.CategoryData(name, icon, description, widgets);
        }
    }
    
    // Interface for option builders
    interface OptionBuilderData {
        OptionWidget build(Dim2i dim, ThemeData theme);
    }
    
    // Toggle builder implementation
    static class ToggleBuilderImpl implements ToggleBuilder, OptionBuilderData {
        private Component name = Component.empty();
        private @Nullable Component description;
        private Supplier<Boolean> getter = () -> false;
        private Consumer<Boolean> setter = v -> {};
        private boolean defaultValue = false;
        private boolean liveUpdate = false;
        private boolean requiresRestart = false;
        private Component onText = Component.translatable("eclipseui.toggle.on");
        private Component offText = Component.translatable("eclipseui.toggle.off");
        private @Nullable Consumer<Boolean> onChange;
        
        @Override
        public ToggleBuilder name(Component name) { this.name = name; return this; }
        @Override
        public ToggleBuilder name(String key) { this.name = Component.translatable(key); return this; }
        @Override
        public ToggleBuilder description(Component desc) { this.description = desc; return this; }
        @Override
        public ToggleBuilder description(String key) { this.description = Component.translatable(key); return this; }
        @Override
        public ToggleBuilder binding(Supplier<Boolean> getter, Consumer<Boolean> setter) {
            this.getter = getter; this.setter = setter; return this;
        }
        @Override
        public ToggleBuilder defaultValue(boolean val) { this.defaultValue = val; return this; }
        @Override
        public ToggleBuilder liveUpdate(boolean enabled) { this.liveUpdate = enabled; return this; }
        @Override
        public ToggleBuilder requiresRestart(boolean required) { this.requiresRestart = required; return this; }
        @Override
        public ToggleBuilder onText(Component text) { this.onText = text; return this; }
        @Override
        public ToggleBuilder offText(Component text) { this.offText = text; return this; }
        @Override
        public ToggleBuilder onChange(Consumer<Boolean> callback) { this.onChange = callback; return this; }
        
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            ToggleWidget widget = new ToggleWidget(dim, theme, name)
                .binding(getter, setter)
                .defaultValue(defaultValue)
                .liveUpdate(liveUpdate)
                .onText(onText)
                .offText(offText);
            
            if (onChange != null) widget.onChange(onChange);
            widget.setDescription(description);
            widget.setRequiresRestart(requiresRestart);
            return widget;
        }
    }
    
    // Slider builder implementation
    static class SliderBuilderImpl implements SliderBuilder, OptionBuilderData {
        private Component name = Component.empty();
        private @Nullable Component description;
        private Supplier<Double> getter = () -> 0.0;
        private Consumer<Double> setter = v -> {};
        private double defaultValue = 0;
        private double min = 0;
        private double max = 100;
        private double step = 1;
        private boolean liveUpdate = false;
        private boolean requiresRestart = false;
        private @Nullable Function<Double, Component> formatter;
        private boolean showValue = true;
        private String suffix = "";
        private @Nullable Consumer<Double> onChange;
        
        @Override
        public SliderBuilder name(Component name) { this.name = name; return this; }
        @Override
        public SliderBuilder name(String key) { this.name = Component.translatable(key); return this; }
        @Override
        public SliderBuilder description(Component desc) { this.description = desc; return this; }
        @Override
        public SliderBuilder description(String key) { this.description = Component.translatable(key); return this; }
        @Override
        public SliderBuilder min(double min) { this.min = min; return this; }
        @Override
        public SliderBuilder max(double max) { this.max = max; return this; }
        @Override
        public SliderBuilder step(double step) { this.step = step; return this; }
        @Override
        public SliderBuilder range(double min, double max, double step) {
            this.min = min; this.max = max; this.step = step; return this;
        }
        @Override
        public SliderBuilder bindingInt(Supplier<Integer> getter, Consumer<Integer> setter) {
            this.getter = () -> getter.get().doubleValue();
            this.setter = v -> setter.accept(v.intValue());
            return this;
        }
        @Override
        public SliderBuilder bindingDouble(Supplier<Double> getter, Consumer<Double> setter) {
            this.getter = getter; this.setter = setter; return this;
        }
        @Override
        public SliderBuilder bindingFloat(Supplier<Float> getter, Consumer<Float> setter) {
            this.getter = () -> getter.get().doubleValue();
            this.setter = v -> setter.accept(v.floatValue());
            return this;
        }
        @Override
        public SliderBuilder defaultValue(double val) { this.defaultValue = val; return this; }
        @Override
        public SliderBuilder liveUpdate(boolean enabled) { this.liveUpdate = enabled; return this; }
        @Override
        public SliderBuilder requiresRestart(boolean required) { this.requiresRestart = required; return this; }
        @Override
        public SliderBuilder formatter(Function<Double, Component> fmt) { this.formatter = fmt; return this; }
        @Override
        public SliderBuilder showValue(boolean show) { this.showValue = show; return this; }
        @Override
        public SliderBuilder suffix(String suffix) { this.suffix = suffix; return this; }
        @Override
        public SliderBuilder percentageFormat() { 
            this.formatter = v -> Component.literal(String.format("%.0f%%", v * 100));
            return this;
        }
        @Override
        public SliderBuilder onChange(Consumer<Double> callback) { this.onChange = callback; return this; }
        
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            SliderWidget widget = new SliderWidget(dim, theme, name)
                .bindingDouble(getter, setter)
                .range(min, max, step)
                .defaultValue(defaultValue)
                .liveUpdate(liveUpdate)
                .showValue(showValue)
                .suffix(suffix);
            
            if (formatter != null) widget.formatter(formatter);
            if (onChange != null) widget.onChange(onChange);
            widget.setDescription(description);
            widget.setRequiresRestart(requiresRestart);
            return widget;
        }
    }
    
    // Text input builder implementation
    static class TextInputBuilderImpl implements TextInputBuilder, OptionBuilderData {
        private Component name = Component.empty();
        private @Nullable Component description;
        private Supplier<String> getter = () -> "";
        private Consumer<String> setter = v -> {};
        private String defaultValue = "";
        private @Nullable Component placeholder;
        private int maxLength = 256;
        private @Nullable Predicate<String> validator;
        private boolean liveUpdate = false;
        private boolean requiresRestart = false;
        private @Nullable Consumer<String> onChange;
        private @Nullable Component errorMessage;
        
        @Override
        public TextInputBuilder name(Component name) { this.name = name; return this; }
        @Override
        public TextInputBuilder name(String key) { this.name = Component.translatable(key); return this; }
        @Override
        public TextInputBuilder description(Component desc) { this.description = desc; return this; }
        @Override
        public TextInputBuilder description(String key) { this.description = Component.translatable(key); return this; }
        @Override
        public TextInputBuilder binding(Supplier<String> getter, Consumer<String> setter) {
            this.getter = getter; this.setter = setter; return this;
        }
        @Override
        public TextInputBuilder defaultValue(String val) { this.defaultValue = val; return this; }
        @Override
        public TextInputBuilder placeholder(Component placeholder) { this.placeholder = placeholder; return this; }
        @Override
        public TextInputBuilder placeholder(String key) { this.placeholder = Component.translatable(key); return this; }
        @Override
        public TextInputBuilder maxLength(int max) { this.maxLength = max; return this; }
        @Override
        public TextInputBuilder validator(Predicate<String> validator) { this.validator = validator; return this; }
        @Override
        public TextInputBuilder liveUpdate(boolean enabled) { this.liveUpdate = enabled; return this; }
        @Override
        public TextInputBuilder requiresRestart(boolean required) { this.requiresRestart = required; return this; }
        @Override
        public TextInputBuilder onChange(Consumer<String> callback) { this.onChange = callback; return this; }
        @Override
        public TextInputBuilder errorMessage(Component msg) { this.errorMessage = msg; return this; }
        
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            TextFieldWidget widget = new TextFieldWidget(dim, theme, name)
                .binding(getter, setter)
                .defaultValue(defaultValue)
                .maxLength(maxLength)
                .liveUpdate(liveUpdate);
            
            if (placeholder != null) widget.placeholder(placeholder);
            if (validator != null) widget.validator(validator);
            if (onChange != null) widget.onChange(onChange);
            if (errorMessage != null) widget.errorMessage(errorMessage);
            widget.setDescription(description);
            widget.setRequiresRestart(requiresRestart);
            return widget;
        }
    }
    
    // Dropdown builder implementation
    static class DropdownBuilderImpl<E extends Enum<E>> implements DropdownBuilder<E>, OptionBuilderData {
        private Component name = Component.empty();
        private @Nullable Component description;
        private Class<E> enumClass;
        private Supplier<E> getter;
        private Consumer<E> setter;
        private E defaultValue;
        private @Nullable Function<E, Component> formatter;
        private boolean liveUpdate = false;
        private boolean requiresRestart = false;
        private @Nullable Consumer<E> onChange;
        
        @Override
        public DropdownBuilder<E> name(Component name) { this.name = name; return this; }
        @Override
        public DropdownBuilder<E> name(String key) { this.name = Component.translatable(key); return this; }
        @Override
        public DropdownBuilder<E> description(Component desc) { this.description = desc; return this; }
        @Override
        public DropdownBuilder<E> description(String key) { this.description = Component.translatable(key); return this; }
        @Override
        public DropdownBuilder<E> enumClass(Class<E> enumClass) { this.enumClass = enumClass; return this; }
        @Override
        public DropdownBuilder<E> binding(Supplier<E> getter, Consumer<E> setter) {
            this.getter = getter; this.setter = setter; return this;
        }
        @Override
        public DropdownBuilder<E> defaultValue(E val) { this.defaultValue = val; return this; }
        @Override
        public DropdownBuilder<E> formatter(Function<E, Component> fmt) { this.formatter = fmt; return this; }
        @Override
        public DropdownBuilder<E> liveUpdate(boolean enabled) { this.liveUpdate = enabled; return this; }
        @Override
        public DropdownBuilder<E> requiresRestart(boolean required) { this.requiresRestart = required; return this; }
        @Override
        public DropdownBuilder<E> onChange(Consumer<E> callback) { this.onChange = callback; return this; }
        
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            DropdownWidget<E> widget = new DropdownWidget<E>(dim, theme, name)
                .enumClass(enumClass)
                .binding(getter, setter)
                .defaultValue(defaultValue)
                .liveUpdate(liveUpdate);
            
            if (formatter != null) widget.formatter(formatter);
            if (onChange != null) widget.onChange(onChange);
            widget.setDescription(description);
            widget.setRequiresRestart(requiresRestart);
            return widget;
        }
    }
    
    // Color picker builder implementation
    static class ColorPickerBuilderImpl implements ColorPickerBuilder, OptionBuilderData {
        private Component name = Component.empty();
        private @Nullable Component description;
        private Supplier<Integer> getter = () -> 0xFFFFFFFF;
        private Consumer<Integer> setter = v -> {};
        private int defaultValue = 0xFFFFFFFF;
        private boolean allowAlpha = false;
        private boolean liveUpdate = false;
        private boolean requiresRestart = false;
        private @Nullable Consumer<Integer> onChange;
        private boolean showHexInput = true;
        private int[] presets;
        
        @Override
        public ColorPickerBuilder name(Component name) { this.name = name; return this; }
        @Override
        public ColorPickerBuilder name(String key) { this.name = Component.translatable(key); return this; }
        @Override
        public ColorPickerBuilder description(Component desc) { this.description = desc; return this; }
        @Override
        public ColorPickerBuilder description(String key) { this.description = Component.translatable(key); return this; }
        @Override
        public ColorPickerBuilder binding(Supplier<Integer> getter, Consumer<Integer> setter) {
            this.getter = getter; this.setter = setter; return this;
        }
        @Override
        public ColorPickerBuilder defaultValue(int val) { this.defaultValue = val; return this; }
        @Override
        public ColorPickerBuilder allowAlpha(boolean allowed) { this.allowAlpha = allowed; return this; }
        @Override
        public ColorPickerBuilder liveUpdate(boolean enabled) { this.liveUpdate = enabled; return this; }
        @Override
        public ColorPickerBuilder requiresRestart(boolean required) { this.requiresRestart = required; return this; }
        @Override
        public ColorPickerBuilder onChange(Consumer<Integer> callback) { this.onChange = callback; return this; }
        @Override
        public ColorPickerBuilder showHexInput(boolean show) { this.showHexInput = show; return this; }
        @Override
        public ColorPickerBuilder presets(int... colors) { this.presets = colors; return this; }
        
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            ColorPickerWidget widget = new ColorPickerWidget(dim, theme, name)
                .binding(getter, setter)
                .defaultValue(defaultValue)
                .allowAlpha(allowAlpha)
                .liveUpdate(liveUpdate)
                .showHexInput(showHexInput);
            
            if (presets != null) widget.presets(presets);
            if (onChange != null) widget.onChange(onChange);
            widget.setDescription(description);
            widget.setRequiresRestart(requiresRestart);
            return widget;
        }
    }
    
    // Separator placeholder
    record SeparatorData() implements OptionBuilderData {
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            return new SeparatorWidget(dim, theme);
        }
    }
    
    // Label placeholder
    record LabelData(Component text) implements OptionBuilderData {
        @Override
        public OptionWidget build(Dim2i dim, ThemeData theme) {
            return new LabelWidget(dim, theme, text);
        }
    }
}
