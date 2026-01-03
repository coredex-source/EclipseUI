# EclipseUI

A lightweight, themeable config screen library for Minecraft mods.

## Features

- **Fabric & NeoForge** support (1.21.9+)
- **Two themes**: Faithful (vanilla style) & Modern (flat design)
- **Rich widgets**: toggles, sliders, dropdowns, color pickers, text fields
- **ModMenu** integration out of the box

## Installation

### For Mod Developers

Add EclipseUI to your mod by following the [Usage Guide](USAGE.md).

**Quick dependency setup:**

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/coredex-source/EclipseUI")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // Fabric
    modImplementation("dev.eclipseui:EclipseUI-fabric:1.0.0+mc1.21.10")
    
    // NeoForge
    implementation("dev.eclipseui:EclipseUI-neoforge:1.0.0+mc1.21.10")
}
```

See the full [Usage Guide](USAGE.md) for complete setup instructions and examples.

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

## Documentation

- [Usage Guide](USAGE.md) - Complete guide on integrating EclipseUI into your mod
- [GitHub Issues](https://github.com/coredex-source/EclipseUI/issues) - Report bugs or request features

## Building from Source

```bash
# Clone the repository
git clone https://github.com/coredex-source/EclipseUI.git
cd EclipseUI

# Clean build all modules
./gradlew clean cleanBin buildAll

# Publish to local Maven
./gradlew publishToMavenLocal
```

## License

MIT License
