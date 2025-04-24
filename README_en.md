# Invy - A Simple Custom Item Management Plugin

[![en](https://img.shields.io/badge/lang-en-red.svg)](README_en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](README.md)
![GitHub Downloads](https://img.shields.io/github/downloads/epsilon-labs-llc/Invy/total?color=orange&label=GitHub%20Downloads)
[![Downloads](https://img.shields.io/spiget/downloads/124328?label=Spigot%20Downloads&color=orange)](https://www.spigotmc.org/resources/124328/)
[![Version](https://img.shields.io/spiget/version/124328?color=brightgreen)](https://www.spigotmc.org/resources/124328/)
[![Support on Buy Me a Coffee](https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-ffdd00?logo=buymeacoffee)](https://buymeacoffee.com/epsilonlabs)

**Invy** is a lightweight, easy-to-use custom item management plugin for Minecraft servers.  
It allows you to define items via configuration files and summon them through a simple GUI.

## Features

- **File-based** item definitions via `items.yml`
- Item summonable via GUI
- Temporary permission granting via `/invy grant`
- Automatically deletes dropped custom items
- Reload settings and items instantly with `/invy reload`
- **Multilingual support** (English, Japanese, Custom)
- 
## Requirements

- Developed and tested with **Java 21**
- Confirmed working on **Spigot 1.21.x**
- **Not supported before 1.20.6**

## Available Commands

| Command                       | Description                             |
|-------------------------------|-----------------------------------------|
| `/invy`                       | Show plugin version and help            |
| `/invy gui`                   | Open the custom item GUI                |
| `/invy reload`                | Reload configuration and items.yml      |
| `/invy grant <player> <time>` | Temporarily grant `invy.use` permission |

## Permissions

| Permission      | Description                                     |
|-----------------|-------------------------------------------------|
| `invy.use`      | Allows players to obtain items from GUI         |
| `invy.reload`   | Allows reloading of configuration and items     |
| `invy.grant`    | Allows granting temporary permissions to others |

## How to Define Items (items.yml)
To add custom items to the GUI, simply define them in `plugins/Invy/items.yml`.
You can register **up to 54 items**.
Check the sample [items.yml](src/main/resources/items.yml).

### Notes
- This plugin supports Bukkit/Spigot features like `Enchantment` and `Attribute`, but not all combinations are tested or guaranteed to work.
- Depending on your environment or Minecraft version, **some materials or attributes may not function properly**, or may **corrupt item data**.
- In the unlikely event of unexpected behavior or data corruption, **We are not responsible**.

### Field Reference

| Field         | Description                                                   |
|---------------|---------------------------------------------------------------|
| `material`    | Type of item (e.g., `STICK`)                                  |
| `name`        | Display name (supports color codes like `&b`)                 |
| `lore`        | Description text for the item                                 |
| `enchants`    | Enchantments and their levels                                 |
| `attributes`  | Attributes with values and application methods                |
| `unbreakable` | Set to `true` to prevent durability loss (Default is `false`) |

`ID`and `material` are required. Everything else is optional and customizable.
See: [Spigot Material List](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) 

### About Enchantments
Use the `enchants` section to add special effects to items such as damage boosts, knockback, or tool enhancements.

```yaml
enchants:
  SHARPNESS: 100
  FIRE_ASPECT: 10
  KNOCKBACK: 999
```
Format: `ENCHANTMENT_NAME: level`
Levels up to `999` are supported (Some effects may cap out).

#### Common Enchantments

| Enchantment   | Effect                   | Max (Vanilla) |
|---------------|--------------------------|---------------|
| `SHARPNESS`   | Increases attack damage  | 5             |
| `FIRE_ASPECT` | Sets enemies on fire     | 2             | 
| `KNOCKBACK`   | Pushes enemies back      | 2             | 
| `PROTECTION`  | General damage reduction | 4             | 
| `UNBREAKING`  | Reduces durability loss  | 3             | 
| `EFFICIENCY`  | Faster mining            | 5             | 

- [Spigot Enchantment List](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html)
- [Minecraft Wiki - Enchanting](https://minecraft.wiki/w/Enchanting)

### About Attributes
The `attributes` section allows you to enhance items with combat stats and abilities like speed, health, and damage.

```yaml
attributes:
  ATTACK_DAMAGE:
    amount: 100
    operation: ADD_NUMBER
```

#### Basic Structure

| Field       | Description                                          |
|-------------|------------------------------------------------------|
| `amount`    | The amount of the effect (differs by operation type) |
| `operation` | How the value is applied                             |

You don't need to set `slot`, it's automatically set to `ANY`

#### `operation` Types

| Operation           | Description                  | Example (base = 10)                     |
|---------------------|------------------------------|-----------------------------------------|
| `ADD_NUMBER`        | Adds `amount` directly       | `10 + amount`                           |
| `ADD_SCALAR`        | Adds based on a percentage   | `10 + (10 × 0.3)` = `13`                |
| `MULTIPLY_SCALAR_1` | Multiplies by `(1 + amount)` | `10 × (1 + amount)` = `10 × 1.3` = `13` |

Typically, use `ADD_SCALAR` for speed and  `ADD_NUMBER` for health or damage.

#### Common Attributes

| Attribute        | Effect             | Default | Min | Max    |
|------------------|--------------------|---------|-----|--------|
| `MOVEMENT_SPEED` | Increases movement | 0.7     | 0.0 | 1024.0 |
| `JUMP_STRENGTH`  | Increases jump     | 0.42    | 0.0 | 32.0   |
| `ATTACK_DAMAGE`  | Boosts damage      | 2.0     | 0.0 | 2048.0 |
| `ATTACK_SPEED`   | Faster attacks     | 4.0     | 0.0 | 1024.0 |
| `MAX_HEALTH`     | More hearts        | 20.0    | 0.0 | 1024.0 |
| `ARMOR`          | Extra defense      | 0.0     | 0.0 | 30.0   |

- [Spigot Attribute List](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html)
- [Minecraft Wiki - Attributes](https://minecraft.wiki/w/Attribute)

## Language Setting (config.yml)

```yaml
lang: en  # Options: en, ja, custom
```

## Custom Messages
If you set `lang: custom`, a `message.yml` file will be generated for editing.  
By default, English and Japanese are included.

## Support This Project

This plugin is maintained by **Epsilon Labs, LLC.**  
Your support helps cover development and maintenance.

<a href="https://www.buymeacoffee.com/epsilonlabs" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Coffee" width="200" />
</a>

## License
This project is licensed under the [MIT License](LICENSE).
