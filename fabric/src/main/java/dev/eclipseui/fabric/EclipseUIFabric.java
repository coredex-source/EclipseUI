package dev.eclipseui.fabric;

import dev.eclipseui.EclipseUI;
import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric client entry point for EclipseUI.
 */
public class EclipseUIFabric implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        EclipseUI.init();
    }
}
