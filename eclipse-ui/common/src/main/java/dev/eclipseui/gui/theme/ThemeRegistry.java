package dev.eclipseui.gui.theme;

import dev.eclipseui.api.Theme;
import dev.eclipseui.api.ThemeData;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry for built-in and custom themes.
 */
public final class ThemeRegistry {
    
    private static final Map<Theme, ThemeData> THEMES = new EnumMap<>(Theme.class);
    private static ThemeData customTheme = null;
    
    static {
        registerBuiltInThemes();
    }
    
    private ThemeRegistry() {}
    
    private static void registerBuiltInThemes() {
        // Faithful - Vanilla-style widgets with modern layout
        THEMES.put(Theme.FAITHFUL, ThemeData.builder()
            .useVanillaWidgets(true)
            .backgroundColor(0xC0101010)
            .backgroundSecondary(0xFF1a1a1a)
            .textPrimary(0xFFFFFFFF)
            .textSecondary(0xFFB0B0B0)
            .textDisabled(0xFF606060)
            .buttonBackground(0xFF404040)
            .buttonBackgroundHover(0xFF505050)
            .buttonBackgroundDisabled(0xFF303030)
            .buttonBorder(0xFF606060)
            .accentPrimary(0xFF6A8CFF)
            .accentSecondary(0xFF4A6CD9)
            .toggleOn(0xFF6AFF6A)
            .toggleOff(0xFFFF6A6A)
            .toggleHandle(0xFFFFFFFF)
            .sliderTrack(0xFF404040)
            .sliderFilled(0xFF6A8CFF)
            .sliderHandle(0xFFFFFFFF)
            .inputBackground(0xFF1a1a1a)
            .inputBorder(0xFF606060)
            .inputBorderFocused(0xFF6A8CFF)
            .scrollbarTrack(0xFF1a1a1a)
            .scrollbarThumb(0xFF505050)
            .scrollbarThumbHover(0xFF707070)
            .categoryBackground(0xFF1a1a1a)
            .categorySelected(0xFF6A8CFF)
            .categoryHover(0xFF2a2a2a)
            .tooltipBackground(0xF0100010)
            .tooltipBorder(0xFF6A8CFF)
            .divider(0xFF3a3a3a)
            .shadow(0x80000000)
            .build());
        
        // Modern - Sodium-inspired cyan theme
        THEMES.put(Theme.MODERN, ThemeData.builder()
            .backgroundColor(0xFF1a1a1a)
            .backgroundSecondary(0xFF2d2d2d)
            .textPrimary(0xFFFFFFFF)
            .textSecondary(0xFFAAAAAA)
            .textDisabled(0xFF666666)
            .buttonBackground(0xFF3a3a3a)
            .buttonBackgroundHover(0xFF4a4a4a)
            .buttonBackgroundDisabled(0xFF2a2a2a)
            .buttonBorder(0xFF5a5a5a)
            .accentPrimary(0xFF00BCD4)
            .accentSecondary(0xFF0097A7)
            .toggleOn(0xFF00BCD4)
            .toggleOff(0xFF5a5a5a)
            .toggleHandle(0xFFFFFFFF)
            .sliderTrack(0xFF3a3a3a)
            .sliderFilled(0xFF00BCD4)
            .sliderHandle(0xFFFFFFFF)
            .inputBackground(0xFF2a2a2a)
            .inputBorder(0xFF4a4a4a)
            .inputBorderFocused(0xFF00BCD4)
            .scrollbarTrack(0xFF2a2a2a)
            .scrollbarThumb(0xFF5a5a5a)
            .scrollbarThumbHover(0xFF7a7a7a)
            .categoryBackground(0xFF252525)
            .categorySelected(0xFF00BCD4)
            .categoryHover(0xFF353535)
            .tooltipBackground(0xF0101018)
            .tooltipBorder(0xFF00BCD4)
            .divider(0xFF3a3a3a)
            .shadow(0x80000000)
            .build());
    }
    
    /**
     * Get the ThemeData for a given theme.
     */
    public static ThemeData get(Theme theme) {
        if (theme == Theme.CUSTOM) {
            return customTheme != null ? customTheme : THEMES.get(Theme.MODERN);
        }
        return THEMES.getOrDefault(theme, THEMES.get(Theme.MODERN));
    }
    
    /**
     * Set a custom theme.
     */
    public static void setCustomTheme(ThemeData themeData) {
        customTheme = themeData;
    }
    
    /**
     * Get the current custom theme, or null if not set.
     */
    public static ThemeData getCustomTheme() {
        return customTheme;
    }
    
    /**
     * Check if a custom theme is set.
     */
    public static boolean hasCustomTheme() {
        return customTheme != null;
    }
}
