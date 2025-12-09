package dev.eclipseui.neoforge;

import dev.eclipseui.EclipseUI;
import dev.eclipseui.neoforge.example.ExampleConfigScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * NeoForge entry point for EclipseUI.
 * 
 * <p>This demonstrates how to integrate EclipseUI with NeoForge's config screen system.</p>
 * 
 * <h3>Usage in your own mod:</h3>
 * <pre>{@code
 * @Mod(value = "yourmod", dist = Dist.CLIENT)
 * public class YourMod {
 *     public YourMod(IEventBus modEventBus, ModContainer modContainer) {
 *         // Register your config screen factory
 *         modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
 *             (mc, parent) -> EclipseUI.configScreen()
 *                 .title(Component.literal("Your Mod Config"))
 *                 .parent(parent)
 *                 .theme(Theme.MODERN)
 *                 .category(cat -> cat
 *                     .name(Component.literal("General"))
 *                     .toggle(t -> t
 *                         .name(Component.literal("Enable Feature"))
 *                         .binding(() -> YourConfig.enableFeature, v -> YourConfig.enableFeature = v)
 *                         .defaultValue(true)
 *                     )
 *                 )
 *                 .onSave(() -> YourConfig.save())
 *                 .build()
 *         );
 *     }
 * }
 * }</pre>
 */
@Mod(value = EclipseUI.MOD_ID, dist = Dist.CLIENT)
public class EclipseUINeoForge {
    
    public EclipseUINeoForge(IEventBus modEventBus, ModContainer modContainer) {
        EclipseUI.init();
        
        // Register config screen factory - uses MODERN theme example
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
            (mc, parent) -> ExampleConfigScreens.createModernScreen(parent)
        );
    }
}
