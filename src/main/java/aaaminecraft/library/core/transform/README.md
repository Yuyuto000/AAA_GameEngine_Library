# AAA Gaming Engine Library

## Core Transform Mathematics Specification

### 数学基盤仕様規約 v1.0

---

## 0. 目的

本仕様は、`aaaminecraft.library.core` における3次元数学基盤を定義する。

対象とする主要型は以下とする。

```text
aaaminecraft.library.core.transform
├── Vector3
├── Quaternion
├── Transform
├── MathConstants
└── TransformException
```

本仕様の目的は、以下を保証することである。

1. 3次元ベクトル演算の数学的定義を固定する。
2. Quaternionによる3次元回転の数学的定義を固定する。
3. Skeleton、Animation、IK、Retargeting等が同一の数学モデルを共有できるようにする。
4. Blender、Minecraft、将来のUnreal Engine等との座標系変換をCoreから分離する。
5. NaN、Infinity、ゼロ除算、不正Quaternion等を早期検出する。
6. 浮動小数点誤差を考慮した比較規則を定義する。
7. 後続システムが「暗黙の数学的仮定」に依存しないようにする。
8. 数学演算とゲーム固有の仕様を明確に分離する。

---

# 1. 最重要原則

## 1.1 Core MathematicsはMinecraftを知らない

`Vector3`、`Quaternion`、`Transform`その他Core数学型は、Minecraft固有の座標系・単位・ModelPart・PoseStack等を一切知らない。

禁止：

```java
vector.toMinecraft();
quaternion.toMinecraft();
```

CoreからMinecraftへの変換はMinecraft Adapter層が担当する。

```text
AAA Core
    ↓
Minecraft Adapter
    ↓
Minecraft
```

---

## 1.2 Core MathematicsはBlenderも知らない

Blenderの座標系・FBX・GLTF・Blender Bone等はCore数学型の責務ではない。

BlenderからAAA Coreへの変換はImporter / Loader層が担当する。

---

## 1.3 数学値とゲーム上の意味を分離する

`Vector3`は3次元ベクトルそのものを表す。

以下を同一型で表現することは許可する。

```text
Position
Direction
Scale
Velocity
Normal
Offset
```

ただし、各値の意味は上位型・変数名・Transform等によって明示する。

---

# 2. 数値型

## 2.1 基本型

Core Mathematicsでは原則としてIEEE 754 binary32 `float`を使用する。

理由：

* Minecraft / Javaゲーム環境との親和性
* GPU側データとの親和性
* メモリ使用量
* アニメーション用途での十分な精度

`double`への変更は個別の高精度計算が必要となった場合のみ検討する。

---

# 3. 浮動小数点値の基本規約

## 3.1 NaN禁止

Core Mathematicsの公開状態にNaNを保持してはならない。

禁止：

```text
x = NaN
y = NaN
z = NaN
```

---

## 3.2 Infinity禁止

Core Mathematicsの公開状態に正負Infinityを保持してはならない。

禁止：

```text
+Infinity
-Infinity
```

---

## 3.3 有限値検証

全ての外部入力値について、必要な境界で

```java
Float.isFinite(value)
```

を使用する。

不正値は黙って0やIdentityへ変換してはならない。

---

# 4. 数値誤差

## 4.1 Exact Equality禁止

浮動小数点値について、数学的に同一であることを要求しない限り、

```java
a == b
```

による近似判定を行わない。

---

## 4.2 Epsilon比較

近似比較には明示的なepsilonを使用する。

概念：

```text
|a - b| <= epsilon
```

Vector3については各成分を比較する。

QuaternionについてはQuaternion特有の符号同値性を考慮する。

---

## 4.3 Epsilonを用途ごとに分離する

以下を同一epsilonとして扱わない。

```text
ZERO_TEST_EPSILON
NORMALIZATION_EPSILON
EQUALITY_EPSILON
ANGLE_EPSILON
INTERPOLATION_EPSILON
```

実際の数値は実装前に確定する。

各epsilonには用途をコメントで明記する。

---

# 5. Vector3仕様

## 5.1 数学的定義

Vector3は実数3次元ベクトル

```text
v = (x, y, z)
```

を表現する。

---

## 5.2 成分

```text
x
y
z
```

の3成分を持つ。

---

## 5.3 基本加算

```text
a + b =
(
    ax + bx,
    ay + by,
    az + bz
)
```

---

## 5.4 基本減算

```text
a - b =
(
    ax - bx,
    ay - by,
    az - bz
)
```

API名は必ず

