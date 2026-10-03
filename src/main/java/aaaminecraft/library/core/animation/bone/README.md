# AAA Gaming Engine Library
※ Created in collaboration with ChatGPT/ChatGPTとともに作成

## Bone System v2

### Coordinate / Transform / Retargeting Specification

---

## 0. この規約の目的

本規約は、Blender、AAA Gaming Engine Library、Minecraft間に存在する複数の座標系を明確に分離し、Boneの位置・回転・スケール・親子関係を一貫して扱うための正式仕様である。

本規約では、

* Blenderの都合
* Minecraftの都合
* ModelPartの内部実装
* 描画APIの都合

をLib内部へ持ち込まない。

Lib内部には独自の明確な座標規約を持たせる。

MinecraftやBlenderとの違いは、必ず境界層で変換する。

---

# 1. 基本原則

## Rule 1-1

**Lib内部の座標系を唯一の基準座標系とする。**

Blenderの座標系もMinecraftの座標系も、Lib内部の基準にはしない。

---

## Rule 1-2

**BoneはMinecraftのModelPartではない。**

Boneは「モデルを構成するための階層化された変換ノード」である。

MinecraftのModelPartはBoneの出力先の一つに過ぎない。

したがって、

```text
Bone = ModelPart
```

とは考えない。

正しくは、

```text
Bone
 ↓
Transform
 ↓
Renderer / Adapter
 ↓
Minecraft ModelPart
```

である。

---

## Rule 1-3

**Minecraft World SpaceとBone Spaceを絶対に混同しない。**

例えば、

```text
Minecraft World:
(45, 28, 69)
```

というプレイヤー位置が存在していても、

```text
AAA Skeleton:
(0, 0, 0)
```

をキャラクターの基準位置として使用できる。

これは正常である。

---

# 2. 座標空間の定義

Bone Systemでは最低4種類の空間を定義する。

```text
Minecraft World Space
        ↓
Minecraft Entity Space
        ↓
AAA Skeleton Space
        ↓
AAA Bone Local Space
```

---

## 2-1. Minecraft World Space

Minecraft世界そのものの座標。

例:

```text
X = 45
Y = 28
Z = 69
```

これはMinecraft側でのみ扱う。

LibのBoneには直接保存しない。

---

# 2-2. Minecraft Entity Space

プレイヤーなど、1つのEntityを基準にした座標空間。

Entityの位置を

```text
(0, 0, 0)
```

として扱う。

例えばMinecraft World上で、

```text
Player = (45, 28, 69)
```

だった場合、

```text
Player Root = (0, 0, 0)
```

となる。

Entityが移動してもSkeleton内部のBone位置は変化しない。

---

# 2-3. AAA Skeleton Space

Libが使用するキャラクター基準空間。

これを**Libの最重要座標系**とする。

Skeleton Rootの基準点を、

```text
(0, 0, 0)
```

とする。

Boneの位置はこのSkeleton Spaceを基準に定義される。

---

# 2-4. AAA Bone Local Space

各Boneが親Boneに対して持つ相対変換。

例えば、

```text
Chest
 └── UpperArm_R
```

の場合、

```text
UpperArm_R.position
```

はMinecraft Worldにおける位置ではない。

Chestに対する相対位置である。

---

# 3. Root規約

## Rule 3-1

Skeletonには必ず1つだけRoot Boneを存在させる。

```text
Root
```

を標準名とする。

---

## Rule 3-2

RootのLocal Transformは原則として、

```text
Position = (0, 0, 0)
Rotation = Identity
Scale    = (1, 1, 1)
```

とする。

---

## Rule 3-3

Blender側に存在する都合上のRoot回転を、そのままLibへ持ち込まない。

例えば現在存在している、

```text
90° X Rotation
```

のような補正は、

```text
Blender Coordinate Conversion
```

として処理する。

Bone自身のRest Transformへ混ぜない。

---

# 4. Blender側規約

## Rule 4-1

Blenderではキャラクターを原点付近に配置する。

キャラクターの基準点はSkeleton Rootと一致させる。

---

## Rule 4-2

