package aaaminecraft.library.core.transform;

/**
 * AAA Gaming Engine Library
 *
 * Core数学基盤で使用する数学定数および数値判定規約を一元管理する。
 *
 * <p>
 * このクラスは単なる数学定数の保管場所ではない。
 * Vector3、Quaternion、TransformなどのCore数学クラスが
 * 共通して使用する浮動小数点・近似比較・正規化・補間・
 * 三角関数入力の基準を定義する。
 * </p>
 *
 * <p>
 * 重要:
 * <ul>
 *     <li>Minecraft固有の値を定義しない。</li>
 *     <li>Blender固有の値を定義しない。</li>
 *     <li>ゲーム固有の単位変換を定義しない。</li>
 *     <li>mutableオブジェクトを保持しない。</li>
 *     <li>epsilonを各数学クラスへ直接記述しない。</li>
 *     <li>NaNおよびInfinityを正常な数学値として扱わない。</li>
 * </ul>
 * </p>
 */
public final class MathConstants {

    /*
     * ============================================================
     *  Instantiation Prevention / インスタンス化禁止
     * ============================================================
     */

    private MathConstants() {
        throw new TransformException("MATH-001", "[MathConstants] MathConstants cannot be instantiated. /" + "MathConstantsはインスタンス化できません。");
    }


    /*
     * ============================================================
     *  Mathematical Constants / 数学定数
     * ============================================================
     */

    /**
     * 円周率 π。
     *
     * <p>
     * floatとして表現可能なπ。
     * </p>
     */
    public static final float PI = (float) Math.PI;

    /**
     * 2π。
     *
     * <p>
     * 1回転分の角度をradianで表した値。
     * </p>
     */
    public static final float TWO_PI = 2.0f * PI;

    /**
     * π / 2。
     *
     * <p>
     * 90度をradianで表した値。
     * </p>
     */
    public static final float HALF_PI = 0.5f * PI;

    /**
     * π / 4。
     *
     * <p>
     * 45度をradianで表した値。
     * </p>
     */
    public static final float QUARTER_PI = 0.25f * PI;

    /**
     * 1 / π。
     */
    public static final float INV_PI = 1.0f / PI;

    /**
     * 1 / (2π)。
     */
    public static final float INV_TWO_PI = 1.0f / TWO_PI;

    /**
     * √2。
     */
    public static final float SQRT_TWO = (float) Math.sqrt(2.0);

    /**
     * 1 / √2。
     */
    public static final float INV_SQRT_TWO = 1.0f / SQRT_TWO;


    /*
     * ============================================================
     *  Angle Conversion / 角度変換
     * ============================================================
     */

    /**
     * Degree → Radian変換係数。
     *
     * <pre>
     * radians = degrees * DEG_TO_RAD
     * </pre>
     */
    public static final float DEG_TO_RAD = PI / 180.0f;

    /**
     * Radian → Degree変換係数。
     *
     * <pre>
     * degrees = radians * RAD_TO_DEG
     * </pre>
     */
    public static final float RAD_TO_DEG = 180.0f / PI;


    /*
     * ============================================================
     *  Floating Point Information / 浮動小数点情報
     * ============================================================
     */

    /**
     * 1.0fにおけるfloatの1 ULP。
     *
     * <p>
     * これはゲーム内の許容誤差ではない。
     * floatそのものの表現粒度を示す値。
     * </p>
     */
    public static final float FLOAT_ULP_AT_ONE = Math.ulp(1.0f);

    /**
     * floatにおける最小の正の正規化数。
     */
    public static final float FLOAT_MIN_NORMAL = Float.MIN_NORMAL;

    /**
     * floatにおける最小の正の値。
     *
     * <p>
     * これはsubnormalを含む。
     * </p>
     */
    public static final float FLOAT_MIN_VALUE = Float.MIN_VALUE;

    /**
     * floatで表現可能な最大有限値。
     */
    public static final float FLOAT_MAX_VALUE = Float.MAX_VALUE;


    /*
     * ============================================================
     *  General Comparison / 一般比較
     * ============================================================
     */

    /**
     * 一般的な数学演算に使用する絶対許容誤差。
     *
     * <p>
     * これはfloatそのものの精度ではなく、
     * Core数学演算における「十分近い」の基準。
     * </p>
     */
    public static final float GENERAL_ABSOLUTE_EPSILON = 1.0e-6f;

    /**
     * 一般的な相対誤差の基準。
     *
     * <p>
     * 値の大きさに応じて許容誤差を調整するために使用する。
     * </p>
     */
    public static final float GENERAL_RELATIVE_EPSILON = 1.0e-6f;

    /**
     * 一般的な絶対epsilonの二乗。
     *
     * <p>
     * distanceSquaredなど、
     * 平方値で比較できる処理に使用する。
     * </p>
     */
    public static final float GENERAL_ABSOLUTE_EPSILON_SQUARED = GENERAL_ABSOLUTE_EPSILON * GENERAL_ABSOLUTE_EPSILON;


