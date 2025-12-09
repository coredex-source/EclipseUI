package dev.eclipseui.api;

import java.util.function.Consumer;

/**
 * Interface for config data storage.
 * Implementations handle loading/saving configuration values.
 */
public interface ConfigData {
    
    /**
     * Load config values from storage.
     */
    void load();
    
    /**
     * Save config values to storage.
     */
    void save();
    
    /**
     * Reset all values to defaults.
     */
    void resetToDefaults();
    
    /**
     * Check if any values have been modified since last save.
     */
    boolean isDirty();
    
    /**
     * Mark the config as modified.
     */
    void markDirty();
    
    /**
     * Clear the dirty flag (after saving).
     */
    void clearDirty();
    
    /**
     * Register a listener for when config values change.
     */
    void addChangeListener(Consumer<ConfigData> listener);
    
    /**
     * Remove a change listener.
     */
    void removeChangeListener(Consumer<ConfigData> listener);
}
