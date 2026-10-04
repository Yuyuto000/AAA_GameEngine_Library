package aaaminecraft.library.core.transform;

/**
 * 3次元回転を表現するQuaternion。
 *
 * <p>
 * AAA Gaming Engine Library Core におけるQuaternionは、
 * MinecraftやBlenderなどの外部環境に依存しない純粋な数学型として扱う。
 * </p>
 *
 * <p>
 * 成分構成:
 *
 * <pre>
 * Quaternion = (x, y, z, w)
 *
 * x, y, z = imaginary components / 虚部
 * w       = real component      / 実部
 * </pre>
 *
 * <p>
 * Identity Quaternion:
 *
 * <pre>
 * (0, 0, 0, 1)
 * </pre>
 *
 * </p>
 *
 * <p>
 * 回転を表現するQuaternionは単位Quaternionでなければならない。
 * </p>
 *
 * <p>
 * 回転の合成順序は以下で固定する。
 *
 * <pre>
 * WorldRotation = ParentRotation * LocalRotation
 * </pre>
 *
 * Quaternion multiplication is non-commutative.
 * Quaternionの乗算は交換法則を満たさない。
 * </p>
 *
 * <p>
 * NaNおよびInfinityは有効な値として認めない。
 * 不正な値が入力された場合はTransformExceptionを発生させる。
 * </p>
 *
 * <p>
 * 注意:
 * このクラスはmutableである。
 * getterは内部状態を直接公開しない。
 * </p>
 */
public class Quaternion {

    // ============================================================
    // Fields / フィールド
    // ============================================================

    private float x;
    private float y;
    private float z;
    private float w;


    // ============================================================
    // Constructors / コンストラクタ
    // ============================================================