```java
subtract()
```

とする。

`subtrack()`は禁止。

---

## 5.5 スカラー乗算

```text
v * s =
(
    x*s,
    y*s,
    z*s
)
```

---

## 5.6 成分積

Vector3同士の乗算は、通常のベクトル乗算ではなくHadamard積として定義する。

```text
a ⊙ b =
(
    ax*bx,
    ay*by,
    az*bz
)
```

API名は曖昧な

```java
multiply(Vector3)
```

ではなく、

```java
multiplyComponents(Vector3)
```

を推奨する。

---

# 6. Vector3 長さ

## 6.1 Length

```text
|v| = sqrt(x² + y² + z²)
```

---

## 6.2 Squared Length

```text
|v|² = x² + y² + z²
```

`lengthSquared()`を提供する。

sqrtが不要な比較では`lengthSquared()`を優先する。

---

# 7. Vector3 正規化

## 7.1 定義

非零ベクトルについて、

```text
normalize(v) = v / |v|
```

とする。

---

## 7.2 Zero Vector

```text
(0,0,0)
```

の正規化は未定義。

ゼロベクトルを自動的に

```text
(0,0,0)
```

へ返す処理は禁止。

Identity等への自動変換も禁止。

`TransformException`を発生させる。

---

# 8. Vector3 内積

```text
a · b =
ax*bx + ay*by + az*bz
```

API：

```java
dot(Vector3 other)
```

---

# 9. Vector3 外積

右手系を前提とし、

```text
a × b =
(
    ay*bz - az*by,
    az*bx - ax*bz,
    ax*by - ay*bx
)
```

とする。

基本関係：

```text
UNIT_X × UNIT_Y = UNIT_Z
```

を必須テストとする。

---

# 10. Vector3 距離

```text
distance(a,b) = |a-b|
```

および

```text
distanceSquared(a,b) = |a-b|²
```

を提供する。

---

# 11. Vector3 線形補間

```text
lerp(a,b,t)
=
a + (b-a)t
```

を基本定義とする。

`t`の範囲についてはAPIごとに、

```text
unclamped
clamped
```

を明示する。

勝手にclampして入力値を変更してはならない。

---

# 12. Vector3 単位ベクトル

以下の論理的単位ベクトルを定義する。

```text
ZERO
ONE
UNIT_X
UNIT_Y
UNIT_Z
```

ただしmutableなstaticインスタンスを直接公開してはならない。

---

# 13. Vector3 Mutation Policy

Vector3内部実装はmutableであってもよい。

ただし、上位クラスが内部状態を直接公開してはならない。

例えば、

```java
getPosition()
```

で内部Vector3そのものを返してはならない。

必要に応じてcopyを返す。

---

# 14. Quaternion仕様

## 14.1 数学的定義

Quaternionを

```text
q = (x,y,z,w)
```

とする。

ここで、

```text
(x,y,z)
```

は虚部、

```text
w
```

は実部とする。

---

# 15. Quaternion Identity

Identity Quaternionは、

```text
I = (0,0,0,1)
```

とする。

これは無回転を表す。

また、

```text
(0,0,0,-1)
```

も同一回転を表す。

---

# 16. Quaternion Norm

```text
|q| = sqrt(x²+y²+z²+w²)
```

Squared Norm：

```text
|q|² = x²+y²+z²+w²
```

を提供する。

---

# 17. Unit Quaternion

回転を表すQuaternionは、

```text
|q| = 1
```

を満たすものとする。

Core内で「rotation quaternion」として使用するQuaternionは、原則Unit Quaternionであることを要求する。

---

# 18. Quaternion正規化

```text
normalize(q) = q / |q|
```

ただし、

```text
|q| <= NORMALIZATION_EPSILON
```

の場合は例外。

Identityへの自動変換は禁止。

---

# 19. Quaternion積

Quaternion積はHamilton Productとする。

```text
q1 * q2
```

を、

```text
x =
w1*x2 + x1*w2 + y1*z2 - z1*y2

y =
w1*y2 - x1*z2 + y1*w2 + z1*x2

z =
w1*z2 + x1*y2 - y1*x2 + z1*w2

w =
w1*w2 - x1*x2 - y1*y2 - z1*z2
```

と定義する。

Quaternion積は可換ではない。

```text
q1*q2 != q2*q1
```

を前提とする。

---

# 20. 回転合成順序

AAA Animation Coreでは、

```text
WorldRotation = ParentRotation * LocalRotation
```

を基本規約とする。

したがって、

```java
parentRotation.multiply(localRotation)
```

