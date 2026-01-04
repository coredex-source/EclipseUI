package dev.eclipseui.testmod.fabric;

import net.fabricmc.api.ClientModInitializer;

public class TestModFabric implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        System.out.println("[EclipseUI-TestMod] Fabric TestMod initialized!");
    }
}
