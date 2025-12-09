# EclipseUI

A lightweight, themeable config screen library for Minecraft mods.

## Features

- **Fabric & NeoForge** support (1.21.9+)
- **Two themes**: Faithful (vanilla style) & Modern (flat design)
- **Rich widgets**: toggles, sliders, dropdowns, color pickers, text fields
- **ModMenu** integration out of the box

## Quick Start

```java
EclipseUI.configScreen()
    .title(Component.literal("My Mod Config"))
    .parent(parent)
    .theme(Theme.MODERN)
    .category(cat -> cat
        .name(Component.literal("General"))
        .toggle(t -> t
            .name(Component.literal("Enable Feature"))
            .binding(() -> config.enabled, v -> config.enabled = v)
            .defaultValue(true)
        )
        .slider(s -> s
            .name(Component.literal("Range"))
            .range(0, 100, 1)
            .bindingInt(() -> config.range, v -> config.range = v)
            .defaultValue(50)
        )
    )
    .build();
```

## License

MIT License