    /*
     * ============================================================
     *  Vector3 Thresholds / Vector3用閾値
     * ============================================================
     */

    /**
     * Vector3の長さが実質的にゼロと判断される閾値。
     *
     * <p>
     * Vector3の正規化などに使用する。
     * </p>
     */
    public static final float VECTOR_ZERO_LENGTH_EPSILON = 1.0e-6f;

    /**
     * Vector3のゼロ長判定用epsilonの二乗。
     */
    public static final float VECTOR_ZERO_LENGTH_EPSILON_SQUARED = VECTOR_ZERO_LENGTH_EPSILON * VECTOR_ZERO_LENGTH_EPSILON;


    /*
     * ============================================================
     *  Quaternion Thresholds / Quaternion用閾値
     * ============================================================
     */

    /**
     * Quaternionのノルムが実質的にゼロと判断される閾値。
     *
     * <p>
     * Vector3とは数学的な意味が異なるため、
     * 独立した閾値として管理する。
     * </p>
     */
    public static final float QUATERNION_ZERO_LENGTH_EPSILON = 1.0e-6f;

    /**
     * Quaternionのゼロ長判定用epsilonの二乗。
     */
    public static final float QUATERNION_ZERO_LENGTH_EPSILON_SQUARED = QUATERNION_ZERO_LENGTH_EPSILON * QUATERNION_ZERO_LENGTH_EPSILON;

    /**
     * Unit Quaternion判定用epsilon。
     *
     * <p>
     * 判定:
     *
     * <pre>
     * abs(lengthSquared - 1) <= epsilon
     * </pre>
     * </p>
     */
    public static final float NORMALIZED_QUATERNION_EPSILON = 1.0e-5f;


    /*
     * ============================================================
     *  Quaternion Interpolation / Quaternion補間
     * ============================================================
     */

    /**
     * Quaternion SLERPをLinear Interpolationへ切り替える閾値。
     *
     * <p>
     * dot productがこの値以上の場合、
     * 2つのQuaternionは非常に近いため、
     * 数値安定性のためnormalized LERPへ切り替える。
     * </p>
     */
    public static final float QUATERNION_SLERP_LINEAR_THRESHOLD = 0.9995f;


    /*
     * ============================================================
     *  Trigonometric Safety / 三角関数安全性
     * ============================================================
     */

    /**
     * asin / acosなどへ入力する値の許容範囲を
     * 判定するためのepsilon。
     *
     * <p>
     * 本来[-1, 1]に収まるべき値が、
     * 浮動小数点誤差によって僅かに範囲外へ出た場合のみ
     * 補正を許可する。
     * </p>
     */
    public static final float TRIGONOMETRIC_EPSILON = GENERAL_ABSOLUTE_EPSILON;

    /**
     * 三角関数入力の数学的最小値。
     */
    public static final float TRIGONOMETRIC_MIN = -1.0f;

    /**
     * 三角関数入力の数学的最大値。
     */
    public static final float TRIGONOMETRIC_MAX = 1.0f;


    /*
     * ============================================================
     *  Common Scalar Values / 基本スカラー値
     * ============================================================
     */

    public static final float ZERO = 0.0f;
    public static final float ONE = 1.0f;
    public static final float NEGATIVE_ONE = -1.0f;
    public static final float HALF = 0.5f;
    public static final float TWO = 2.0f;


    /*
     * ============================================================
     *  Validation Helpers / 検証ヘルパー
     * ============================================================
     */

    /**
     * floatが有限値か判定する。
     *
     * <p>
     * NaNおよびPositive/Negative Infinityはfalse。
     * </p>
     *
     * @param value value / 値
     * @return true if finite / 有限値ならtrue
     */
    public static boolean isFinite(float value) {
        return Float.isFinite(value);
    }

    /**
     * floatがNaNか判定する。
     */
    public static boolean isNaN(float value) {
        return Float.isNaN(value);
    }

    /**
     * floatがInfinityか判定する。
     */
    public static boolean isInfinite(float value) {
        return Float.isInfinite(value);
    }


    /*
     * ============================================================
     *  Approximate Comparison / 近似比較
     * ============================================================
     */

    /**
     * 絶対誤差による近似比較を行う。
     *
     * <pre>
     * abs(a - b) <= epsilon
     * </pre>
     *
     * <p>
     * NaNおよびInfinityは比較対象として認めない。
     * epsilonが不正な場合はTransformExceptionを発生させる。
     * </p>
     */
    public static boolean approximatelyEqualAbsolute(float a, float b, float epsilon) {
        validateEpsilon(epsilon);

        if (!isFinite(a) || !isFinite(b)) {
            return false;
        }

        return Math.abs(a - b) <= epsilon;
    }

