package dev.eclipseui.testmod.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.eclipseui.testmod.TestModConfigScreen;
import net.minecraft.client.gui.screens.Screen;

/**
 * Separate factory class to defer Screen class loading.
 */
public final class ModMenuScreenFactory {
    
    private ModMenuScreenFactory() {}
    
    public static ConfigScreenFactory<?> getFactory() {
        return parent -> TestModConfigScreen.create(parent);
    }
    
    public static Screen createConfigScreen(Screen parent) {
        return TestModConfigScreen.create(parent);
    }
}
