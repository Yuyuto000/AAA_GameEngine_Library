package aaaminecraft.library.core.transform;

/**
 * 3次元空間における位置・回転・スケールを統合して表現するTransform。
 *
 * <p>
 * AAA Gaming Engine Library Core におけるTransformは、
 * MinecraftやBlenderなどの外部環境に依存しない純粋な数学型として扱う。
 * </p>
 *
 * <p>
 * Transformは以下の3要素から構成される。
 *
 * <pre>
 * Transform = (Position, Rotation, Scale)
 * </pre>
 *
 * <ul>
 *     <li>Position  = Vector3</li>
 *     <li>Rotation  = normalized Quaternion</li>
 *     <li>Scale     = Vector3</li>
 * </ul>
 * </p>
 *
 * <p>
 * Core座標系:
 *
 * <pre>
 * X = Right / 右
 * Y = Up / 上
 * Z = Forward / 前
 * </pre>
 * </p>
 *
 * <p>
 * Core内部の基本単位:
 *
 * <pre>
 * 1.0 = 1 meter
 * </pre>
 *
 * 外部環境との座標系・単位系の変換はAdapter層の責務であり、
 * Transform自身はMinecraftやBlenderの仕様を認識しない。
 * </p>
 *
 * <p>
 * Transformによる点の変換は以下の順序で行われる。
 *
 * <pre>
 * Scale → Rotation → Translation
 *
 * p' = R(S ⊙ p) + P
 * </pre>
 * </p>
 *
 * <p>
 * 親TransformとローカルTransformの合成は以下の規約で固定する。
 *
 * <pre>
 * WorldTransform = ParentTransform × LocalTransform
 * </pre>
 *
 * 具体的には、
 *
 * <pre>
 * WorldPosition =
 *     ParentPosition
 *     + ParentRotation(
 *         ParentScale ⊙ LocalPosition
 *       )
 *
 * WorldRotation =
 *     ParentRotation × LocalRotation
 *
 * WorldScale =
 *     ParentScale ⊙ LocalScale
 * </pre>
 * </p>
 *
 * <p>
 * Rotationには常に正規化されたQuaternionを要求する。
 * TransformはQuaternionを自動的に正規化しない。
 * 不正なQuaternionが指定された場合はTransformExceptionを発生させる。
 * </p>
 *
 * <p>
 * Scaleは負値を許可する。
 * ただし、Scaleの各成分が0の場合、そのTransformは逆変換不能となるため、
 * inverse()およびinverseTransform系の処理では例外を発生させる。
 * </p>
 *
 * <p>
 * このクラスはmutableである。
 * ただし、内部のVector3およびQuaternionを直接公開しない。
 * getterおよびcopy()では独立したコピーを返す。
 * </p>
 */
public class Transform {

    // ============================================================
    // Fields / フィールド
    // ============================================================

    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;


    // ============================================================
    // Constructors / コンストラクタ
    // ============================================================

    /**
     * Identity Transformを生成する。
     *
     * <pre>
     * Position = (0, 0, 0)
     * Rotation = (0, 0, 0, 1)
     * Scale    = (1, 1, 1)
     * </pre>
     */
    public Transform() {
        this(Vector3.zero(), Quaternion.identity(), new Vector3(MathConstants.ONE, MathConstants.ONE, MathConstants.ONE));
    }

    /**
     * 指定されたPosition、Rotation、ScaleからTransformを生成する。
     *
     * @param position position / 位置
     * @param rotation rotation / 回転
     * @param scale scale / スケール
     */
    public Transform(
            Vector3 position,
            Quaternion rotation,
            Vector3 scale
    ) {
        requirePosition(position);
        requireRotation(rotation);
        requireScale(scale);

        if (!rotation.isNormalized()) {
            throw new TransformException("TRANS-001", "[Transform] Rotation must be a normalized quaternion. /" + "Rotationには正規化されたQuaternionが必要です。" + " Current length squared: " + rotation.lengthSquared());
        }

        this.position = position.copy();
        this.rotation = rotation.copy();
        this.scale = scale.copy();
    }