は、

```text
親回転を適用した後にローカル回転を適用する
```

というTransform階層の数学モデルとして扱う。

この順序を勝手に変更してはならない。

---

# 21. Quaternion 共役

```text
conjugate(q) =
(
    -x,
    -y,
    -z,
    w
)
```

---

# 22. Quaternion 逆元

一般Quaternionについて、

```text
q⁻¹ = conjugate(q) / |q|²
```

とする。

Unit Quaternionの場合、

```text
q⁻¹ = conjugate(q)
```

となる。

`conjugate()`と`inverse()`を同一APIとして扱ってはならない。

---

# 23. Vector Rotation

QuaternionによるVector3の回転は、

```text
v' = q * v * q⁻¹
```

として定義する。

ここでVector3は、

```text
v = (vx,vy,vz,0)
```

という純虚Quaternionとして扱う。

---

# 24. Rotation Quaternionの正規化

`rotate()`は非正規化Quaternionによる誤った回転を黙って実行してはならない。

実装上、

```text
normalized rotation
```

を保証するか、

明示的に検証して例外を発生させる。

---

# 25. Quaternion 符号同値性

以下は同じ回転を表す。

```text
q
-q
```

したがって、

```text
(x,y,z,w)
```

と

```text
(-x,-y,-z,-w)
```

を回転比較で異なる姿勢として扱ってはならない。

---

# 26. Quaternion Dot Product

```text
dot(a,b)
=
ax*bx + ay*by + az*bz + aw*bw
```

を提供する。

Quaternionの角度比較・SLERP等に使用する。

---

# 27. Quaternion角度

Unit Quaternion `a`,`b`について、

```text
cos(theta/2) = |dot(a,b)|
```

を基本とする。

符号同値性を考慮するため、必要に応じてdotの絶対値を使用する。

---

# 28. Quaternion Lerp

Quaternionの線形補間は単純な成分補間ではなく、

1. Dot Product確認
2. Shortest Path判定
3. 必要なら片方のQuaternionを反転
4. Linear Interpolation
5. Normalize

の順序を基本とする。

---

# 29. Quaternion SLERP

SLERPを正式な回転補間として提供する。

概念：

```text
slerp(q1,q2,t)
```

はUnit Quaternion間の球面線形補間とする。

`dot`が1に極端に近い場合には数値安定性のためLinear Interpolationへフォールバックしてよい。

フォールバック条件は定数化する。

---

# 30. Euler Angle

Euler AngleはQuaternionの内部表現として使用しない。

Euler Angleは外部システムとの互換・デバッグ・Minecraft `ModelPart`等の末端用途に限定する。

---

# 31. Euler Angle 単位

Coreで扱うEuler Angleは**radian**とする。

Degreesへの変換は明示的なAPIを使用する。

---

# 32. Euler Rotation Order

Euler変換では回転順序を明示する。

単に

```java
toEuler()
```

だけで順序を暗黙化してはならない。

最低限、

```text
EulerRotationOrder
```

を導入可能な設計にする。

現在のMinecraft Adapterで必要となるEuler順序は、Minecraftの実装仕様と照合したうえで決定する。

---

# 33. Gimbal Lock

Euler Angle変換はGimbal Lockを完全には回避できない。

したがって、

```text
AAA Animation
→ Euler
→ AAA Animation
```

という往復をアニメーション内部で行ってはならない。

内部アニメーション状態はQuaternionを基本とする。

---

# 34. Euler変換時のClamp

`asin()`等の入力値について浮動小数点誤差により、

```text
1.0000001
-1.0000001
```

等が発生する可能性がある。

そのため、数学的に[-1,1]へ収まるべき値については適切にClampしてから逆三角関数へ渡す。

---

# 35. Transform仕様

Vector3とQuaternionを直接Boneごとに個別管理するのではなく、論理的なTransformを定義する。

```text
Transform
├── position : Vector3
├── rotation : Quaternion
└── scale    : Vector3
```

---

# 36. TransformのLocal定義

Local Transformは親Boneに対する相対Transformとする。

```text
LocalPosition
LocalRotation
LocalScale
```

を持つ。

---

# 37. TransformのWorld / Skeleton Space

Skeleton内では、

```text
Root
 ↓
Parent
 ↓
Child
```

の階層を辿ってTransformを合成する。

ただし、ここでいうWorld Transformは必ずしもゲーム世界のWorld座標ではない。

AAA Skeleton単体では、

```text
Skeleton Space
```

という名称を優先する。

