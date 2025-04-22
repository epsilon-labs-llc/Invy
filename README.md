# Invy - シンプルなカスタムアイテム管理用プラグイン

[![en](https://img.shields.io/badge/lang-en-red.svg)](README_en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](README.md)
[![Downloads](https://img.shields.io/spiget/downloads/124328?label=Spigot%20Downloads&color=orange)](https://www.spigotmc.org/resources/124328/)
[![Version](https://img.shields.io/spiget/version/124328?color=brightgreen)](https://www.spigotmc.org/resources/124328/)
[![Support on Buy Me a Coffee](https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-ffdd00?logo=buymeacoffee)](https://buymeacoffee.com/epsilonlabs)

**Invy** は、サーバーに軽量で簡単に導入できるカスタムアイテム管理用プラグインです。  
設定ファイルから定義したアイテムをGUIで呼び出し、手軽に召喚・使用できます。

## 特徴

- **ファイルベース**でのアイテム定義（`items.yml`）
- GUIでのアイテム呼び出し
- 一時的な権限付与機能 `/invy grant`
- アイテムを**ドロップすると自動削除**
- `/invy reload` で設定とアイテムを即時反映
- **多言語対応**（日本語、英語、カスタム）

## 動作環境

- **Java 21** で開発・動作確認済み
- **Spigot 1.21.x** で確認済み
- **1.20.6 以前は非対応**

## コマンド一覧

| コマンド | 説明 |
|---------|------|
| `/invy` | バージョンやヘルプ表示 |
| `/invy gui` | アイテム一覧GUIを開く |
| `/invy reload` | 設定とitems.ymlを再読み込み |
| `/invy grant <player> <time>` | 一時的に `invy.use` を付与 |

## 権限一覧

| 権限 | 説明                 |
|------|--------------------|
| `invy.use` | GUIからアイテムを召喚可能     |
| `invy.reload` | 設定を再読み込み可能         |
| `invy.grant` | 他プレイヤーに一時的な権限を付与可能 |

## アイテム定義例（items.yml）

```yaml
items:
  1:
    material: STICK
    name: "&bノックバッカー"
    lore:
      - "&7これは最強のノックバック棒"
    enchants:
      KNOCKBACK: 999
    unbreakable: true
```

## config.yml（言語設定）

```yaml
lang: ja  # en, ja, custom に対応
```

## メッセージのカスタマイズ
`lang: custom` にすると、`message.yml` が生成されて編集できます。  
初期状態では日本語と英語に対応しています。

## ご支援のお願い

このプラグインは、合同会社イプシロン・ラボにより運営・開発さしています。  
いただいたご支援は、開発・保守などに活用させていただきます。

<a href="https://www.buymeacoffee.com/epsilonlabs" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Coffee" width="200" />
</a>

## ライセンス
このプロジェクトは [MITライセンス](LICENSE) のもとで公開されています。