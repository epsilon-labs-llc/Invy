# Invy - A Simple Custom Item Management Plugin

[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/epsilon-labs-llc/Invy/blob/main/README.en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](https://github.com/epsilon-labs-llc/Invy/blob/main/README.md)

**Invy** is a lightweight, easy-to-use custom item management plugin for Minecraft servers.  
It allows you to define items via configuration files and summon them through a simple GUI.

## Features

- **File-based** item definitions via `items.yml`
- Item summonable via GUI
- Temporary permission granting via `/invy grant`
- Automatically deletes dropped custom items
- Reload settings and items instantly with `/invy reload`
- **Multilingual support** (English, Japanese, Custom)

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

## License
This project is licensed under the [MIT License]((LICENSE)).