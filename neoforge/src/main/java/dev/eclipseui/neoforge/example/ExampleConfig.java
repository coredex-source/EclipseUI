package dev.eclipseui.neoforge.example;

import dev.eclipseui.api.Theme;

/**
 * Example configuration class demonstrating all supported option types.
 * This is a simple POJO that holds config values - you would typically
 * serialize/deserialize this to a JSON file or use NeoForge's config system.
 */
public class ExampleConfig {
    
    // Singleton instance
    private static ExampleConfig INSTANCE = new ExampleConfig();
    
    public static ExampleConfig get() {
        return INSTANCE;
    }
    
    // ========================================
    // General Settings
    // ========================================
    
    public boolean enableMod = true;
    public boolean showNotifications = true;
    public boolean debugMode = false;
    public boolean experimentalFeatures = false;
    
    // ========================================
    // Graphics Settings  
    // ========================================
    
    public GraphicsQuality graphicsQuality = GraphicsQuality.MEDIUM;
    public int renderDistance = 12;
    public double brightness = 1.0;
    public boolean enableShaders = false;
    public boolean fancyParticles = true;
    public int maxFps = 60;
    
    // ========================================
    // Audio Settings
    // ========================================
    
    public int masterVolume = 100;
    public int musicVolume = 80;
    public int sfxVolume = 100;
    public boolean muteWhenUnfocused = false;
    public SoundMode soundMode = SoundMode.STEREO;
    
    // ========================================
    // Appearance Settings
    // ========================================
    
    public Theme theme = Theme.MODERN;
    public int primaryColor = 0xFF5555FF;  // Blue
    public int secondaryColor = 0xFF55FF55;  // Green
    public int accentColor = 0xFFFF5555;  // Red
    public double uiScale = 1.0;
    public String customTitle = "My Mod";
    public HudPosition hudPosition = HudPosition.TOP_LEFT;
    
    // ========================================
    // Gameplay Settings
    // ========================================
    
    public GameDifficulty difficulty = GameDifficulty.NORMAL;
    public boolean autoSave = true;
    public int autoSaveInterval = 5;  // minutes
    public boolean showTutorials = true;
    public double movementSpeed = 1.0;
    public boolean enablePvp = true;
    
    // ========================================
    // Enums
    // ========================================
    
    public enum GraphicsQuality {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        ULTRA("Ultra");
        
        private final String displayName;
        
        GraphicsQuality(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public enum SoundMode {
        MONO("Mono"),
        STEREO("Stereo"),
        SURROUND("Surround 5.1");
        
        private final String displayName;
        
        SoundMode(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public enum HudPosition {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right"),
        CENTER("Center");
        
        private final String displayName;
        
        HudPosition(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public enum GameDifficulty {
        EASY("Easy"),
        NORMAL("Normal"),
        HARD("Hard"),
        NIGHTMARE("Nightmare");
        
        private final String displayName;
        
        GameDifficulty(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    // ========================================
    // Save/Load Methods
    // ========================================
    
    public void save() {
        // In a real mod, you would serialize to JSON or use NeoForge config here
        System.out.println("[ExampleConfig] Config saved!");
    }
    
    public void load() {
        // In a real mod, you would deserialize from JSON or use NeoForge config here
        System.out.println("[ExampleConfig] Config loaded!");
    }
    
    public void resetToDefaults() {
        INSTANCE = new ExampleConfig();
        System.out.println("[ExampleConfig] Config reset to defaults!");
    }
}