    /**
     * 相対誤差による近似比較を行う。
     *
     * <p>
     * 値の絶対的な大きさに依存しない比較を行う。
     * </p>
     *
     * <pre>
     * abs(a - b)
     *     <= epsilon * max(abs(a), abs(b))
     * </pre>
     *
     * <p>
     * 両方の値が0に近い場合は、
     * GENERAL_ABSOLUTE_EPSILONによる比較を使用する。
     * </p>
     */
    public static boolean approximatelyEqualRelative(float a, float b, float epsilon) {
        validateEpsilon(epsilon);

        if (!isFinite(a) || !isFinite(b)) {
            return false;
        }

        float absoluteDifference = Math.abs(a - b);
        float largestMagnitude = Math.max(Math.abs(a), Math.abs(b));

        if (largestMagnitude <= GENERAL_ABSOLUTE_EPSILON) {
            return absoluteDifference <= GENERAL_ABSOLUTE_EPSILON;
        }

        return absoluteDifference <= epsilon * largestMagnitude;
    }

    /**
     * 絶対誤差と相対誤差を組み合わせた近似比較。
     *
     * <pre>
     * abs(a - b)
     *     <= max(
     *         absoluteEpsilon,
     *         relativeEpsilon * max(abs(a), abs(b))
     *     )
     * </pre>
     *
     * <p>
     * 一般的なCore数学処理で使用するための
     * 最も汎用的な近似比較。
     * </p>
     */
    public static boolean approximatelyEqual(float a, float b, float absoluteEpsilon, float relativeEpsilon) {
        validateEpsilon(absoluteEpsilon);
        validateEpsilon(relativeEpsilon);

        if (!isFinite(a) || !isFinite(b)) {
            return false;
        }

        float difference = Math.abs(a - b);
        float relativeTolerance = relativeEpsilon * Math.max(Math.abs(a), Math.abs(b));
        float tolerance = Math.max(absoluteEpsilon, relativeTolerance);
        return difference <= tolerance;
    }


    /*
     * ============================================================
     *  Clamp / 範囲制限
     * ============================================================
     */

    /**
     * 値を指定範囲へ制限する。
     *
     * <p>
     * min > maxの場合は数学的に不正な範囲として例外。
     * </p>
     */
    public static float clamp(float value, float min, float max) {
        validateFinite(value, "value");
        validateFinite(min, "min");
        validateFinite(max, "max");

        if (min > max) {
            throw new TransformException("MATH-004", "[MathConstants] Clamp minimum cannot be greater " + "than maximum. /" + "Clampの最小値は最大値より大きくできません。");
        }

        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    /**
     * 値を[-1, 1]へ厳密に制限する。
     *
     * <p>
     * 通常のclampとは異なり、
     * [-1, 1]から大きく外れた値を勝手に補正しない。
     * </p>
     *
     * <p>
     * 浮動小数点誤差として許容できる範囲:
     *
     * <pre>
     * [-1 - TRIGONOMETRIC_EPSILON,
     *   1 + TRIGONOMETRIC_EPSILON]
     * </pre>
     *
     * </p>
     *
     * <p>
     * それを超えた場合は入力値そのものが不正である可能性が高いため、
     * TransformExceptionを発生させる。
     * </p>
     */
    public static float clampUnit(float value) {
        validateFinite(value, "value");

        if (value < TRIGONOMETRIC_MIN - TRIGONOMETRIC_EPSILON || value > TRIGONOMETRIC_MAX + TRIGONOMETRIC_EPSILON) {

            throw new TransformException("MATH-006", "[MathConstants] Value is outside the valid " + "unit range beyond floating-point tolerance. /" + "値が浮動小数点誤差として許容できる範囲を超えています。" + " Received: " + value);
        }

        return clamp(value, TRIGONOMETRIC_MIN, TRIGONOMETRIC_MAX);
    }


    /*
     * ============================================================
     *  Threshold Helpers / 閾値判定
     * ============================================================
     */

    /**
     * 値が一般的な意味で実質的にゼロか判定する。
     */
    public static boolean isNearlyZero(float value) {
        if (!isFinite(value)) {
            return false;
        }

        return Math.abs(value) <= GENERAL_ABSOLUTE_EPSILON;
    }

    /**
     * 値がQuaternionのUnit長1に十分近いか判定する。
     */
    public static boolean isApproximatelyOne(float value) {
        if (!isFinite(value)) {
            return false;
        }

        return Math.abs(value - ONE) <= NORMALIZED_QUATERNION_EPSILON;
    }


    /*
     * ============================================================
     *  Internal Validation / 内部検証
     * ============================================================
     */

    /**
     * epsilonが有効か検証する。
     */
    private static void validateEpsilon(float epsilon) {
        if (!isFinite(epsilon) || epsilon < ZERO) {
            throw new TransformException("MATH-002", "[MathConstants] Epsilon must be finite " + "and non-negative. / " + "Epsilonは有限値かつ0以上でなければなりません。");
        }
    }

    /**
     * 値が有限値であることを検証する。
     */
    private static void validateFinite(float value, String argumentName) {
        if (!isFinite(value)) {
            throw new TransformException(
                    "MATH-003",
                    "[MathConstants] Argument '" + argumentName + "' must be finite. /" + "引数 '" + argumentName + "' は有限値でなければなりません。" + " Received: " + value
            );
        }
    }
}