    /**
     * 他のTransformをコピーする。
     *
     * @param other source transform / コピー元Transform
     */
    public Transform(Transform other) {
        if (other == null) {
            throw new TransformException("TRANS-002", "[Transform] Source transform cannot be null. /" + "コピー元のTransformにnullは指定できません。");
        }

        this.position = other.position.copy();
        this.rotation = other.rotation.copy();
        this.scale = other.scale.copy();
    }


    // ============================================================
    // Factory Methods / ファクトリメソッド
    // ============================================================

    /**
     * Identity Transformを生成する。
     *
     * @return identity transform / 単位Transform
     */
    public static Transform identity() {
        return new Transform();
    }


    // ============================================================
    // Getters / アクセサ
    // ============================================================

    /**
     * Positionのコピーを取得する。
     *
     * @return copied position / Positionのコピー
     */
    public Vector3 getPosition() {
        return position.copy();
    }

    /**
     * Rotationのコピーを取得する。
     *
     * @return copied rotation / Rotationのコピー
     */
    public Quaternion getRotation() {
        return rotation.copy();
    }

    /**
     * Scaleのコピーを取得する。
     *
     * @return copied scale / Scaleのコピー
     */
    public Vector3 getScale() {
        return scale.copy();
    }


    // ============================================================
    // Setters / セッター
    // ============================================================

    /**
     * Positionを設定する。
     *
     * @param position new position / 新しい位置
     * @return this
     */
    public Transform setPosition(Vector3 position) {
        requirePosition(position);
        this.position.set(position);
        return this;
    }

    /**
     * Rotationを設定する。
     *
     * <p>
     * Rotationには正規化されたQuaternionのみ指定できる。
     * 自動正規化は行わない。
     * </p>
     *
     * @param rotation new rotation / 新しい回転
     * @return this
     */
    public Transform setRotation(Quaternion rotation) {
        requireRotation(rotation);

        if (!rotation.isNormalized()) {
            throw new TransformException("TRANS-003", "[Transform] Rotation must be a normalized quaternion. /" + "Rotationには正規化されたQuaternionが必要です。" + " Current length squared: " + rotation.lengthSquared());
        }

        this.rotation.set(rotation);

        return this;
    }

    /**
     * Scaleを設定する。
     *
     * <p>
     * Scale = 0は保持可能。
     * ただし、そのTransformは逆変換できない。
     * </p>
     *
     * @param scale new scale / 新しいスケール
     * @return this
     */
    public Transform setScale(Vector3 scale) {
        requireScale(scale);
        this.scale.set(scale);

        return this;
    }

    /**
     * Position、Rotation、Scaleを一括設定する。
     *
     * <p>
     * すべての入力を検証してから内部状態を変更する。
     * </p>
     *
     * @param position new position / 新しい位置
     * @param rotation new rotation / 新しい回転
     * @param scale new scale / 新しいスケール
     * @return this
     */
    public Transform set(Vector3 position, Quaternion rotation, Vector3 scale) {
        requirePosition(position);
        requireRotation(rotation);
        requireScale(scale);

        if (!rotation.isNormalized()) {
            throw new TransformException("TRANS-004", "[Transform] Rotation must be a normalized quaternion. /" + "Rotationには正規化されたQuaternionが必要です。" + " Current length squared: " + rotation.lengthSquared());
        }

        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);