BlenderのWorld PositionをそのままBone PositionとしてExportしない。

Exportするのは、

```text
Bone Local Transform
```

である。

---

## Rule 4-3

BoneのPositionは必ず親Bone基準で記録する。

概念的には、

```text
LocalTransform =
ParentInverse × BoneTransform
```

で求める。

---

## Rule 4-4

Blenderの都合による、

* Scene位置
* Collection位置
* Object位置
* Export用の一時移動
* Ground Plane位置

などはBoneデータへ混入させない。

---

# 5. 軸規約

AAA Skeleton Spaceでは以下を正式規約とする。

```text
+X = Character Right
+Y = Character Up
+Z = Character Forward
```

つまり、

```text
        +Y
        ↑
        |
        |
        +------→ +X
       /
      /
    +Z
```

をAAA標準とする。

Blender、Minecraft、その他の外部フォーマットが異なる場合、境界で変換する。

---

# 6. 単位規約

AAA Skeleton Spaceでは、

```text
1.0 = 1 AAA Unit
```

とする。

Minecraftの1ブロックやModelPartの1単位をLibの基準単位にしない。

Minecraftへの変換時に、

```text
AAA Unit
↓
Minecraft Model Unit
```

というScale Conversionを行う。

変換倍率はコード中に散在させない。

必ず、

```java
CoordinateSystem
TransformConverter
```

などの中央管理された設定から取得する。

---

# 7. Position規約

Boneには以下の3種類のPositionを区別して保持する。

```text
localPosition
worldPosition
restPosition
```

---

## 7-1. localPosition

親Boneに対する現在の相対位置。

---

## 7-2. worldPosition

Skeleton Spaceにおける累積位置。

これはMinecraft World Positionではない。

名称上の混乱を防ぐため、将来的には

```text
skeletonPosition
```

への変更も許可する。

---

## 7-3. restPosition

Animationが存在しない基準姿勢におけるPosition。

AnimationはRest Poseを基準に評価する。

---

# 8. Rotation規約

Rotationは内部ではQuaternionを使用する。

```text
Quaternion(x, y, z, w)
```

を標準とする。

Euler角をBone内部の正式データとして保存してはならない。

Euler角は、

* Blender UI
* Minecraft ModelPart
* Debug表示

など、外部境界で必要な場合のみ使用する。

---

# 9. Scale規約

通常のHumanoid Boneでは、

```text
Scale = (1, 1, 1)
```

を基本とする。

Scaleをアニメーションさせることは禁止しない。

ただし、

```text
null
NaN
Infinity
0除算
```

などの不正値はTransform適用前に拒否する。

---

# 10. Transformの親子関係

BoneのWorld Transformは必ず、

```text
Parent World Transform
        +
Bone Local Transform
```

から計算する。

概念上、

```text
WorldMatrix =
ParentWorldMatrix
×
LocalMatrix
```

とする。

Rootだけは、

```text
WorldMatrix = LocalMatrix
```

である。

---

# 11. World Transformの扱い

World Transformは派生値とする。

つまり、

```text
localPosition
localRotation
localScale
```

を変更した結果として、

```text
worldPosition
worldRotation
worldScale
```

を再計算する。

World Transformを直接編集してはいけない。

---

# 12. Animation規約

AnimationはBoneのWorld Transformそのものを書き換えない。

Animationは、

```text
Rest Pose
+
Animation Delta
```

として評価する。

例えば、

```text
Rest Rotation
```

に対して、

```text
Animation Rotation
```

を適用する。

これにより、Animationを再生してもSkeleton全体の基準座標が破壊されない。

---

# 13. Minecraft ModelPartとの接続

Minecraft ModelPartはAAA Skeletonの座標系ではない。

したがって、

```java
modelPart.setPos(...)
```

へAAA World Positionを直接投入してはならない。

必ず、

```text
AAA Skeleton Space
        ↓
Minecraft Model Space
```

というAdapterを通す。

---

# 14. Minecraft ModelPart Adapter

Minecraft Adapterは最低限、

```text
Position Conversion
Rotation Conversion
Scale Conversion
Rest Pose Conversion
Hierarchy Conversion
```

