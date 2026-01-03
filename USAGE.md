# Using EclipseUI in Your Mod

This guide explains how to integrate EclipseUI into your Minecraft mod for both Fabric and NeoForge platforms.

## Table of Contents
- [Adding the Dependency](#adding-the-dependency)
  - [Fabric](#fabric)
  - [NeoForge](#neoforge)
- [Basic Usage](#basic-usage)
- [Advanced Examples](#advanced-examples)
- [Available Widgets](#available-widgets)

## Adding the Dependency

EclipseUI is distributed as a single combined JAR that includes all modules (core, platform, and UI). Simply add one dependency to get everything you need.

### Fabric

#### Step 1: Add the repository

In your `build.gradle` or `build.gradle.kts`, add the GitHub Packages repository:

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
```

#### Step 2: Add the dependency

```kotlin
dependencies {
    // EclipseUI - Complete UI library (single JAR)
    modImplementation("dev.eclipseui:EclipseUI-fabric:1.0.0+mc1.21.10")
    
    // Include in your mod JAR (bundles the library with your mod)
    include("dev.eclipseui:EclipseUI-fabric:1.0.0+mc1.21.10")
}
```

#### Step 3: Configure GitHub authentication

Create a `gradle.properties` file in your project root or `~/.gradle/` directory:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.token=YOUR_GITHUB_TOKEN
```

> **Note:** You'll need a GitHub Personal Access Token with `read:packages` permission. Generate one at: https://github.com/settings/tokens

### NeoForge

#### Step 1: Add the repository

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
```

#### Step 2: Add the dependency

```kotlin
dependencies {
    // EclipseUI - Complete UI library (single JAR)
    implementation("dev.eclipseui:EclipseUI-neoforge:1.0.0+mc1.21.10")
    
    // Include in your mod JAR (jar-in-jar)
    jarJar("dev.eclipseui:EclipseUI-neoforge:1.0.0+mc1.21.10")
}
```

### Manual Installation

You can also download the JAR directly from [GitHub Releases](https://github.com/coredex-source/EclipseUI/releases) or the workflow artifacts:

| Platform | File |
|----------|------|
| Fabric | `EclipseUI-fabric-{version}.jar` |
| NeoForge | `EclipseUI-neoforge-{version}.jar` |

## Basic Usage

### Creating a Simple Config Screen

```java
import dev.eclipseui.EclipseUI;
import dev.eclipseui.api.Theme;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyModConfig {
    public static boolean enableFeature = true;
    public static int damageMultiplier = 10;
    public static String playerName = "Steve";
    
    public static Screen createConfigScreen(Screen parent) {
        return EclipseUI.configScreen()
            .title(Component.literal("My Mod Configuration"))
            .parent(parent)
            .theme(Theme.MODERN)  // or Theme.FAITHFUL
            .category(cat -> cat
                .name(Component.literal("General Settings"))
                .toggle(toggle -> toggle
                    .name(Component.literal("Enable Feature"))
                    .description(Component.literal("Enables the main feature"))
                    .binding(() -> enableFeature, v -> enableFeature = v)
                    .defaultValue(true)
                )
                .slider(slider -> slider
                    .name(Component.literal("Damage Multiplier"))
                    .range(1, 100, 1)
                    .bindingInt(() -> damageMultiplier, v -> damageMultiplier = v)
                    .defaultValue(10)
                )
                .textInput(field -> field
                    .name(Component.literal("Player Name"))
                    .binding(() -> playerName, v -> playerName = v)
                    .defaultValue("Steve")
                )
            )
            .build();
    }
}
```

### ModMenu Integration (Fabric only)

For Fabric mods, you can integrate with ModMenu:

```java
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MyModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MyModConfig::createConfigScreen;
    }
}
```

Don't forget to register it in your `fabric.mod.json`:

```json
{
  "entrypoints": {
    "modmenu": [
      "com.example.mymod.MyModMenuIntegration"
    ]
  }
}
```

## Advanced Examples

### Multiple Categories with Different Widgets

```java
public static Screen createAdvancedConfigScreen(Screen parent) {
    return EclipseUI.configScreen()
        .title(Component.literal("Advanced Configuration"))
        .parent(parent)
        .theme(Theme.MODERN)
        
        // Gameplay Category
        .category(cat -> cat
            .name(Component.literal("Gameplay"))
            .description(Component.literal("Game mechanics settings"))
            
            .toggle(t -> t
                .name(Component.literal("PvP Enabled"))
                .binding(() -> Config.pvpEnabled, v -> Config.pvpEnabled = v)
                .defaultValue(false)
            )
            .slider(s -> s
                .name(Component.literal("Difficulty Scale"))
                .range(0.5, 2.0, 0.1)
                .bindingDouble(() -> Config.difficultyScale, v -> Config.difficultyScale = v)
                .defaultValue(1.0)
            )
        )
        
        // Visual Category
        .category(cat -> cat
            .name(Component.literal("Visual"))
            .description(Component.literal("Visual appearance settings"))
            
            .separator()
            .label(Component.literal("§lColors"))
            
            .colorPicker(c -> c
                .name(Component.literal("Primary Color"))
                .binding(() -> Config.primaryColor, v -> Config.primaryColor = v)
                .defaultValue(0xFF5555FF)
                .allowAlpha(false)
            )
            .colorPicker(c -> c
                .name(Component.literal("Accent Color"))
                .binding(() -> Config.accentColor, v -> Config.accentColor = v)
                .defaultValue(0x80FF5555)
                .allowAlpha(true)
                .presets(0xFFFF0000, 0xFF00FF00, 0xFF0000FF)
            )
        )
        
        // Audio Category
        .category(cat -> cat
            .name(Component.literal("Audio"))
            .description(Component.literal("Sound settings"))
            
            .slider(s -> s
                .name(Component.literal("Master Volume"))
                .range(0, 100, 1)
                .bindingInt(() -> Config.masterVolume, v -> Config.masterVolume = v)
                .defaultValue(80)
                .suffix("%")
            )
            
            .<SoundMode>dropdown(d -> d
                .name(Component.literal("Sound Mode"))
                .enumClass(SoundMode.class)
                .binding(() -> Config.soundMode, v -> Config.soundMode = v)
                .defaultValue(SoundMode.STEREO)
            )
        )
        
        .build();
}
```

### Using with a Config File

Here's an example integrating with a simple JSON config:

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/mymod.json");
    
    public static Config config = new Config();
    
    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Screen createConfigScreen(Screen parent) {
        return EclipseUI.configScreen()
            .title(Component.literal("My Mod Config"))
            .parent(parent)
            .theme(Theme.FAITHFUL)
            .onSave(() -> save())  // Save config when user clicks save
            .category(cat -> cat
                .name(Component.literal("Settings"))
                .toggle(t -> t
                    .name(Component.literal("Feature Enabled"))
                    .binding(() -> config.featureEnabled, v -> config.featureEnabled = v)
                    .defaultValue(true)
                )
                .slider(s -> s
                    .name(Component.literal("Speed"))
                    .range(1, 10, 1)
                    .bindingInt(() -> config.speed, v -> config.speed = v)
                    .defaultValue(5)
                )
            )
            .build();
    }
    
    public static class Config {
        public boolean featureEnabled = true;
        public int speed = 5;
    }
}
```

## Available Widgets

### Toggle
Boolean on/off switch
```java
.toggle(t -> t
    .name(Component.literal("Enable Feature"))
    .description(Component.literal("Description shown on hover"))
    .binding(() -> value, v -> value = v)
    .defaultValue(true)
    .onText(Component.literal("ON"))    // Custom on text
    .offText(Component.literal("OFF"))  // Custom off text
    .requiresRestart(true)              // Show restart warning
)
```

### Slider
Numeric value selector with range
```java
// Integer slider
.slider(s -> s
    .name(Component.literal("Count"))
    .range(0, 100, 1)
    .bindingInt(() -> count, v -> count = v)
    .defaultValue(50)
    .suffix("%")  // Add suffix to displayed value
)

// Double slider
.slider(s -> s
    .name(Component.literal("Multiplier"))
    .range(0.0, 5.0, 0.1)
    .bindingDouble(() -> multiplier, v -> multiplier = v)
    .defaultValue(1.0)
    .percentageFormat()  // Display as percentage (1.0 = 100%)
)
```

### Text Input
String input field
```java
.textInput(f -> f
    .name(Component.literal("Server Name"))
    .binding(() -> serverName, v -> serverName = v)
    .defaultValue("My Server")
    .maxLength(64)
    .placeholder(Component.literal("Enter name..."))
)
```

### Dropdown
Selection from enum options
```java
.<MyEnum>dropdown(d -> d
    .name(Component.literal("Difficulty"))
    .enumClass(MyEnum.class)
    .binding(() -> difficulty, v -> difficulty = v)
    .defaultValue(MyEnum.NORMAL)
    .formatter(val -> Component.literal(val.getDisplayName()))  // Custom formatting
)
```

### Color Picker
Color selection widget (ARGB format)
```java
.colorPicker(c -> c
    .name(Component.literal("Theme Color"))
    .binding(() -> color, v -> color = v)
    .defaultValue(0xFFFFFFFF)  // White
    .allowAlpha(true)          // Enable transparency
    .presets(                  // Preset color buttons
        0xFFFF0000, 0xFF00FF00, 0xFF0000FF,
        0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF
    )
)
```

### Separator
Visual divider between options
```java
.separator()
```

### Label
Section header or text label
```java
.label(Component.literal("§lSection Title"))
.label(Component.literal("§7Muted description text"))
```

## Themes

EclipseUI comes with two built-in themes:

- **Theme.FAITHFUL** - Vanilla Minecraft style with classic widgets
- **Theme.MODERN** - Flat, modern design with smooth animations

Select a theme when building your config screen:
```java
.theme(Theme.MODERN)
```

## Building from Source

Clone the repository and build combined JARs:

```bash
git clone https://github.com/coredex-source/EclipseUI.git
cd EclipseUI
./gradlew buildAll
```

Combined JARs will be output to the `bin/` directory:
- `bin/EclipseUI-fabric-{version}.jar`
- `bin/EclipseUI-neoforge-{version}.jar`

## Tips

1. **Keep categories focused** - Group related settings together
2. **Add descriptions** - Help users understand what each option does
3. **Use appropriate ranges** - For sliders, choose sensible min/max values
4. **Save on close** - Use `.onSave()` to persist changes
5. **Default values** - Always provide sensible defaults
6. **Use separators and labels** - Organize options visually

## Support

For issues, questions, or feature requests, visit the [GitHub repository](https://github.com/coredex-source/EclipseUI/issues).

## License

EclipseUI is licensed under the MIT License.