        return this;
    }


    // ============================================================
    // Identity / 単位Transform
    // ============================================================

    /**
     * このTransformをIdentity状態へ戻す。
     *
     * @return this
     */
    public Transform identityTransform() {
        position.set(
                MathConstants.ZERO,
                MathConstants.ZERO,
                MathConstants.ZERO
        );

        rotation.set(
                MathConstants.ZERO,
                MathConstants.ZERO,
                MathConstants.ZERO,
                MathConstants.ONE
        );

        scale.set(
                MathConstants.ONE,
                MathConstants.ONE,
                MathConstants.ONE
        );

        return this;
    }


    // ============================================================
    // Copy / コピー
    // ============================================================

    /**
     * このTransformの独立したコピーを生成する。
     *
     * @return copied transform / コピーされたTransform
     */
    public Transform copy() {
        return new Transform(this);
    }


    // ============================================================
    // Point Transformation / 点の変換
    // ============================================================

    /**
     * このTransformによってPointを変換する。
     *
     * <pre>
     * p' = R(S ⊙ p) + P
     * </pre>
     *
     * <p>
     * 適用順序:
     *
     * <pre>
     * Scale
     *   ↓
     * Rotation
     *   ↓
     * Translation
     * </pre>
     * </p>
     *
     * @param point point in local space / Local空間の点
     * @return point in transformed space / 変換後の点
     */
    public Vector3 transformPoint(Vector3 point) {

        requireVector(point, "TRANS-005");
        Vector3 result = point.copy();
        result.multiplyComponents(scale);
        result = rotation.rotate(result);
        result.add(position);
        return result;
    }

    /**
     * このTransformによってPointを逆変換する。
     *
     * <pre>
     * p_local =
     *     S^-1 ⊙
     *     R^-1(
     *         p_world - P
     *     )
     * </pre>
     *
     * <p>
     * Scaleに0が含まれる場合は逆変換できないため、
     * TransformExceptionを発生させる。
     * </p>
     *
     * @param point point in transformed space / 変換後空間の点
     * @return point in local space / Local空間の点
     */
    public Vector3 inverseTransformPoint(Vector3 point) {

        requireVector(point, "TRANS-006");
        requireInvertibleScale("TRANS-007");
        Vector3 result = point.copy();
        result.subtract(position);
        result = rotation.inverse().rotate(result);
        result.set(
                result.getX() / scale.getX(),
                result.getY() / scale.getY(),
                result.getZ() / scale.getZ()
        );

        return result;
    }


    // ============================================================
    // Vector Transformation / ベクトルの変換
    // ============================================================

    /**
     * このTransformによってVectorを変換する。
     *
     * <pre>
     * v' = R(S ⊙ v)
     * </pre>
     *
     * <p>
     * Translationは適用しない。
     * </p>
     *
     * @param vector vector in local space / Local空間のベクトル
     * @return transformed vector / 変換後のベクトル
     */
    public Vector3 transformVector(Vector3 vector) {
        requireVector(vector, "TRANS-008");
        Vector3 result = vector.copy();
        result.multiplyComponents(scale);
        result = rotation.rotate(result);
        return result;
    }

    /**
     * このTransformによってVectorを逆変換する。
     *
     * <pre>
     * v_local =
     *     S^-1 ⊙
     *     R^-1(v_world)
     * </pre>
     *
     * @param vector vector in transformed space / 変換後空間のベクトル
     * @return vector in local space / Local空間のベクトル
     */
    public Vector3 inverseTransformVector(Vector3 vector) {
        requireVector(vector, "TRANS-009");
        requireInvertibleScale("TRANS-010");
        Vector3 result = rotation.inverse().rotate(vector);

        result.set(
                result.getX() / scale.getX(),
                result.getY() / scale.getY(),
                result.getZ() / scale.getZ()
        );

        return result;
    }


    // ============================================================
    // Direction Transformation / 方向の変換
    // ============================================================

    /**
     * このTransformによってDirectionを変換する。
     *
     * <pre>
     * d' = R(d)
     * </pre>
     *
     * <p>
     * DirectionにはScaleおよびTranslationを適用しない。
     * また、自動正規化もしない。
     * </p>
     *
     * @param direction direction / 方向ベクトル
     * @return transformed direction / 変換後の方向
     */
    public Vector3 transformDirection(Vector3 direction) {
        requireVector(direction, "TRANS-011");
        return rotation.rotate(direction);
    }

    /**
     * このTransformによってDirectionを逆変換する。
     *
     * <pre>
     * d_local = R^-1(d_world)
     * </pre>
     *
     * @param direction transformed direction / 変換後の方向
     * @return local direction / Local空間の方向
     */
    public Vector3 inverseTransformDirection(Vector3 direction) {
        requireVector(direction, "TRANS-012");
        return rotation.inverse().rotate(direction);
    }


    // ============================================================
    // Transform Composition / Transform合成
    // ============================================================

    /**
     * 親TransformとLocal Transformを合成する。
     *
     * <p>
     * このTransformをParent、
     * 引数のTransformをLocalとして扱う。
     * </p>
     *
     * <pre>
     * result = this × localTransform
     * </pre>
     *
     * <p>
     * 合成結果:
     *
     * <pre>
     * WorldPosition =
     *     ParentPosition
     *     + ParentRotation(
     *         ParentScale ⊙ LocalPosition
     *       )
     *
     * WorldRotation =
     *     ParentRotation × LocalRotation
     *
     * WorldScale =
     *     ParentScale ⊙ LocalScale
     * </pre>
     * </p>
     *
     * @param localTransform child local transform / 子のLocal Transform
     * @return composed world transform / 合成されたWorld Transform
     */
    public Transform combine(Transform localTransform) {
        if (localTransform == null) {
            throw new TransformException("TRANS-013", "[Transform] Local transform cannot be null. /" + "Local Transformにnullは指定できません。");
        }

        Vector3 worldPosition = localTransform.position.copy();
        worldPosition.multiplyComponents(scale);
        worldPosition = rotation.rotate(worldPosition);
        worldPosition.add(position);
        Quaternion worldRotation = rotation.multiplied(localTransform.rotation);

        /*
         * Both rotations are normalized, therefore their Hamilton
         * product should also represent a unit rotation.
         *
         * 浮動小数点誤差を含む可能性があるため、
         * 最終的に正規化してTransformの不変条件を保証する。
         */
        worldRotation.normalize();
        Vector3 worldScale = scale.copy().multiplyComponents(localTransform.scale);
        return new Transform(worldPosition, worldRotation, worldScale);
    }


    // ============================================================
    // Inverse Transform / 逆Transform
    // ============================================================

    /**
     * このTransformの逆Transformを生成する。
     *
     * <pre>
     * T^-1
     * </pre>
     *
     * <p>
     * 逆Transformは以下で定義される。
     *
     * <pre>
     * InverseScale =
     *     (1/Sx, 1/Sy, 1/Sz)
     *
     * InverseRotation =
     *     Rotation^-1
     *
     * InversePosition =
     *     InverseScale ⊙
     *     InverseRotation(-Position)
     * </pre>
     * </p>
     *
     * <p>
     * Scaleのいずれかが0の場合、逆変換は数学的に定義できない。
     * </p>
     *
     * @return inverse transform / 逆Transform
     */
    public Transform inverse() {
        requireInvertibleScale("TRANS-014");

        Quaternion inverseRotation = rotation.inverse();
        Vector3 inverseScale = new Vector3(
                MathConstants.ONE / scale.getX(),
                MathConstants.ONE / scale.getY(),
                MathConstants.ONE / scale.getZ()
        );

        Vector3 inversePosition = position.copy().multiply(MathConstants.NEGATIVE_ONE);
        inversePosition = inverseRotation.rotate(inversePosition);
        inversePosition.multiplyComponents(inverseScale);

        return new Transform(inversePosition, inverseRotation, inverseScale);
    }


    // ============================================================
    // Interpolation / 補間
    // ============================================================

    /**
     * 2つのTransform間を補間する。
     *
     * <p>
     * 引数の順序を明示的に固定する。
     *
     * <pre>
     * fromTransform → toTransform
     * </pre>
     *
     * PositionとScaleにはVector3の線形補間、
     * RotationにはQuaternionのSLERPを使用する。
     * </p>
     *
     * <pre>
     * Position:
     *     Vector3.lerp
     *
     * Rotation:
     *     Quaternion.slerp
     *
     * Scale:
     *     Vector3.lerp
     * </pre>
     *
     * <p>
     * t = 0でfromTransform、
     * t = 1でtoTransformとなる。
     *
     * tは0～1の範囲外も許可し、外挿を行う。
     * </p>
     *
     * @param fromTransform interpolation start / 補間開始Transform
     * @param toTransform interpolation target / 補間終了Transform
     * @param t interpolation parameter / 補間係数
     * @return interpolated Transform / 補間されたTransform
     */
    public static Transform interpolateTransform(Transform fromTransform, Transform toTransform, float t) {
        if (fromTransform == null) {
            throw new TransformException("TRANS-015", "[Transform] From transform cannot be null. /" + "補間開始Transformにnullは指定できません。");
        }

        if (toTransform == null) {
            throw new TransformException("TRANS-016", "[Transform] To transform cannot be null. /" + "補間終了Transformにnullは指定できません。");
        }

        if (!MathConstants.isFinite(t)) {
            throw new TransformException("TRANS-017", "[Transform] Interpolation parameter must be finite. Received: " + t + ". /補間係数には有限値のみ指定できます。入力値: " + t);
        }

        Vector3 interpolatedPosition = fromTransform.position.copy().lerp(toTransform.position, t);
        Quaternion interpolatedRotation = fromTransform.rotation.copy().slerp(toTransform.rotation, t);
        Vector3 interpolatedScale = fromTransform.scale.copy().lerp(toTransform.scale, t);

        return new Transform(
                interpolatedPosition,
                interpolatedRotation,
                interpolatedScale
        );
    }


    // ============================================================
    // Validation / 検証
    // ============================================================

    /**
     * このTransformの構成要素が有限値で構成されているか確認する。
     *
     * @return true if finite / すべて有限値ならtrue
     */
    public boolean isFinite() {
        return position.isFinite()
                && rotation.isFinite()
                && scale.isFinite();
    }

    /**
     * 指定されたTransformと近似的に等しいか確認する。
     *
     * <p>
     * PositionとScaleは成分ごとの近似比較、
     * RotationはQuaternionとしての回転同値性を考慮する。
     * </p>
     *
     * @param other other transform / 相手Transform
     * @param epsilon allowed error / 許容誤差
     * @return true if approximately equal / 近似的に等しければtrue
     */
    public boolean approximatelyEquals(
            Transform other,
            float epsilon
    ) {
        if (other == null) {
            throw new TransformException("TRANS-018", "[Transform] Other transform cannot be null. /" + "比較対象のTransformにnullは指定できません。");
        }

        return position.approximatelyEquals(
                other.position,
                epsilon
        )
                && rotation.approximatelyEquals(
                other.rotation,
                epsilon
        )
                && scale.approximatelyEquals(
                other.scale,
                epsilon
        );
    }


    // ============================================================
    // Internal Validation / 内部検証
    // ============================================================

    /**
     * Positionがnullでないことを確認する。
     */
    private static void requirePosition(Vector3 position) {
        if (position == null) {
            throw new TransformException("TRANS-019", "[Transform] Position cannot be null. /" + "Positionにnullは指定できません。");
        }
    }

    /**
     * Rotationがnullでないことを確認する。
     */
    private static void requireRotation(Quaternion rotation) {
        if (rotation == null) {
            throw new TransformException("TRANS-020", "[Transform] Rotation cannot be null. /" + "Rotationにnullは指定できません。");
        }
    }

    /**
     * Scaleがnullでないことを確認する。
     */
    private static void requireScale(Vector3 scale) {
        if (scale == null) {
            throw new TransformException("TRANS-021", "[Transform] Scale cannot be null. /" + "Scaleにnullは指定できません。");
        }
    }

    /**
     * Vector3がnullでないことを確認する。
     */
    private static void requireVector(
            Vector3 vector,
            String errorCode
    ) {
        if (vector == null) {
            throw new TransformException(errorCode, "[Transform] Vector cannot be null. /" + "Vectorにnullは指定できません。");
        }
    }

    /**
     * Scaleが逆変換可能であることを確認する。
     *
     * <p>
     * Scaleのいずれかの成分が0の場合、
     * その軸の情報が失われるため逆変換できない。
     * </p>
     */
    private void requireInvertibleScale(String errorCode) {
        if (MathConstants.isNearlyZero(scale.getX())
                || MathConstants.isNearlyZero(scale.getY())
                || MathConstants.isNearlyZero(scale.getZ())) {

            throw new TransformException(errorCode, "[Transform] Transform cannot be inverted because one or more scale components are zero or near-zero. /" + "1つ以上のScale成分がゼロまたは極めて小さいため、Transformを逆変換できません。" + " Scale: " + scale);
        }
    }


    // ============================================================
    // Object Methods / Object関連
    // ============================================================

    @Override
    public String toString() {
        return "Transform{" + "position=" + position + ", rotation=" + rotation + ", scale=" + scale + '}';
    }
}