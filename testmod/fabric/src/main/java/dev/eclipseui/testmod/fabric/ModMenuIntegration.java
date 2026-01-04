package dev.eclipseui.testmod.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.eclipseui.testmod.TestModConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuScreenFactory::createConfigScreen;
    }
}
