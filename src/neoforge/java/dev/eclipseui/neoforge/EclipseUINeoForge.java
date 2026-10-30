package dev.eclipseui.neoforge;

import dev.eclipseplatform.EclipsePlatform;
import dev.eclipseui.EclipseUI;
import dev.eclipseui.example.ExampleConfigScreens;
import net.neoforged.fml.ModContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * NeoForge entry point for the EclipseUI mod.
 */
@Mod(value = EclipseUI.MOD_ID, dist = Dist.CLIENT)
public final class EclipseUINeoForge {

    public EclipseUINeoForge(ModContainer container) {
        EclipsePlatform.init();
        EclipseUI.init();

        IConfigScreenFactory configScreenFactory = (modContainer, parent) -> ExampleConfigScreens.createModernScreen(parent);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactory);
    }
}
