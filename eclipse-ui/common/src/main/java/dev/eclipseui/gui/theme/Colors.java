package dev.eclipseui.gui.theme;

/**
 * Utility class for color manipulation.
 * Colors are represented as ARGB integers.
 */
public final class Colors {
    
    private Colors() {}
    
    // Common colors
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int TRANSPARENT = 0x00000000;
    
    // Grays
    public static final int GRAY_DARK = 0xFF1a1a1a;
    public static final int GRAY_MEDIUM = 0xFF3a3a3a;
    public static final int GRAY_LIGHT = 0xFF6a6a6a;
    
    // Accent colors
    public static final int CYAN = 0xFF00bcd4;
    public static final int BLUE = 0xFF2196f3;
    public static final int GREEN = 0xFF4caf50;
    public static final int RED = 0xFFf44336;
    public static final int ORANGE = 0xFFff9800;
    public static final int PURPLE = 0xFF9c27b0;
    
    /**
     * Creates an ARGB color from components.
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    /**
     * Creates an RGB color with full opacity.
     */
    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }
    
    /**
     * Extracts the alpha component (0-255).
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    
    /**
     * Extracts the red component (0-255).
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Extracts the green component (0-255).
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Extracts the blue component (0-255).
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    /**
     * Returns a color with modified alpha.
     */
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }
    
    /**
     * Returns a color with modified alpha (0.0-1.0).
     */
    public static int withAlpha(int color, float alpha) {
        return withAlpha(color, (int) (alpha * 255));
    }
    
    /**
     * Blends two colors together.
     * @param factor 0.0 = color1, 1.0 = color2
     */
    public static int blend(int color1, int color2, float factor) {
        // Fast path for no blending needed
        if (factor <= 0f) return color1;
        if (factor >= 1f) return color2;
        
        float inverse = 1.0f - factor;
        
        // Inline component extraction to avoid method call overhead
        int a = (int) (((color1 >> 24) & 0xFF) * inverse + ((color2 >> 24) & 0xFF) * factor);
        int r = (int) (((color1 >> 16) & 0xFF) * inverse + ((color2 >> 16) & 0xFF) * factor);
        int g = (int) (((color1 >> 8) & 0xFF) * inverse + ((color2 >> 8) & 0xFF) * factor);
        int b = (int) ((color1 & 0xFF) * inverse + (color2 & 0xFF) * factor);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Lightens a color by the given factor.
     */
    public static int lighten(int color, float factor) {
        return blend(color, WHITE, factor);
    }
    
    /**
     * Darkens a color by the given factor.
     */
    public static int darken(int color, float factor) {
        return blend(color, BLACK, factor);
    }
    
    /**
     * Converts a hex string (with or without #) to a color.
     */
    public static int fromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        
        if (hex.length() == 6) {
            return 0xFF000000 | Integer.parseInt(hex, 16);
        } else if (hex.length() == 8) {
            return (int) Long.parseLong(hex, 16);
        }
        
        throw new IllegalArgumentException("Invalid hex color: " + hex);
    }
    
    /**
     * Converts a color to a hex string (without #).
     */
    public static String toHex(int color) {
        return String.format("%08X", color);
    }
    
    /**
     * Converts a color to a hex string with # prefix.
     */
    public static String toHexWithHash(int color) {
        return "#" + toHex(color);
    }
}
