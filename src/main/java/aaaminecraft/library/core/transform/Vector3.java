package aaaminecraft.library.core.transform;

/**
 * 3次元ベクトルを表現する数学クラス。
 *
 * <p>
 * AAA Gaming Engine Library Core では、Vector3 は Minecraft や Blender など
 * 外部環境に依存しない純粋な数学データとして扱う。
 * </p>
 *
 * <p>
 * 座標系:
 * X = Right / 右
 * Y = Up / 上
 * Z = Forward / 前
 * </p>
 *
 * <p>
 * このクラスでは NaN および Infinity を有効な値として認めない。
 * 不正な値が入力された場合は TransformException を発生させる。
 * </p>
 *
 * <p>
 * 注意:
 * このクラスは mutable である。
 * ただし、外部から内部状態を直接変更できないよう、
 * getter や copy() では新しい Vector3 を返す。
 * </p>
 */
public class Vector3 {

    // ============================================================
    // Fields / フィールド
    // ============================================================

    private float x;
    private float y;
    private float z;


    // ============================================================
    // Constructors / コンストラクタ
    // ============================================================

    /**
     * ゼロベクトルを生成する。
     *
     * <p>
     * Creates the zero vector (0, 0, 0).
     * </p>
     */
    public Vector3() {
        this(0.0f, 0.0f, 0.0f);
    }