Minecraft World Spaceとは明確に区別する。

---

# 38. Transform階層合成

一般的なTRSについて、

```text
Parent
+
Local
→
Child/Skeleton Space
```

を生成する。

基本的な位置計算：

```text
WorldPosition =
ParentPosition
+
ParentRotation(
    ParentScale ⊙ LocalPosition
)
```

回転：

```text
WorldRotation =
ParentRotation * LocalRotation
```

Scale：

```text
WorldScale =
ParentScale ⊙ LocalScale
```

---

# 39. Non-Uniform Scale

非一様Scaleと回転の組み合わせでは、一般のAffine TransformにおいてShearが発生し得る。

単純な

```text
Position + Quaternion + Scale
```

だけでは全てのAffine Transformを完全表現できない。

したがって現在のTransformモデルでは、

> Skeleton / Character Animation用途においてShearを正式サポートしない。

とする。

Shearが必要になった場合は将来的にMatrix4 / Matrix3x4等の導入を検討する。

---

# 40. Scaleの禁止値

Scaleについて、

```text
(0,0,0)
```

または極端に小さい値を無条件に正常Transformとして扱わない。

Bone Transformでは、ゼロScaleによる逆変換不能問題を避けるため、必要な箇所で検証する。

ただし負Scaleは数学的には必ずしも不正ではないため、全面禁止しない。

---

# 41. 座標系

AAA Coreでは右手系を基本とする。

基準軸：

```text
X = Right
Y = Up
Z = Forward
```

とする。

ただし、「Forward」の具体的な正方向についてはCore全体で一意に定義し、各Importer / Adapterが変換する。

---

# 42. Core Unit

Core内部の位置単位はゲーム固有単位ではなく、物理的な長さとして扱う。

基本仕様：

```text
1.0 = 1 meter
```

とする。

Minecraftの1 Block等への変換はAdapter側で行う。

---

# 43. Minecraft Scale変換

Minecraft側で必要となる

```text
1 meter → 16 Minecraft model units
```

等の変換はMinecraft Adapterに限定する。

Core Vector3が16倍処理を持ってはならない。

---

# 44. API設計原則

数学APIは以下を優先する。

```text
明確性
数学的一貫性
安全性
デバッグ容易性
予測可能性
```

短いコードより優先する。

---

# 45. Silent Correction禁止

以下のような自動修正は禁止する。

```text
NaN → 0
Infinity → 0
Zero Quaternion → Identity
Zero Vector normalize → Zero
Invalid rotation → Identity
```

異常値を正常値に変換すると、原因が消失するためである。

---

# 46. Copy Policy

Vector3 / Quaternion / Transformのgetterについて、内部mutable状態を直接外部へ公開してはならない。

原則：

```text
getPosition()
→ copy

getRotation()
→ copy

getScale()
→ copy
```

とする。

内部パフォーマンスが問題になった場合は、後から専用Mutable APIを設計する。

---

# 47. Exception仕様

数学基盤では、

```text
TransformException
```

を基底例外として使用する。

エラーコードを保持する。

例：

```text
VECTOR-001
VECTOR-002
QUATERNION-001
QUATERNION-002
TRANSFORM-001
```

メッセージは、

```text
English
Japanese
```

の両方を含める。

---

# 48. 例外コード例

```text
VECTOR-001
Vector contains NaN or Infinity.
ベクトルにNaNまたはInfinityが含まれています。

VECTOR-002
Cannot normalize a zero-length vector.
ゼロ長のベクトルは正規化できません。

QUATERNION-001
Quaternion contains NaN or Infinity.
クォータニオンにNaNまたはInfinityが含まれています。

QUATERNION-002
Cannot normalize a zero-length quaternion.
ゼロ長クォータニオンは正規化できません。

QUATERNION-003
Rotation requires a normalized quaternion.
回転演算には正規化されたクォータニオンが必要です。

TRANSFORM-001
Transform contains an invalid component.
Transformに不正な成分が含まれています。
```

---

# 49. Test Specification

数学基盤は単体テストを必須とする。

最低限以下をテストする。

## Vector3

```text
ZERO
UNIT_X
UNIT_Y
UNIT_Z

addition
subtraction
scalar multiplication
component multiplication

dot
cross

length
lengthSquared

normalize

distance
distanceSquared

lerp

epsilon comparison

NaN rejection
Infinity rejection
zero normalization rejection
```

---

# 50. Vector3数学的不変条件

必須：

```text
UNIT_X × UNIT_Y = UNIT_Z
UNIT_Y × UNIT_Z = UNIT_X
UNIT_Z × UNIT_X = UNIT_Y
```