を担当する。

Bone System本体にはMinecraft固有コードを書かない。

---

# 15. Rest Pose同期

Minecraft ModelPartを利用する場合、

```text
AAA Rest Pose
```

と

```text
Minecraft Default Model Pose
```

を比較する。

AnimationそのものをMinecraftの絶対位置へ変換するのではなく、

```text
AAA Current Pose
-
AAA Rest Pose
=
Animation Delta
```

を求める。

そのDeltaをMinecraftの基準姿勢へ適用する。

概念的には、

```text
Minecraft Current Pose
=
Minecraft Rest Pose
+
Converted AAA Animation Delta
```

とする。

---

# 16. 座標変換の禁止事項

以下は禁止する。

### 禁止1

各クラスで個別に、

```java
x = -x;
y = z;
z = -y;
```

などの変換を書く。

---

### 禁止2

「見た目が合うから」という理由で、

```java
position.add(...)
```

による固定補正値を追加する。

---

### 禁止3

```java
position.multiply(16)
```

のような単位変換をRenderer内部で行う。

---

### 禁止4

Boneによって異なる座標変換を使用する。

---

### 禁止5

MinecraftのModelPartに合わせるため、BlenderのBoneそのものを歪める。

---

### 禁止6

Rootの補正を複数箇所で行う。

---

# 17. 座標変換の責任範囲

必ず以下の責任範囲を維持する。

```text
Blender
  ↓
Blender Import / Converter
  ↓
AAA Skeleton Space
  ↓
Bone System
  ↓
Minecraft Adapter
  ↓
Minecraft Model Space
  ↓
Renderer
```

---

# 18. JSON規約

Skeleton JSONには座標系情報を明示する。

最低限、

```json
{
  "coordinateSystem": {
    "up": "Y",
    "forward": "Z",
    "right": "X",
    "unitScale": 1.0
  },
  "skeleton": {
    ...
  }
}
```

という構造を持つ。

実際のJSON SchemaはLoader実装時に正式決定する。

---

# 19. Loaderの責任

Loaderは、

```text
JSON
↓
AAA Skeleton
```

への変換のみ担当する。

Loader自身がMinecraft ModelPartを知ってはいけない。

---

# 20. Boneの責任

Boneは、

* 名前
* 親
* 子
* Local Transform
* World Transform

のみを管理する。

Bone自身が、

```text
Minecraft
Blender
Forge
ModelPart
Renderer
```

を参照してはいけない。

---

# 21. Mappingの責任

BoneMappingは、

```text
AAA Bone
↓
外部Target
```

の対応関係だけを管理する。

Minecraft専用処理はMappingへ持ち込まない。

Minecraft固有の処理はMinecraft Adapter側で行う。

---

# 22. Rendererの責任

Rendererは、

```text
Transform
+
Mesh
+
Material
```

を使って描画する。

RendererはSkeletonの内部仕様を変更してはいけない。

---

# 23. Debug規約

Debug出力では必ず、

```text
Space
Bone
Parent
Local Position
World Position
Rotation
Scale
```

を区別する。

例えば、

```text
[AAA Bone]
Bone: UpperArm_R
Parent: Shoulder_R

Local:
  Position: ...

World:
  Position: ...

Rotation:
  ...

Scale:
  ...
```

のようにする。

単に、

```text
UpperArm_R Position: ...
```

と出力してはならない。

何のPositionなのか分からないログはデバッグ情報として失格。

---

# 24. テスト規約

Bone Systemは単体テストだけでは完成扱いにしない。

以下の順番でテストする。

## Level 1: Transform Test

数学的なTransformが正しいことを確認する。

---

## Level 2: Skeleton Test

親子関係とWorld Transformが正しいことを確認する。

---

## Level 3: JSON Load Test

実際のBlender出力JSONを読み込む。

---

## Level 4: Minecraft Integration Test

Minecraft内で実際のPlayerModelへ接続する。

---

## Level 5: Animation Load Test

実際のAnimationデータを読み込む。

---

## Level 6: Animation Playback Test

Minecraft上でAnimationを実際に再生する。

