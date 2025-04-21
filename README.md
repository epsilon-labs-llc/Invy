# Invy - シンプルなカスタムアイテム管理用プラグイン

[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/epsilon-labs-llc/Invy/blob/main/README.en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](https://github.com/epsilon-labs-llc/Invy/blob/main/README.md)

**Invy** は、サーバーに軽量で簡単に導入できるカスタムアイテム管理用プラグインです。  
設定ファイルから定義したアイテムをGUIで呼び出し、手軽に召喚・使用できます。

## 特徴

- **ファイルベース**でのアイテム定義（`items.yml`）
- GUIでのアイテム呼び出し
- 一時的な権限付与機能 `/invy grant`
- アイテムを**ドロップすると自動削除**
- `/invy reload` で設定とアイテムを即時反映
- **多言語対応**（日本語、英語、カスタム）

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

## ライセンス
このプロジェクトは [MITライセンス](LICENSE) のもとで公開されています。