    /**
     * 指定された成分からベクトルを生成する。
     *
     * @param x X component / X成分
     * @param y Y component / Y成分
     * @param z Z component / Z成分
     */
    public Vector3(float x, float y, float z) {
        validateComponent(x, "x");
        validateComponent(y, "y");
        validateComponent(z, "z");

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * 既存のVector3をコピーして生成する。
     *
     * @param other source vector / コピー元ベクトル
     */
    public Vector3(Vector3 other) {
        if (other == null) {
            throw new TransformException("VEC-001", "[Vector3] Source vector cannot be null. /" + "コピー元のVector3にnullは指定できません。");
        }

        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }


    // ============================================================
    // Factory Methods / ファクトリメソッド
    // ============================================================

    /**
     * ゼロベクトルを生成する。
     *
     * @return (0, 0, 0)
     */
    public static Vector3 zero() {
        return new Vector3(0.0f, 0.0f, 0.0f);
    }

    /**
     * X方向の単位ベクトルを生成する。
     *
     * @return (1, 0, 0)
     */
    public static Vector3 right() {
        return new Vector3(1.0f, 0.0f, 0.0f);
    }

    /**
     * Y方向の単位ベクトルを生成する。
     *
     * @return (0, 1, 0)
     */
    public static Vector3 up() {
        return new Vector3(0.0f, 1.0f, 0.0f);
    }

    /**
     * Z方向の単位ベクトルを生成する。
     *
     * @return (0, 0, 1)
     */
    public static Vector3 forward() {
        return new Vector3(0.0f, 0.0f, 1.0f);
    }

    /**
     * X方向の負の単位ベクトルを生成する。
     *
     * @return (-1, 0, 0)
     */
    public static Vector3 left() {
        return new Vector3(-1.0f, 0.0f, 0.0f);
    }

    /**
     * Y方向の負の単位ベクトルを生成する。
     *
     * @return (0, -1, 0)
     */
    public static Vector3 down() {
        return new Vector3(0.0f, -1.0f, 0.0f);
    }

    /**
     * Z方向の負の単位ベクトルを生成する。
     *
     * @return (0, 0, -1)
     */
    public static Vector3 backward() {
        return new Vector3(0.0f, 0.0f, -1.0f);
    }


    // ============================================================
    // Getters / アクセサ
    // ============================================================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }


    // ============================================================
    // Setters / セッター
    // ============================================================

    /**
     * すべての成分を設定する。
     *
     * @param x X component / X成分
     * @param y Y component / Y成分
     * @param z Z component / Z成分
     * @return this
     */
    public Vector3 set(float x, float y, float z) {
        validateComponent(x, "x");
        validateComponent(y, "y");
        validateComponent(z, "z");

        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    /**
     * 他のVector3の値をコピーする。
     *
     * @param other source vector / コピー元ベクトル
     * @return this
     */
    public Vector3 set(Vector3 other) {
        if (other == null) {
            throw new TransformException("VEC-002", "[Vector3] Source vector cannot be null. /" + "コピー元のVector3にnullは指定できません。");
        }

        this.x = other.x;
        this.y = other.y;
        this.z = other.z;

        return this;
    }


    // ============================================================
    // Copy / コピー
    // ============================================================

    /**
     * このベクトルの独立したコピーを生成する。
     *
     * @return copied vector / コピーされたベクトル
     */
    public Vector3 copy() {
        return new Vector3(this);
    }


    // ============================================================
    // Basic Arithmetic / 基本演算
    // ============================================================

    /**
     * ベクトル加算。
     *
     * <p>
     * this = this + other
     * </p>
     *
     * @param other vector to add / 加算するベクトル
     * @return this
     */
    public Vector3 add(Vector3 other) {
        requireOther(other, "VEC-003");

        return set(
                x + other.x,
                y + other.y,
                z + other.z
        );
    }

    /**
     * ベクトル減算。
     *
     * <p>
     * this = this - other
     * </p>
     *
     * @param other vector to subtract / 減算するベクトル
     * @return this
     */
    public Vector3 subtract(Vector3 other) {
        requireOther(other, "VEC-004");

        return set(
                x - other.x,
                y - other.y,
                z - other.z
        );
    }

    /**
     * スカラー倍。
     *
     * <p>
     * this = this * scalar
     * </p>
     *
     * @param scalar scalar value / スカラー値
     * @return this
     */
    public Vector3 multiply(float scalar) {
        validateComponent(scalar, "scalar");

        return set(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    /**
     * 成分ごとの乗算。
     *
     * <p>
     * this = this ⊙ other
     * </p>
     *
     * <p>
     * This is Hadamard multiplication.
     * 通常のベクトル積ではなく、各成分を個別に乗算する。
     * </p>
     *
     * @param other vector / 相手ベクトル
     * @return this
     */
    public Vector3 multiplyComponents(Vector3 other) {
        requireOther(other, "VEC-005");

        return set(
                x * other.x,
                y * other.y,
                z * other.z
        );
    }


    // ============================================================
    // Length / 長さ
    // ============================================================

    /**
     * ベクトルの長さの二乗を返す。
     *
     * <p>
     * lengthSquared = x² + y² + z²
     * </p>
     *
     * <p>
     * 平方根を使用しないため、長さの比較に適している。
     * </p>
     *
     * @return squared length / 長さの二乗
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * ベクトルの長さを返す。
     *
     * <p>
     * length = √(x² + y² + z²)
     * </p>
     *
     * @return vector length / ベクトルの長さ
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }


    // ============================================================
    // Normalization / 正規化
    // ============================================================

    /**
     * このベクトルを単位ベクトルへ正規化する。
     *
     * <p>
     * this = this / |this|
     * </p>
     *
     * <p>
     * ゼロベクトル、または長さが正規化可能な範囲を下回る場合は
     * TransformExceptionを発生させる。
     * </p>
     *
     * @return this
     */
    public Vector3 normalize() {
        float lengthSquared = lengthSquared();

        if (lengthSquared <= MathConstants.VECTOR_ZERO_LENGTH_EPSILON_SQUARED) {
            throw new TransformException("VEC-006", "[Vector3] Cannot normalize a zero or near-zero vector. /" + "ゼロベクトルまたは極めて小さいベクトルは正規化できません。");
        }

        float inverseLength = 1.0f / (float) Math.sqrt(lengthSquared);

        return set(
                x * inverseLength,
                y * inverseLength,
                z * inverseLength
        );
    }

    /**
     * 正規化されたコピーを生成する。
     *
     * <p>
     * 元のVector3は変更されない。
     * </p>
     *
     * @return normalized copy / 正規化されたコピー
     */
    public Vector3 normalized() {
        return copy().normalize();
    }


    // ============================================================
    // Dot / Cross Product
    // ============================================================

    /**
     * 内積を計算する。
     *
     * <p>
     * dot = x1*x2 + y1*y2 + z1*z2
     * </p>
     *
     * @param other vector / 相手ベクトル
     * @return dot product / 内積
     */
    public float dot(Vector3 other) {
        requireOther(other, "VEC-007");

        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * 外積を計算する。
     *
     * <p>
     * このクラスでは右手系を前提とする。
     * </p>
     *
     * <p>
     * cross = this × other
     * </p>
     *
     * @param other vector / 相手ベクトル
     * @return cross product / 外積
     */
    public Vector3 cross(Vector3 other) {
        requireOther(other, "VEC-008");

        return new Vector3(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }


    // ============================================================
    // Distance / 距離
    // ============================================================

    /**
     * 2つのベクトル間の距離の二乗を返す。
     *
     * @param other other vector / 相手ベクトル
     * @return squared distance / 距離の二乗
     */
    public float distanceSquared(Vector3 other) {
        requireOther(other, "VEC-009");

        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * 2つのベクトル間の距離を返す。
     *
     * @param other other vector / 相手ベクトル
     * @return distance / 距離
     */
    public float distance(Vector3 other) {
        return (float) Math.sqrt(distanceSquared(other));
    }


    // ============================================================
    // Interpolation / 補間
    // ============================================================

    /**
     * 線形補間を行う。
     *
     * <p>
     * result = this + (target - this) * t
     * </p>
     *
     * <p>
     * t = 0 でthis、
     * t = 1 でtargetとなる。
     * </p>
     *
     * <p>
     * このメソッドは外挿も許可するため、
     * tが0～1の範囲外でもエラーにはしない。
     * </p>
     *
     * @param target target vector / 補間先
     * @param t interpolation parameter / 補間係数
     * @return interpolated vector / 補間結果
     */
    public Vector3 lerp(Vector3 target, float t) {
        requireOther(target, "VEC-010");
        validateComponent(t, "t");

        return set(
                x + (target.x - x) * t,
                y + (target.y - y) * t,
                z + (target.z - z) * t
        );
    }

    /**
     * 線形補間による新しいVector3を生成する。
     *
     * <p>
     * 元のベクトルは変更されない。
     * </p>
     *
     * @param target target vector / 補間先
     * @param t interpolation parameter / 補間係数
     * @return interpolated copy / 補間結果
     */
    public Vector3 lerped(Vector3 target, float t) {
        return copy().lerp(target, t);
    }


    // ============================================================
    // Utility / ユーティリティ
    // ============================================================

    /**
     * このベクトルの各成分が有限値か確認する。
     *
     * @return true if all components are finite
     */
    public boolean isFinite() {
        return
                MathConstants.isFinite(x)
                        && MathConstants.isFinite(y)
                        && MathConstants.isFinite(z);
    }

    /**
     * 指定されたベクトルと近似的に等しいか確認する。
     *
     * @param other other vector / 相手ベクトル
     * @param epsilon allowed error / 許容誤差
     * @return true if approximately equal
     */
    public boolean approximatelyEquals(Vector3 other, float epsilon) {
        requireOther(other, "VEC-011");

        return MathConstants.approximatelyEqual(x, other.x, epsilon, MathConstants.GENERAL_RELATIVE_EPSILON)
                && MathConstants.approximatelyEqual(y, other.y, epsilon, MathConstants.GENERAL_RELATIVE_EPSILON)
                && MathConstants.approximatelyEqual(z, other.z, epsilon, MathConstants.GENERAL_RELATIVE_EPSILON);
    }


    // ============================================================
    // Internal Validation / 内部検証
    // ============================================================

    /**
     * Vector3の成分として有効な値か検証する。
     *
     * <p>
     * NaNおよびInfinityを許可しない。
     * </p>
     */
    private static void validateComponent(float value, String componentName) {
        if (!MathConstants.isFinite(value)) {
            throw new TransformException("VEC-012", "[Vector3] Component '" + componentName + "' must be finite. Received: " + value + ". /" + "成分 '" + componentName + "' には有限値のみ指定できます。入力値: " + value);
        }
    }

    /**
     * 他のVector3がnullでないことを確認する。
     */
    private static void requireOther(Vector3 other, String errorCode) {
        if (other == null) {
            throw new TransformException(errorCode, "[Vector3] Other vector cannot be null. /" + "演算対象のVector3にnullは指定できません。");
        }
    }


    // ============================================================
    // Object Methods / Object関連
    // ============================================================

    @Override
    public String toString() {
        return "Vector3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}