---

## Level 7: Visual Stability Test

実際にプレイし、

* ガクつき
* 瞬間移動
* 回転飛び
* Boneのズレ
* 足の滑り
* 左右反転
* 回転軸の異常
* Scaling異常
* Animation終了時の姿勢崩壊
* Animation切り替え時の不自然なジャンプ

を確認する。

---

# 25. 合格基準

Bone Systemの完成条件は、

```text
「コードが動いた」
```

ではない。

以下をすべて満たした場合のみ合格とする。

```text
Blender
  ↓
Export
  ↓
JSON
  ↓
Loader
  ↓
Skeleton
  ↓
Animation
  ↓
Minecraft
  ↓
Playback
```

の全工程が正常に動作すること。

さらに実プレイ中に不自然な挙動が存在しないこと。

---

# 26. 1ミリでも不自然ならFail

以下のいずれかが発生した場合は合格としない。

* 数値は合っているが見た目がズレる
* Animation開始時にBoneが跳ぶ
* Animation終了時にBoneが戻らない
* 親Bone回転によって子Boneが不自然に移動する
* 左右で挙動が反転する
* Minecraft側の姿勢とAAA側の姿勢が一致しない
* Animation切り替え時に瞬間的な位置ズレが発生する
* フレームによって位置が揺れる
* FPSによって挙動が変化する
* ScaleによってPositionが意図せず変化する
* Quaternion→Euler変換による異常が発生する

この場合、

```text
Fail
↓
原因特定
↓
仕様または実装修正
↓
全テスト再実行
```

とする。

---

# 27. 高水準テストの最終条件

最終テストでは、単なるBone表示ではなく、

```text
実際のMinecraft Player
+
実際のAAA Skeleton
+
実際のAnimation
+
実際のAnimation Playback
```

を使用する。

さらに複数Animationを連続再生する。

例えば、

```text
Idle
↓
Walk
↓
Run
↓
Attack
↓
Idle
```

のような状態遷移を実際に行う。

---

# 28. 最終的な設計思想

Bone Systemは、

```text
Minecraft ModelPartを高度に操作する仕組み
```

ではなく、

```text
独立したSkeleton Animation System
```

として設計する。

Minecraft ModelPartへの接続はAdapterで行う。

これにより将来的に、

```text
Minecraft
Unreal Engine
Custom Renderer
Mod
Standalone Tool
```

などへSkeletonシステムを移植できる。

---

# 29. 人間向け制作原則

BlenderでBoneを作る人間は、

```text
「Minecraftではどう見えるか」
```

を考えてBoneを配置してはならない。

考えるべきなのは、

```text
「このキャラクターの骨格として自然か」
```

だけである。

Minecraftとの違いはExport / Adapter層が吸収する。

---

# 30. 最終原則

AAA Bone Systemにおいて、

**「見た目を合わせるための謎の補正値」は存在してはならない。**

すべての座標変換は、

```text
定義された座標系
+
定義された単位
+
定義された原点
+
定義された親子関係
+
定義された変換行列
```

によって説明可能でなければならない。

説明できない補正値は仕様ではなくバグである。

---

# 31. 開発順序

Bone System v2の再構築は以下の順番で行う。

```text
1. Coordinate specification
2. Vector / Quaternion specification
3. Transform specification
4. Bone
5. Skeleton
6. JSON Schema
7. Skeleton Loader
8. Blender Exporter
9. Coordinate Converter
10. Minecraft Adapter
11. Rest Pose synchronization
12. Animation data
13. Animation Loader
14. Animation Playback
15. Minecraft integration
16. High-level test
17. Visual tuning
18. Final acceptance
```

途中の実装を「とりあえず動いたから完成」としない。

各段階で仕様と実装を一致させる。

---

# 完成条件

最終的に、

```text
Blenderで制作
↓
JSON Export
↓
AAA LibraryでLoad
↓
Skeleton生成
↓
Animation生成
↓
Animation再生
↓
Minecraftへ接続
↓
実プレイ
```

が一貫して成立すること。

この状態をもってBone Loading / Skeleton / Animation基盤の完成とする。
