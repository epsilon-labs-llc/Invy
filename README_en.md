# Invy - A Simple Custom Item Management Plugin

[![en](https://img.shields.io/badge/lang-en-red.svg)](README_en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](README.md)
![GitHub Downloads](https://img.shields.io/github/downloads/epsilon-labs-llc/Invy/total?color=orange&label=GitHub%20Downloads)
[![Downloads](https://img.shields.io/spiget/downloads/124328?label=Spigot%20Downloads&color=orange)](https://www.spigotmc.org/resources/124328/)
[![Version](https://img.shields.io/spiget/version/124328?color=brightgreen)](https://www.spigotmc.org/resources/124328/)
[![Support on Buy Me a Coffee](https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-ffdd00?logo=buymeacoffee)](https://buymeacoffee.com/epsilonlabs)

**Invy** is a lightweight, easy-to-use custom item management plugin for Minecraft servers.  
You can summon and use items easily through a GUI based on definitions from a configuration file.

Summoning Custom Items from GUI
![Image of Summoning Custom Items from GUI](imgs/summon_items.gif)

Using Custom Items
![Image of Using Custom Items](imgs/use_items.gif)

## Features

- **File-based** item definitions via `items.yml`
- Item summonable via GUI
- Temporary permission granting via `/invy grant`
- Automatically deletes dropped custom items
- Reload configuration and items instantly with `/invy reload`
- **Multilingual support** (English, Japanese, and Custom)

## Requirements

- Developed and tested with **Java 21**
- Confirmed working on **Spigot 1.21.x**
- **Not supported before 1.20.6**

## Commands

| Command                       | Description                             |
|-------------------------------|-----------------------------------------|
| `/invy`                       | Show plugin version and help            |
| `/invy gui`                   | Open the custom item GUI                |
| `/invy reload`                | Reload configuration and items.yml      |
| `/invy grant <player> <time>` | Temporarily grant `invy.use` permission |

## Permissions

| Permission      | Description                                     |
|-----------------|-------------------------------------------------|
| `invy.use`      | Allows players to summon items from GUI         |
| `invy.reload`   | Allows reloading of configuration and items     |
| `invy.grant`    | Allows granting temporary permissions to others |

## How to Define Items (items.yml)
Define the items you want to summon in `plugins/Invy/items.yml`, and they’ll be available in the GUI.
You can register up to **54 items**.
Refer to the sample: [items.yml](src/main/resources/items.yml).

### Notes
- This plugin supports Bukkit/Spigot features like `Enchantment`, `Attribute` and `PotionEffect`, but **not all combinations are guaranteed to work**.
- Enchantments and Attributes can be duplicated, but **Potion Effects are completely separate metadata and cannot be duplicated**.
- Depending on your environment or Minecraft version, **some items or attributes may not function properly**, or may **corrupt item data**.
- We **cannot be held responsible** for any unexpected behavior or corrupted data.

### Field Reference

| Field            | Description                                                   |
|------------------|---------------------------------------------------------------|
| `material`       | Type of item (e.g., `STICK`)                                  |
| `name`           | Display name (supports color codes like `&b`)                 |
| `lore`           | Description text for the item                                 |
| `unbreakable`    | Set to `true` to prevent durability loss (Default is `false`) |
| `enchants`       | Enchantments and their levels                                 |
| `attributes`     | Attributes with values and application methods                |
| `potion_effects` | Potion effects and detailed settings                          |

`ID`and `material` are required. Everything else is optional and customizable.
Check: [Spigot Material List](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) 

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

| Enchantment   | Effect                  | Max Lv (Vanilla) |
|---------------|-------------------------|------------------|
| `SHARPNESS`   | Increases attack damage | 5                |
| `FIRE_ASPECT` | Sets targets on fire    | 2                | 
| `KNOCKBACK`   | Increases knockback     | 2                | 
| `PROTECTION`  | Increases protection    | 4                | 
| `UNBREAKING`  | Reduces durability loss | 3                | 
| `EFFICIENCY`  | Increases mining speed  | 5                | 

- [Spigot Enchantment List](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html)
- [Minecraft Wiki - Enchanting](https://minecraft.wiki/w/Enchanting)

### About Attributes
The `attributes` section allows you to enhance items with combat stats and abilities like speed, health, and damage.

```yaml
attributes:
  ATTACK_DAMAGE:
    amount: 100
    operation: ADD_NUMBER
    slots:
      - HAND
      - OFF_HAND
  ATTACK_SPEED:
    amount: 100
    operation: ADD_SCALAR
    slots: HAND
```

#### Basic Structure

| Field       | Description                                          |
|-------------|------------------------------------------------------|
| `amount`    | The amount of the effect (differs by operation type) |
| `operation` | How the value is applied                             |
| `slots`     | The equipment slot(s) to apply the effect to         |

#### operation Types

| Operation           | Description                  | Example (base = 10)                     |
|---------------------|------------------------------|-----------------------------------------|
| `ADD_NUMBER`        | Directly add `amount`        | `10 + amount`                           |
| `ADD_SCALAR`        | Adds `(base × amount)`       | `10 + (10 × 0.3)` = `13`                |
| `MULTIPLY_SCALAR_1` | Multiplies by `(1 + amount)` | `10 × (1 + amount)` = `10 × 1.3` = `13` |

Typically, use `ADD_SCALAR` for speed and  `ADD_NUMBER` for health or damage.

#### Equipment Slots
| Slot Name  | Description |
|------------|-------------|
| `HAND`     | Main hand   |
| `OFF_HAND` | Off-hand    |
| `HEAD`     | Helmet      |
| `CHEST`    | Chestplate  |
| `LEGS`     | Leggings    |
| `FEET`     | Boots       |

- If `slots` are not specified, it defaults to ANY.
- Avoid **mixing multiple slots for the same key** to prevent unexpected behavior.

#### Common Attributes

| Attribute        | Effect        | Default | Min | Max    |
|------------------|---------------|---------|-----|--------|
| `MOVEMENT_SPEED` | Move speed    | 0.7     | 0.0 | 1024.0 |
| `JUMP_STRENGTH`  | Jump boost    | 0.42    | 0.0 | 32.0   |
| `ATTACK_DAMAGE`  | Attack power  | 2.0     | 0.0 | 2048.0 |
| `ATTACK_SPEED`   | Attack speed  | 4.0     | 0.0 | 1024.0 |
| `MAX_HEALTH`     | Max health    | 20.0    | 0.0 | 1024.0 |
| `ARMOR`          | Extra defense | 0.0     | 0.0 | 30.0   |

- [Spigot Attribute List](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html)
- [Minecraft Wiki - Attributes](https://minecraft.wiki/w/Attribute)

### Potion Effects
The `potion_effects` section allows you to add several unique effects to a potion.   
For example, you can set up custom effects such as "Always Speed Up" or "Weakness on Attack."     　
Potion effects are only available for potions whose `material` is `POTION`, `SPLASH_POTION` or `LINGERING_POTION`.

```yaml
potion_effects:
  SPEED:
    duration: 60
    level: 99
    ambient: true
    particles: true
    icon: true
```

#### Basic Structure

| Field       | Description                   |
|-------------|-------------------------------|
| `SPEED`     | Effect type                   |
| `duration`  | Duration in seconds           |
| `level`     | Effect level                  |
| `ambient`   | Reduce visual particles       |
| `particles` | Show effect particles         |
| `icon`      | Show icon in the top-right UI |

#### About Duration
- The `duration` should be specified in seconds.
- Actually, it is internally handled in **tick units** (1 second = 20 ticks).
- Due to Bukkit/Minecraft specifications, you can specify up to **maximum** `2,147,483,647 ticks` (**about 24.8 days**).
- For practical use, you can set it to `99999999` (about 2.7 days) or `-1` for **"almost infinite”** use.

#### List of potion effects
See official documentation or Wiki

- [Spigot PotionEffectType List](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html)
- [Minecraft Wiki - Effect](https://minecraft.wiki/w/Effect)

## Configuration (config.yml)
Customize language and features via `plugins/Invy/config.yml`.

### Language

```yaml
lang: en
```

- `ja`: Japanese
- `en`: English
- `custom`: Generates `message.yml` for full customization

### Feature Toggles

```yaml
features:
  prevent_drop: true
  prevent_storage: true
  prevent_death_drop: true
```
| Key                  | Description                                                                           |
|----------------------|---------------------------------------------------------------------------------------|
| `prevent_drop`       | Prevents custom items from being dropped                                              |
| `prevent_storage`    | Blocks storing in **chests**, **shulker boxes**, or **barrels** (Ender chest allowed) |
| `prevent_death_drop` | Prevents item drops on player death                                                   |

## Support This Project

This plugin is maintained by **Epsilon Labs, LLC.**  
Your support helps cover development and maintenance.

<a href="https://www.buymeacoffee.com/epsilonlabs" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Coffee" width="200" />
</a>

## License
This project is licensed under the [MIT License](LICENSE).
