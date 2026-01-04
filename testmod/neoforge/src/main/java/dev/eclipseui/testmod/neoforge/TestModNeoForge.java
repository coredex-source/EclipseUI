package dev.eclipseui.testmod.neoforge;

import dev.eclipseui.testmod.TestModConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("eclipseui_testmod")
public class TestModNeoForge {
    
    public TestModNeoForge(ModContainer modContainer) {
        System.out.println("[EclipseUI-TestMod] NeoForge TestMod initialized!");
        
        // Register config screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
            (mc, parent) -> TestModConfigScreen.create(parent));
    }
    
    @EventBusSubscriber(modid = "eclipseui_testmod", value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            System.out.println("[EclipseUI-TestMod] Client setup complete!");
        }
    }
}
