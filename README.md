# EclipseUI

A lightweight, themeable config screen library for Minecraft mods.

This legacy branch supports **Minecraft 1.21.1 only**, on **Fabric and NeoForge**, with Java 21. It is maintained for occasional compatibility updates.

## Features

- **Fabric and NeoForge** support
- **Two themes**: Faithful (vanilla style) & Modern (flat design)
- **Rich widgets**: toggles, sliders, dropdowns, color pickers, text fields
- **ModMenu** integration out of the box

## Installation

### For Mod Developers

Add EclipseUI to your mod by following the [Usage Guide](USAGE.md).

**Quick dependency setup:**

```groovy
repositories {
    maven { url = "https://api.modrinth.com/maven" }
}

dependencies {
    // Fabric
    modImplementation "maven.modrinth:eclipseui:${project.eclipseUiVersion}"
    // NeoForge: use implementation instead.
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

Use JDK 25 to run Gradle; the build plugins require it. The library targets Java 21, which Gradle resolves through its toolchain configuration.

```bash
# Clone the repository
git clone --branch legacy-1.21.1 https://github.com/coredex-source/EclipseUI.git
cd EclipseUI

# Build both 1.21.1 loaders (Java 21 target)
./gradlew clean cleanBin buildAll

# Publish to local Maven
./gradlew publishToMavenLocal
```

Artifacts are written to `bin/`. To build just one loader, use
`./gradlew :1.21.1-fabric:buildAll` or `./gradlew :1.21.1-neoforge:buildAll`.

The `testmod/` project uses these local JARs. After building the library:

```bash
cd testmod
./gradlew build
./gradlew :fabric:runClient
# Or: ./gradlew :neoforge:runClient
```

## License

MIT License
