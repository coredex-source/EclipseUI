package dev.eclipseui.testmod.neoforge;

import dev.eclipseui.testmod.TestModConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "eclipseui_testmod", dist = Dist.CLIENT)
public final class TestModNeoForge {

    public TestModNeoForge(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> TestModConfigScreen.create(parent));
    }
}
