package dev.eclipseui.api;

/**
 * Holds all color and styling data for a theme.
 */
public record ThemeData(
    // Style flag - true for vanilla MC style, false for modern flat style
    boolean useVanillaWidgets,
    
    // Background
    int backgroundColor,
    int backgroundSecondary,
    
    // Text
    int textPrimary,
    int textSecondary,
    int textDisabled,
    
    // Buttons
    int buttonBackground,
    int buttonBackgroundHover,
    int buttonBackgroundDisabled,
    int buttonBorder,
    
    // Accent colors
    int accentPrimary,
    int accentSecondary,
    
    // Toggle specific
    int toggleOn,
    int toggleOff,
    int toggleHandle,
    
    // Slider specific
    int sliderTrack,
    int sliderFilled,
    int sliderHandle,
    
    // Input specific
    int inputBackground,
    int inputBorder,
    int inputBorderFocused,
    
    // Scrollbar
    int scrollbarTrack,
    int scrollbarThumb,
    int scrollbarThumbHover,
    
    // Category list
    int categoryBackground,
    int categorySelected,
    int categoryHover,
    
    // Misc
    int tooltipBackground,
    int tooltipBorder,
    int divider,
    int shadow
) {
    /**
     * Builder for creating custom ThemeData.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        // Style flag
        private boolean useVanillaWidgets = false;
        
        // Defaults based on Modern theme
        private int backgroundColor = 0xFF1a1a1a;
        private int backgroundSecondary = 0xFF2d2d2d;
        private int textPrimary = 0xFFffffff;
        private int textSecondary = 0xFFaaaaaa;
        private int textDisabled = 0xFF666666;
        private int buttonBackground = 0xFF3a3a3a;
        private int buttonBackgroundHover = 0xFF4a4a4a;
        private int buttonBackgroundDisabled = 0xFF2a2a2a;
        private int buttonBorder = 0xFF5a5a5a;
        private int accentPrimary = 0xFF00bcd4;
        private int accentSecondary = 0xFF0097a7;
        private int toggleOn = 0xFF00bcd4;
        private int toggleOff = 0xFF5a5a5a;
        private int toggleHandle = 0xFFffffff;
        private int sliderTrack = 0xFF3a3a3a;
        private int sliderFilled = 0xFF00bcd4;
        private int sliderHandle = 0xFFffffff;
        private int inputBackground = 0xFF2a2a2a;
        private int inputBorder = 0xFF4a4a4a;
        private int inputBorderFocused = 0xFF00bcd4;
        private int scrollbarTrack = 0xFF2a2a2a;
        private int scrollbarThumb = 0xFF5a5a5a;
        private int scrollbarThumbHover = 0xFF7a7a7a;
        private int categoryBackground = 0xFF252525;
        private int categorySelected = 0xFF00bcd4;
        private int categoryHover = 0xFF353535;
        private int tooltipBackground = 0xF0100010;
        private int tooltipBorder = 0xFF5000ff;
        private int divider = 0xFF3a3a3a;
        private int shadow = 0x80000000;
        
        public Builder useVanillaWidgets(boolean useVanilla) { this.useVanillaWidgets = useVanilla; return this; }
        public Builder backgroundColor(int color) { this.backgroundColor = color; return this; }
        public Builder backgroundSecondary(int color) { this.backgroundSecondary = color; return this; }
        public Builder textPrimary(int color) { this.textPrimary = color; return this; }
        public Builder textSecondary(int color) { this.textSecondary = color; return this; }
        public Builder textDisabled(int color) { this.textDisabled = color; return this; }
        public Builder buttonBackground(int color) { this.buttonBackground = color; return this; }
        public Builder buttonBackgroundHover(int color) { this.buttonBackgroundHover = color; return this; }
        public Builder buttonBackgroundDisabled(int color) { this.buttonBackgroundDisabled = color; return this; }
        public Builder buttonBorder(int color) { this.buttonBorder = color; return this; }
        public Builder accentPrimary(int color) { this.accentPrimary = color; return this; }
        public Builder accentSecondary(int color) { this.accentSecondary = color; return this; }
        public Builder toggleOn(int color) { this.toggleOn = color; return this; }
        public Builder toggleOff(int color) { this.toggleOff = color; return this; }
        public Builder toggleHandle(int color) { this.toggleHandle = color; return this; }
        public Builder sliderTrack(int color) { this.sliderTrack = color; return this; }
        public Builder sliderFilled(int color) { this.sliderFilled = color; return this; }
        public Builder sliderHandle(int color) { this.sliderHandle = color; return this; }
        public Builder inputBackground(int color) { this.inputBackground = color; return this; }
        public Builder inputBorder(int color) { this.inputBorder = color; return this; }
        public Builder inputBorderFocused(int color) { this.inputBorderFocused = color; return this; }
        public Builder scrollbarTrack(int color) { this.scrollbarTrack = color; return this; }
        public Builder scrollbarThumb(int color) { this.scrollbarThumb = color; return this; }
        public Builder scrollbarThumbHover(int color) { this.scrollbarThumbHover = color; return this; }
        public Builder categoryBackground(int color) { this.categoryBackground = color; return this; }
        public Builder categorySelected(int color) { this.categorySelected = color; return this; }
        public Builder categoryHover(int color) { this.categoryHover = color; return this; }
        public Builder tooltipBackground(int color) { this.tooltipBackground = color; return this; }
        public Builder tooltipBorder(int color) { this.tooltipBorder = color; return this; }
        public Builder divider(int color) { this.divider = color; return this; }
        public Builder shadow(int color) { this.shadow = color; return this; }
        
        public ThemeData build() {
            return new ThemeData(
                useVanillaWidgets,
                backgroundColor, backgroundSecondary,
                textPrimary, textSecondary, textDisabled,
                buttonBackground, buttonBackgroundHover, buttonBackgroundDisabled, buttonBorder,
                accentPrimary, accentSecondary,
                toggleOn, toggleOff, toggleHandle,
                sliderTrack, sliderFilled, sliderHandle,
                inputBackground, inputBorder, inputBorderFocused,
                scrollbarTrack, scrollbarThumb, scrollbarThumbHover,
                categoryBackground, categorySelected, categoryHover,
                tooltipBackground, tooltipBorder, divider, shadow
            );
        }
    }
}