    /**
     * Identity Quaternionを生成する。
     *
     * <pre>
     * (0, 0, 0, 1)
     * </pre>
     */
    public Quaternion() {
        this(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * 指定された成分からQuaternionを生成する。
     *
     * <p>
     * このコンストラクタは「回転Quaternion」を自動的に正規化しない。
     * 入力された数学値をそのまま保持する。
     * </p>
     *
     * @param x imaginary X component / 虚部X成分
     * @param y imaginary Y component / 虚部Y成分
     * @param z imaginary Z component / 虚部Z成分
     * @param w real component / 実部
     */
    public Quaternion(float x, float y, float z, float w) {
        validateComponent(x, "x");
        validateComponent(y, "y");
        validateComponent(z, "z");
        validateComponent(w, "w");

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * 既存のQuaternionをコピーする。
     *
     * @param other source quaternion / コピー元Quaternion
     */
    public Quaternion(Quaternion other) {
        if (other == null) {
            throw new TransformException("QUAT-001", "[Quaternion] Source quaternion cannot be null. /" + "コピー元のQuaternionにnullは指定できません。");
        }

        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }


    // ============================================================
    // Factory Methods / ファクトリメソッド
    // ============================================================

    /**
     * Identity Quaternionを生成する。
     *
     * @return identity quaternion / 単位Quaternion
     */
    public static Quaternion identity() {
        return new Quaternion(
                0.0f,
                0.0f,
                0.0f,
                1.0f
        );
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

    public float getW() {
        return w;
    }


    // ============================================================
    // Setters / セッター
    // ============================================================

    /**
     * すべての成分を設定する。
     *
     * <p>
     * 自動正規化は行わない。
     * </p>
     *
     * @return this
     */
    public Quaternion set(float x, float y, float z, float w) {
        validateComponent(x, "x");
        validateComponent(y, "y");
        validateComponent(z, "z");
        validateComponent(w, "w");

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }

    /**
     * 他のQuaternionの値をコピーする。
     *
     * @param other source quaternion / コピー元Quaternion
     * @return this
     */
    public Quaternion set(Quaternion other) {
        requireOther(other, "QUAT-002");

        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;

        return this;
    }


    // ============================================================
    // Copy / コピー
    // ============================================================

    /**
     * このQuaternionの独立したコピーを生成する。
     *
     * @return copied quaternion / コピーされたQuaternion
     */
    public Quaternion copy() {
        return new Quaternion(this);
    }


    // ============================================================
    // Length / 長さ
    // ============================================================

    /**
     * Quaternionのノルムの二乗を返す。
     *
     * <pre>
     * |q|² = x² + y² + z² + w²
     * </pre>
     *
     * @return squared magnitude / ノルムの二乗
     */
    public float lengthSquared() {
        float result = x * x + y * y + z * z + w * w;

        if (!MathConstants.isFinite(result)) {
            throw new TransformException("QUAT-003", "[Quaternion] Quaternion length squared became non-finite. /" + "Quaternionのノルム二乗が有限値ではなくなりました。");
        }

        return result;
    }

    /**
     * Quaternionのノルムを返す。
     *
     * @return magnitude / ノルム
     */
    public float length() {
        float lengthSquared = lengthSquared();
        float result = (float) Math.sqrt(lengthSquared);

        if (!MathConstants.isFinite(result)) {
            throw new TransformException("QUAT-004", "[Quaternion] Quaternion length became non-finite. /" + "Quaternionのノルムが有限値ではなくなりました。");
        }

        return result;
    }


    // ============================================================
    // Normalization / 正規化
    // ============================================================

    /**
     * Quaternionを単位Quaternionへ正規化する。
     *
     * <pre>
     * q_normalized = q / |q|
     * </pre>
     *
     * <p>
     * ゼロまたは極めて小さいQuaternionは正規化できない。
     * その場合、Identity Quaternionへ勝手に置き換えることはしない。
     * </p>
     *
     * @return this
     */
    public Quaternion normalize() {
        float lengthSquared = lengthSquared();

        if (lengthSquared <= MathConstants.QUATERNION_ZERO_LENGTH_EPSILON_SQUARED) {
            throw new TransformException("QUAT-005", "[Quaternion] Cannot normalize a zero or near-zero quaternion. /" + "ゼロQuaternionまたは極めて小さいQuaternionは正規化できません。");
        }

        float inverseLength =
                1.0f / (float) Math.sqrt(lengthSquared);

        if (!MathConstants.isFinite(inverseLength)) {
            throw new TransformException("QUAT-006", "[Quaternion] Normalization produced a non-finite inverse length. /" + "Quaternionの正規化中に有限ではない逆ノルムが生成されました。");
        }

        return set(
                x * inverseLength,
                y * inverseLength,
                z * inverseLength,
                w * inverseLength
        );
    }

    /**
     * 正規化されたQuaternionのコピーを生成する。
     *
     * @return normalized copy / 正規化されたコピー
     */
    public Quaternion normalized() {
        return copy().normalize();
    }

    /**
     * このQuaternionが単位Quaternionか確認する。
     *
     * <p>
     * ノルム二乗が1に近いかをMathConstantsの
     * NORMALIZED_QUATERNION_EPSILONで判定する。
     * </p>
     *
     * @return true if normalized / 単位Quaternionならtrue
     */
    public boolean isNormalized() {
        return MathConstants.approximatelyEqual(lengthSquared(), 1.0f, MathConstants.NORMALIZED_QUATERNION_EPSILON, MathConstants.NORMALIZED_QUATERNION_EPSILON);
    }


    // ============================================================
    // Conjugate / 共役
    // ============================================================

    /**
     * Quaternionの共役を生成する。
     *
     * <pre>
     * conjugate(q) = (-x, -y, -z, w)
     * </pre>
     *
     * <p>
     * 共役は必ずしも逆Quaternionではない。
     * 単位Quaternionの場合に限り、
     *
     * <pre>
     * q^-1 = conjugate(q)
     * </pre>
     *
     * となる。
     * </p>
     *
     * @return conjugated quaternion / 共役Quaternion
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }


    // ============================================================
    // Inverse / 逆Quaternion
    // ============================================================

    /**
     * Quaternionの逆Quaternionを生成する。
     *
     * <pre>
     * q^-1 = conjugate(q) / |q|²
     * </pre>
     *
     * <p>
     * 単位Quaternionでない場合でも数学的に正しい逆を求める。
     * </p>
     *
     * @return inverse quaternion / 逆Quaternion
     */
    public Quaternion inverse() {
        float lengthSquared = lengthSquared();

        if (lengthSquared <= MathConstants.QUATERNION_ZERO_LENGTH_EPSILON_SQUARED) {
            throw new TransformException("QUAT-007", "[Quaternion] Cannot invert a zero or near-zero quaternion. /" + "ゼロQuaternionまたは極めて小さいQuaternionは逆Quaternionを求められません。");
        }

        float inverseLengthSquared = 1.0f / lengthSquared;

        if (!MathConstants.isFinite(inverseLengthSquared)) {
            throw new TransformException("QUAT-008", "[Quaternion] Quaternion inverse produced a non-finite value. /" + "Quaternionの逆算中に有限ではない値が生成されました。");
        }

        return new Quaternion(
                -x * inverseLengthSquared,
                -y * inverseLengthSquared,
                -z * inverseLengthSquared,
                w * inverseLengthSquared
        );
    }


    // ============================================================
    // Quaternion Multiplication / Quaternion乗算
    // ============================================================

    /**
     * Hamilton積によってQuaternionを乗算する。
     *
     * <pre>
     * this = this * other
     * </pre>
     *
     * <p>
     * Hamilton product:
     *
     * <pre>
     * x = w1*x2 + x1*w2 + y1*z2 - z1*y2
     * y = w1*y2 - x1*z2 + y1*w2 + z1*x2
     * z = w1*z2 + x1*y2 - y1*x2 + z1*w2
     * w = w1*w2 - x1*x2 - y1*y2 - z1*z2
     * </pre>
     *
     * </p>
     *
     * <p>
     * 回転合成では以下の規約を採用する。
     *
     * <pre>
     * WorldRotation = ParentRotation * LocalRotation
     * </pre>
     *
     * </p>
     *
     * @param other other quaternion / 相手Quaternion
     * @return this
     */
    public Quaternion multiply(Quaternion other) {
        requireOther(other, "QUAT-009");

        float newX = w * other.x
                        + x * other.w
                        + y * other.z
                        - z * other.y;

        float newY = w * other.y
                        - x * other.z
                        + y * other.w
                        + z * other.x;

        float newZ = w * other.z
                        + x * other.y
                        - y * other.x
                        + z * other.w;

        float newW = w * other.w
                        - x * other.x
                        - y * other.y
                        - z * other.z;

        return set(newX, newY, newZ, newW);
    }

    /**
     * thisを変更せず、Quaternion乗算結果を生成する。
     *
     * @param other other quaternion / 相手Quaternion
     * @return multiplication result / 乗算結果
     */
    public Quaternion multiplied(Quaternion other) {
        return copy().multiply(other);
    }


    // ============================================================
    // Dot Product / 内積
    // ============================================================

    /**
     * Quaternion同士の内積を計算する。
     *
     * <pre>
     * dot = x1*x2 + y1*y2 + z1*z2 + w1*w2
     * </pre>
     *
     * @param other other quaternion / 相手Quaternion
     * @return dot product / 内積
     */
    public float dot(Quaternion other) {
        requireOther(other, "QUAT-010");

        float result = x * other.x + y * other.y + z * other.z + w * other.w;

        if (!MathConstants.isFinite(result)) {
            throw new TransformException("QUAT-011", "[Quaternion] Dot product became non-finite. /" + "Quaternionの内積が有限値ではなくなりました。");
        }

        return result;
    }


    // ============================================================
    // Rotation / 回転
    // ============================================================

    /**
     * Vector3をこのQuaternionで回転させる。
     *
     * <pre>
     * v' = q * v * q^-1
     * </pre>
     *
     * <p>
     * 回転Quaternionとして使用するため、
     * このQuaternionは単位Quaternionでなければならない。
     * </p>
     *
     * <p>
     * 非正規化Quaternionを自動的に正規化することはしない。
     * </p>
     *
     * @param vector vector to rotate / 回転対象Vector3
     * @return rotated vector / 回転後Vector3
     */
    public Vector3 rotate(Vector3 vector) {
        if (vector == null) {
            throw new TransformException("QUAT-012", "[Quaternion] Vector to rotate cannot be null. /" + "回転対象のVector3にnullは指定できません。");
        }
        requireNormalized("QUAT-013");

        /*
         * Optimized form of:
         *
         * v' = q * v * q^-1
         *
         * q must be normalized.
         *
         * 正規化済みQuaternionを前提とした高速な回転計算。
         */
        float vx = vector.getX();
        float vy = vector.getY();
        float vz = vector.getZ();

        float tx = 2.0f * (y * vz - z * vy);
        float ty = 2.0f * (z * vx - x * vz);
        float tz = 2.0f * (x * vy - y * vx);

        float resultX = vx + w * tx + (y * tz - z * ty);
        float resultY = vy + w * ty + (z * tx - x * tz);
        float resultZ = vz + w * tz + (x * ty - y * tx);

        return new Vector3(
                resultX,
                resultY,
                resultZ
        );
    }


    // ============================================================
    // Interpolation / 補間
    // ============================================================

    /**
     * Quaternionの線形補間を行う。
     *
     * <pre>
     * result = this + (target - this) * t
     * </pre>
     *
     * <p>
     * このメソッドは一般的な線形補間であり、
     * 結果が必ず単位Quaternionになるとは限らない。
     * </p>
     *
     * @param target target quaternion / 補間先
     * @param t interpolation parameter / 補間係数
     * @return this
     */
    public Quaternion lerp(Quaternion target, float t) {
        requireOther(target, "QUAT-014");
        validateComponent(t, "t");

        return set(
                x + (target.x - x) * t,
                y + (target.y - y) * t,
                z + (target.z - z) * t,
                w + (target.w - w) * t
        );
    }

    /**
     * Quaternionの球面線形補間を行う。
     *
     * <p>
     * Spherical Linear Interpolation。
     * 回転Quaternionの補間に使用する。
     * </p>
     *
     * <p>
     * Quaternionは q と -q が同じ回転を表すため、
     * dot < 0 の場合はtarget側の符号を反転して
     * 最短経路を使用する。
     * </p>
     *
     * <p>
     * 補間結果は単位Quaternionになる。
     * </p>
     *
     * @param target target rotation / 補間先回転
     * @param t interpolation parameter / 補間係数
     * @return this
     */
    public Quaternion slerp(Quaternion target, float t) {

        requireOther(target, "QUAT-015");
        validateComponent(t, "t");
        requireNormalized("QUAT-016");

        if (!target.isNormalized()) {
            throw new TransformException("QUAT-017", "[Quaternion] SLERP target must be normalized. /" + "SLERPの補間先Quaternionは正規化されていなければなりません。");
        }

        float dot = dot(target);

        /*
         * q and -q represent the same rotation.
         * qと-qは同一の回転を表すため、最短経路を選択する。
         */
        Quaternion adjustedTarget = target;

        if (dot < 0.0f) {
            dot = -dot;

            adjustedTarget = new Quaternion(
                    -target.x,
                    -target.y,
                    -target.z,
                    -target.w
            );
        }

        /*
         * Floating-point error may push dot slightly outside [-1, 1].
         * 浮動小数点誤差による微小な範囲外を補正する。
         */
        dot = MathConstants.clampUnit(dot);

        /*
         * When the angle is very small, SLERP becomes numerically
         * unstable. Use normalized LERP instead.
         *
         * 角度が非常に小さい場合はSLERPの数値安定性が低下するため、
         * 正規化したLERPへ切り替える。
         */
        if (dot >= MathConstants.QUATERNION_SLERP_LINEAR_THRESHOLD) {
            return set(
                    x + (adjustedTarget.x - x) * t,
                    y + (adjustedTarget.y - y) * t,
                    z + (adjustedTarget.z - z) * t,
                    w + (adjustedTarget.w - w) * t
            ).normalize();
        }

        double angle = Math.acos(dot);
        double sinAngle = Math.sin(angle);

        if (Math.abs(sinAngle) <= MathConstants.TRIGONOMETRIC_EPSILON) {
            throw new TransformException("QUAT-018", "[Quaternion] SLERP encountered an unstable sine value. /" + "SLERP計算中に不安定なsin値が検出されました。");
        }

        float weightA = (float) (Math.sin((1.0 - t) * angle) / sinAngle);
        float weightB = (float) (Math.sin(t * angle) / sinAngle);

        return set(
                x * weightA + adjustedTarget.x * weightB,
                y * weightA + adjustedTarget.y * weightB,
                z * weightA + adjustedTarget.z * weightB,
                w * weightA + adjustedTarget.w * weightB
        ).normalize();
    }

    /**
     * SLERPによる新しいQuaternionを生成する。
     *
     * <p>
     * 元のQuaternionは変更されない。
     * </p>
     *
     * @param target target rotation / 補間先回転
     * @param t interpolation parameter / 補間係数
     * @return interpolated rotation / 補間結果
     */
    public Quaternion slerped(Quaternion target, float t) {
        return copy().slerp(target, t);
    }


    // ============================================================
    // Angle / 回転角
    // ============================================================

    /**
     * このQuaternionが表す回転角をラジアンで取得する。
     *
     * <p>
     * Quaternionは単位Quaternionでなければならない。
     * </p>
     *
     * <pre>
     * angle = 2 * acos(w)
     * </pre>
     *
     * <p>
     * acosの入力値は浮動小数点誤差によって
     * [-1, 1]からわずかに外れる可能性があるため、
     * clampUnit()を通してから計算する。
     * </p>
     *
     * @return rotation angle in radians / 回転角（ラジアン）
     */
    public float angleRadians() {
        requireNormalized("QUAT-019");

        float clampedW = MathConstants.clampUnit(w);

        return 2.0f * (float) Math.acos(clampedW);
    }


    // ============================================================
    // Equality / 近似比較
    // ============================================================

    /**
     * Quaternionが近似的に等しいか確認する。
     *
     * <p>
     * Quaternionの数学的な回転としては、
     *
     * <pre>
     * q == -q
     * </pre>
     *
     * が成立する。
     *
     * このメソッドでは、その回転表現の同値性も考慮する。
     * </p>
     *
     * @param other other quaternion / 相手Quaternion
     * @param epsilon tolerance / 許容誤差
     * @return true if approximately equivalent
     */
    public boolean approximatelyEquals(Quaternion other, float epsilon) {
        requireOther(other, "QUAT-020");

        /*
         * Direct comparison:
         * q ≈ p
         */
        boolean direct = MathConstants.approximatelyEqual(x, other.x, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                        && MathConstants.approximatelyEqual(y, other.y, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                        && MathConstants.approximatelyEqual(z, other.z, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                        && MathConstants.approximatelyEqual(w, other.w, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON);

        if (direct) {
            return true;
        }

        /*
         * Equivalent rotation:
         * q ≈ -p
         */
        return MathConstants.approximatelyEqual(x, -other.x, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                && MathConstants.approximatelyEqual(y, -other.y, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                && MathConstants.approximatelyEqual(z, -other.z, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON)
                && MathConstants.approximatelyEqual(w, -other.w, epsilon, MathConstants.NORMALIZED_QUATERNION_EPSILON);
    }


    // ============================================================
    // Validation / 検証
    // ============================================================

    /**
     * Quaternionが有限値だけで構成されているか確認する。
     *
     * @return true if all components are finite
     */
    public boolean isFinite() {
        return MathConstants.isFinite(x)
                && MathConstants.isFinite(y)
                && MathConstants.isFinite(z)
                && MathConstants.isFinite(w);
    }

    /**
     * Quaternionが単位Quaternionであることを要求する。
     */
    private void requireNormalized(String errorCode) {
        if (!isNormalized()) {
            throw new TransformException(errorCode, "[Quaternion] Rotation operation requires a normalized quaternion. /" + "回転演算には正規化されたQuaternionが必要です。" + " Current length squared: " + lengthSquared());
        }
    }

    /**
     * Quaternion成分が有限値であることを検証する。
     */
    private static void validateComponent(float value, String componentName) {
        if (!MathConstants.isFinite(value)) {
            throw new TransformException("QUAT-021", "[Quaternion] Component '" + componentName + "' must be finite. Received: " + value + ". /" + "成分 '" + componentName + "' には有限値のみ指定できます。入力値: " + value);
        }
    }

    /**
     * 他のQuaternionがnullでないことを確認する。
     */
    private static void requireOther(Quaternion other, String errorCode) {
        if (other == null) {
            throw new TransformException(errorCode, "[Quaternion] Other quaternion cannot be null. /" + "演算対象のQuaternionにnullは指定できません。");
        }
    }


    // ============================================================
    // Object Methods / Object関連
    // ============================================================

    @Override
    public String toString() {
        return "Quaternion{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }
}