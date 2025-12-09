package dev.eclipseui.impl;

import dev.eclipseui.api.ConfigData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple implementation of ConfigData for manual config management.
 */
public class ConfigDataImpl implements ConfigData {
    
    private boolean dirty = false;
    private final List<Consumer<ConfigData>> changeListeners = new ArrayList<>();
    private final Runnable onLoad;
    private final Runnable onSave;
    private final Runnable onReset;
    
    public ConfigDataImpl(Runnable onLoad, Runnable onSave, Runnable onReset) {
        this.onLoad = onLoad;
        this.onSave = onSave;
        this.onReset = onReset;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public void load() {
        if (onLoad != null) {
            onLoad.run();
        }
        clearDirty();
    }
    
    @Override
    public void save() {
        if (onSave != null) {
            onSave.run();
        }
        clearDirty();
    }
    
    @Override
    public void resetToDefaults() {
        if (onReset != null) {
            onReset.run();
        }
        markDirty();
    }
    
    @Override
    public boolean isDirty() {
        return dirty;
    }
    
    @Override
    public void markDirty() {
        this.dirty = true;
        notifyListeners();
    }
    
    @Override
    public void clearDirty() {
        this.dirty = false;
    }
    
    @Override
    public void addChangeListener(Consumer<ConfigData> listener) {
        changeListeners.add(listener);
    }
    
    @Override
    public void removeChangeListener(Consumer<ConfigData> listener) {
        changeListeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (Consumer<ConfigData> listener : changeListeners) {
            listener.accept(this);
        }
    }
    
    public static class Builder {
        private Runnable onLoad;
        private Runnable onSave;
        private Runnable onReset;
        
        public Builder onLoad(Runnable onLoad) {
            this.onLoad = onLoad;
            return this;
        }
        
        public Builder onSave(Runnable onSave) {
            this.onSave = onSave;
            return this;
        }
        
        public Builder onReset(Runnable onReset) {
            this.onReset = onReset;
            return this;
        }
        
        public ConfigDataImpl build() {
            return new ConfigDataImpl(onLoad, onSave, onReset);
        }
    }
}