また、

```text
v · (v × w) ≈ 0
w · (v × w) ≈ 0
```

を満たすこと。

---

# 51. Quaternion Test Specification

必須：

```text
Identity
Normalization
Conjugate
Inverse
Multiply
Rotate
Dot
Lerp
Slerp
Euler conversion
```

---

# 52. Quaternion不変条件

Unit Quaternion `q`について、

```text
q * identity = q
identity * q = q
```

および、

```text
q * inverse(q) ≈ identity
```

を満たすこと。

---

# 53. Rotation Test

```text
Quaternion.identity().rotate(v) ≈ v
```

を必須とする。

また、

```text
rotation.inverse().rotate(
    rotation.rotate(v)
) ≈ v
```

を満たすこと。

---

# 54. Quaternion符号テスト

```text
q
```

と

```text
-q
```

が同一回転として判定されること。

---

# 55. Quaternion Composition Test

```text
q1.rotate(q2.rotate(v))
```

と、

```text
(q1 * q2).rotate(v)
```

が定義した乗算規約に従って一致すること。

---

# 56. Transform Test

Root Transformについて、

```text
World = Local
```

となること。

Child Transformについて、

```text
World = Parent * Local
```

の定義に従うこと。

---

# 57. Serialization

Vector3 / Quaternionのシリアライズ形式は数学型そのものにJSON依存を持ち込まない。

JSON化はLoader / Serializer層が担当する。

---

# 58. Minecraft Adapterとの境界

数学Core：

```text
Vector3
Quaternion
Transform
```

↓

Adapter：

```text
MinecraftTransformConverter
MinecraftBoneRetargeter
MinecraftBoneApplier
```

↓

Minecraft：

```text
ModelPart
PoseStack
```

という依存方向を維持する。

逆方向の依存は禁止する。

---

# 59. Blender Adapterとの境界

```text
Blender
↓
Importer
↓
AAA Transform
```

とする。

Blender固有座標系をCoreに持ち込まない。

---

# 60. 将来のMatrix導入

現在は、

```text
Vector3
Quaternion
Transform
```

を中心とする。

将来的に以下が必要になった場合、

```text
Shear
Projection
Camera
Complex Affine Transform
Advanced Skinning
```

Matrix3 / Matrix4等を追加する。

その際も既存Transformの意味論を破壊しない。

---

# 61. 性能方針

数学演算では、

```text
安全性 > 微小な最適化
```

を初期実装方針とする。

Profilerによって実測されたボトルネックのみ最適化する。

「たぶん速いから危険なAPIにする」は禁止。

---

# 62. Allocation方針

現在のAPIでは数学的安全性と明確性を優先する。

ただしAnimation Update Loopで大量の一時オブジェクトが発生することが確認された場合、

```text
Mutable Vector
Mutable Quaternion
Scratch Buffer
Object Pool
SIMD
```

等を後から導入できるようにする。

初期段階で無意味なPoolを作らない。

---

# 63. 最終的な設計原則

AAA Mathematicsは、

```text
「正常な数字を計算する便利クラス」
```

ではなく、

```text
「Animation Engine全体が共有する数学的契約」
```

として扱う。

そのため、

* 数学的意味
* 座標系
* 単位
* 精度
* epsilon
* 正規化
* Quaternionの符号同値性
* 回転合成順序
* Euler順序
* 異常値
* 例外
* Mutation
* Copy
* Transform階層
* テスト不変条件

を全て仕様として固定する。

この仕様を満たさない実装は、たとえ一見正しく動作していてもAAA Coreの正式実装とは認めない。

---

# 64. 実装順序

実装は以下の順序で行う。

```text
1. MathConstants
2. TransformException
3. Vector3
4. Quaternion
5. Transform
6. Vector3 / Quaternion Unit Test
7. BoneTransformer
8. Bone
9. Skeleton
10. SkeletonLoader
11. Animation Clip
12. Retargeting
13. Minecraft Adapter
```

上位層を下位数学基盤より先に完成扱いにしない。

---

# 65. Versioning

本仕様を

```text
AAA Transform Mathematics Specification v1.0
```

として扱う。

数学的意味論を変更する場合はMinor変更ではなく、互換性への影響を評価した上でVersionを更新する。

特に以下の変更は重大変更として扱う。

```text
Coordinate handedness
Axis meaning
Unit scale
Quaternion multiplication convention
Quaternion component convention
Euler rotation order
Transform composition order
```
