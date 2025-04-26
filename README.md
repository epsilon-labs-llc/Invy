# Invy - シンプルなカスタムアイテム管理用プラグイン

[![en](https://img.shields.io/badge/lang-en-red.svg)](README_en.md)
[![ja](https://img.shields.io/badge/lang-ja-blue.svg)](README.md)
![GitHub Downloads](https://img.shields.io/github/downloads/epsilon-labs-llc/Invy/total?color=orange&label=GitHub%20Downloads)
[![Downloads](https://img.shields.io/spiget/downloads/124328?label=Spigot%20Downloads&color=orange)](https://www.spigotmc.org/resources/124328/)
[![Version](https://img.shields.io/spiget/version/124328?color=brightgreen)](https://www.spigotmc.org/resources/124328/)
[![Support on Buy Me a Coffee](https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-ffdd00?logo=buymeacoffee)](https://buymeacoffee.com/epsilonlabs)

**Invy** は、マイクラサーバーに軽量で簡単に導入できるカスタムアイテム管理用プラグインです。  
設定ファイルから定義したアイテムをGUIで呼び出し、手軽に召喚・使用できます。

**GUIからカスタムアイテムの呼び出し**
![GUIからカスタムアイテムの呼び出し時の画像](imgs/summon_items.gif)

**アイテムを使う様子**   
![カスタムアイテムを使う様子時の画像](imgs/use_items.gif)

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

| コマンド                          | 説明                  |
|-------------------------------|---------------------|
| `/invy`                       | バージョンやヘルプ表示         |
| `/invy gui`                   | アイテム一覧GUIを開く        |
| `/invy reload`                | 設定とitems.ymlを再読み込み  |
| `/invy grant <player> <time>` | 一時的に `invy.use` を付与 |

## 権限一覧

| 権限            | 説明                 |
|---------------|--------------------|
| `invy.use`    | GUIからアイテムを召喚可能     |
| `invy.reload` | 設定を再読み込み可能         |
| `invy.grant`  | 他プレイヤーに一時的な権限を付与可能 |

## アイテムの作り方（items.yml）

`plugins/Invy/items.yml` にどんなアイテムを召喚したいかを書くだけで、 GUIに表示され、すぐに使えるようになります。  
最大で**54番**スロットまでに登録できます。
サンプルは [items.yml](src/main/resources/items.yml) を確認してください。

### 注意事項
- 本プラグインでは、Spigot に存在する `Enchantment` や `Attribute` や `PotionEffect` などの機能を自由に利用できますが、**すべての組み合わせに対して動作確認を行っているわけではありません**。
- エンチャントと属性は重複可能ですが、 **ポーション効果は完全に別のメタデータなので重複できません**。
- 使用環境やバージョンによっては、一部の素材や属性で**正常に動作しない**、または**アイテムデータが破損する**可能性もあります。
- 万が一予期しない挙動やデータ破損が発生した場合でも、**私たちはその責任を負いかねます**。

### 項目ごとの説明

| 項目               | 説明                                |
|------------------|-----------------------------------|
| `material`       | アイテムの種類 (例: `STICK`など)            |
| `name`           | 表示名 (色コード `&b` など使えます)            |
| `lore`           | アイテムの説明文                          |
| `unbreakable`    | `true`  にすると壊れません (デフォルトは`false`) |
| `enchants`       | エンチャント名とレベル                       |
| `attributes`     | 属性名とその効果の量と適用方法                   |
| `potion_effects` | ポーション効果の種類とその詳細設定                 |

`ID`と`material` は絶対に必要です。他は自由にカスタマイズしてください。  
`material` の一覧は [Spigot Material 一覧](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) を確認してください。

### エンチャントについて
`enchants` セクションを使うことで、アイテムにさまざまなエンチャントを付与できます。
攻撃・防御・補助などの様々な効果をアイテムに追加が可能です。

```yaml
enchants:
  SHARPNESS: 100
  FIRE_ASPECT: 10
  KNOCKBACK: 999
```
`enchants` セクション内に `エンチャント名: レベル` の形で記述してください。
レベルは最大 999 まで対応しています。（効果は制限ある場合があります）

#### エンチャントの例

| エンチャント名	      | 効果        | 最大Lv (通常) |
|---------------|-----------|-----------|
| `SHARPNESS`   | 攻撃力アップ    | 5　        |
| `FIRE_ASPECT` | 火を付ける     | 2	        | 
| `KNOCKBACK`   | ノックバック強化  | 2　        | 
| `PROTECTION`  | 防御力アップ    | 4         | 
| `UNBREAKING`  | 耐久性アップ    | 3	        | 
| `EFFICIENCY`  | 採掘スピードアップ | 5	        | 

- [Spigot Enchantment 一覧](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html)
- [Minecraft Wiki - エンチャント](https://ja.minecraft.wiki/w/%E3%82%A8%E3%83%B3%E3%83%81%E3%83%A3%E3%83%B3%E3%83%88)

### 属性について
`attributes` セクションを使うことで、アイテムにさまざまな能力値補正を追加できます。  
たとえば、「移動速度を上げる」「最大体力を増やす」「攻撃力を強化する」などが可能です。

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

#### 属性の基本構成
| 項目          | 説明                     |
|-------------|------------------------|
| `amount`    | 効果の量。操作方式により意味が異なります。  |
| `operation` | 効果の適用方法                |
| `slots`     | 効果を適用する装備スロット (複数指定可能) |

#### operation の種類
| 値                   | 説明                         | 計算例（元の値 = 10）                           |
|---------------------|----------------------------|-----------------------------------------|
| `ADD_NUMBER`        | 基本値に `amount` を直接加算（または減算） | `10 + amount`                           |
| `ADD_SCALAR`        | 基本値に `(base × amount)` を加算 | `10 + (10 × 0.3)` = `13`                |
| `MULTIPLY_SCALAR_1` | `(1 + amount)` を掛ける        | `10 × (1 + amount)` = `10 × 1.3` = `13` |

通常、速度系は `ADD_SCALAR`、体力や攻撃力は `ADD_NUMBER` がよく使われます。

#### slots で指定できる装備スロット一覧

| スロット名      | 説明               |
|------------|------------------|
| `HAND`     | メインハンド           |
| `OFF_HAND` | オフハンド            |
| `HEAD`     | 頭装備 (ヘルメットなど)    |
| `CHEST`    | 胴装備 (チェストプレートなど) |
| `LEGS`     | 脚装備 (レギンスなど)     |
| `FEET`     | 足装備 (ブーツなど)      |

- `slots` を指定しない場合は、デフォルトで `ANY` として動作します。
- 内部仕様上、 **同一キーで異なるスロットを併用** することにより、意図した合算にならない場合があります。


#### 属性の例
| 属性名	             | 効果       | デフォルト　 | 最小値	 | 最大値    |
|------------------|----------|--------|------|--------|
| `MOVEMENT_SPEED` | 移動速度アップ  | 0.7    | 0.0  | 1024.0 |
| `JUMP_STRENGTH`  | ジャンプ力アップ | 0.42   | 0.0	 | 32.0   |
| `ATTACK_DAMAGE`  | 攻撃力上昇    | 2.0　   | 0.0	 | 2048.0 |
| `ATTACK_SPEED`   | 攻撃間隔の短縮  | 4.0    | 0.0  | 1024.0 |
| `MAX_HEALTH`     | ハート増加    | 20.0   | 0.0  | 1024.0 |
| `ARMOR`          | 防御力増加    | 0.0    | 0.0	 | 30.0   |

- [Spigot Attribute 一覧](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html)
- [Minecraft Wiki - 属性](https://ja.minecraft.wiki/w/%E5%B1%9E%E6%80%A7)

### ポーション効果について
`potion_effects` セクションを使うことで、 ポーションに独自の効果を複数追加できます。
たとえば「常時スピードアップ」や「攻撃時に弱体化を付与」などのカスタム効果を設定できます。　
ポーション効果は `material` が　`POTION`, `SPLASH_POTION`, `LINGERING_POTION` のみに有効です

```yaml
potion_effects:
  SPEED:
    duration: 60
    level: 99
    ambient: true
    particles: true
    icon: true
```

#### ポーション効果の基本構成

| 項目          | 説明               |
|-------------|------------------|
| `SPEED`     | ポーション効果の種類。      |
| `duration`  | 効果時間（秒単位）        |
| `level`     | 効果レベル            |
| `ambient`   | 効果のパーティクルを少なくするか |
| `particles` | 効果のパーティクルを表示するか  |
| `icon`      | 画面右上にアイコンを表示するか  |

#### 持続時間について
- `duration` は秒単位で指定してください。
- 実際には内部的に **tick単位** (1秒 = 20tick) で処理されています。
- Bukkit/Minecraftの仕様上、**最大で** `2,147,483,647 tick`（**約 24.8 日**）まで指定可能です。
- 実用上は `9999999` (約 2.7 日) または `-1` にすれば、 **"ほぼ無限"** として利用できます。

#### ポーション効果の一覧
公式ドキュメントまたは Wiki を参照してください

- [Spigot PotionEffectType 一覧](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html)
- [Minecraft Wiki - ステータス効果](https://ja.minecraft.wiki/w/%E3%82%B9%E3%83%86%E3%83%BC%E3%82%BF%E3%82%B9%E5%8A%B9%E6%9E%9C)

## 設定について (config.yml)
`plugins/Invy/config.yml` では、プラグインの動作や言語設定をカスタマイズできます。

### 言語設定

```yaml
lang: ja
```

- `ja`: 日本語で表示されます。
- `en`: 英語で表示されます。
- `custom`: `message.yml` が自動生成され、内容を自由に編集できます。

### 機能設定

```yaml
features:
  prevent_drop: true
  prevent_storage: true
  prevent_death_drop: true
```

| 項目名	                 | 内容                                                                                        |
|----------------------|-------------------------------------------------------------------------------------------|
| `prevent_drop`       | プレイヤーがカスタムアイテムを地面にドロップできなくなります。                                                           |
| `prevent_storage`    | 	カスタムアイテムを **チェスト** / **シュルカーボックス** / **樽** に入れられなくなります。<br>※**エンダーチェスト**は例外として許可 されています。 |
| `prevent_death_drop` | プレイヤーが死亡してもカスタムアイテムがドロップされなくなります。                                                         |

`features` セクションを使うことで、簡単にゲームバランスの調整や不正対策ができます。

## ご支援のお願い

このプラグインは、**合同会社イプシロン・ラボ**により運営・開発をしています。  
いただいたご支援は、開発・保守などに活用させていただきます。

<a href="https://www.buymeacoffee.com/epsilonlabs" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Coffee" width="200" />
</a>

## ライセンス
このプロジェクトは [MITライセンス](LICENSE) のもとで公開されています。
