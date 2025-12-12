package dev.eclipsecore.util;

/**
 * Color utility functions.
 */
public final class Colors {
    
    private Colors() {}
    
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    public static int fromARGB(int alpha, int red, int green, int blue) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
    
    public static int fromRGB(int red, int green, int blue) {
        return fromARGB(255, red, green, blue);
    }
    
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }
    
    public static String toHex(int color) {
        return String.format("%08X", color);
    }
    
    public static String toHexWithHash(int color) {
        return "#" + toHex(color);
    }
    
    public static int parseHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 6) {
            hex = "FF" + hex;
        }
        return (int) Long.parseLong(hex, 16);
    }
    
    public static int blend(int color1, int color2, float ratio) {
        float invRatio = 1.0f - ratio;
        int a = (int) (getAlpha(color1) * invRatio + getAlpha(color2) * ratio);
        int r = (int) (getRed(color1) * invRatio + getRed(color2) * ratio);
        int g = (int) (getGreen(color1) * invRatio + getGreen(color2) * ratio);
        int b = (int) (getBlue(color1) * invRatio + getBlue(color2) * ratio);
        return fromARGB(a, r, g, b);
    }
}
