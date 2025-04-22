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

| Command | Description |
|---------|-------------|
| `/invy` | Show plugin version and help |
| `/invy gui` | Open the custom item GUI |
| `/invy reload` | Reload configuration and items.yml |
| `/invy grant <player> <time>` | Temporarily grant `invy.use` permission |

## Permissions

| Permission      | Description                          |
|-----------------|--------------------------------------|
| `invy.use`      | Allows players to obtain items from GUI |
| `invy.reload`   | Allows reloading of configuration and items |
| `invy.grant`    | Allows granting temporary permissions to others |

## Sample Item Definition (`items.yml`)

```yaml
items:
  1:
    material: STICK
    name: "&bKnockbacker"
    lore:
      - "&7This is the strongest knockback stick"
    enchants:
      KNOCKBACK: 999
    unbreakable: true
```

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
This project is licensed under the [MIT License]((LICENSE)).