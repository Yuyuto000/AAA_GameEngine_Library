package aaaminecraft.library.core.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;

/**
 * BoneのLocal Transform、Rest Transform、
 * およびSkeleton Spaceの累積Transformを管理する。
 *
 * <p>
 * 内部のworldPosition等はMinecraft World Spaceではなく、
 * Skeleton Spaceにおける累積変換を表す。
 * </p>
 *
 * <p>
 * Local Transformを変更した後は、Skeletonから
 * updateWorldTransform()を実行すること。
 * </p>
 */
public class BoneTransformer {

    private static final String ERROR_PREFIX = "[AAA-BONE-TRANSFORM]";

    private static final float QUATERNION_EPSILON = 1.0e-8f;
    private static final float SCALE_EPSILON = 1.0e-8f;

    // Current Local Transform
    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;

    // Immutable-by-access Rest Transform
    private Vector3 restPosition;
    private Quaternion restRotation;
    private Vector3 restScale;

    // Accumulated Skeleton Space Transform
    private Vector3 worldPosition;
    private Quaternion worldRotation;
    private Vector3 worldScale;

    public BoneTransformer() {
        position = new Vector3(0.0f, 0.0f, 0.0f);
        rotation = identityQuaternion();
        scale = new Vector3(1.0f, 1.0f, 1.0f);

        restPosition = copy(position);
        restRotation = copy(rotation);
        restScale = copy(scale);

        worldPosition = copy(position);
        worldRotation = copy(rotation);
        worldScale = copy(scale);
    }

    // --------------------------------------------------
    // Current Local Transform
    // --------------------------------------------------

    public Vector3 getPosition() {
        return copy(position);
    }

    public Quaternion getRotation() {
        return copy(rotation);
    }

    public Vector3 getScale() {
        return copy(scale);
    }

    // --------------------------------------------------
    // Rest Transform
    // --------------------------------------------------

    public Vector3 getRestPosition() {
        return copy(restPosition);
    }

    public Quaternion getRestRotation() {
        return copy(restRotation);
    }

    public Vector3 getRestScale() {
        return copy(restScale);
    }

    /**
     * Rest Poseを設定し、現在のLocal Transformも同期する。
     *
     * <p>
     * Skeletonの初期化やJSONの読み込み時に使用する。
     * アニメーション再生中にRest Poseを更新する用途ではない。
     * </p>
     */
    public void setRestTransform(Vector3 position, Quaternion rotation, Vector3 scale) {
        Vector3 checkedPosition = validateVector(position, "Rest Position");
        Quaternion checkedRotation = validateQuaternion(rotation, "Rest Rotation");
        Vector3 checkedScale = validateScale(scale, "Rest Scale");

        this.restPosition = checkedPosition;
        this.restRotation = checkedRotation;
        this.restScale = checkedScale;

        this.position = copy(checkedPosition);
        this.rotation = copy(checkedRotation);
        this.scale = copy(checkedScale);
    }

    /**
     * 現在のLocal TransformをRest Poseへ戻す。
     */
    public void resetToRestPose() {
        position = copy(restPosition);
        rotation = copy(restRotation);
        scale = copy(restScale);
    }

    // --------------------------------------------------
    // Skeleton Space Transform
    // --------------------------------------------------

    public Vector3 getWorldPosition() {
        return copy(worldPosition);
    }

    public Quaternion getWorldRotation() {
        return copy(worldRotation);
    }

    public Vector3 getWorldScale() {
        return copy(worldScale);
    }

    // --------------------------------------------------
    // Local Transform Setters
    // --------------------------------------------------

    public void setPosition(float x, float y, float z) {
        position = validateVector(new Vector3(x, y, z), "Local Position");
    }

    public void setRotation(float x, float y, float z, float w) {
        rotation = validateQuaternion(new Quaternion(x, y, z, w), "Local Rotation");
    }

    public void setScale(float x, float y, float z) {
        scale = validateScale(new Vector3(x, y, z), "Local Scale");
    }

    /**
     * 親Boneの累積Transformから、このBoneの
     * Skeleton Space Transformを計算する。
     *
     * <p>
     * このメソッドはRootには使用しない。
     * </p>
     */
    public void updateWorldTransform(BoneTransformer parent) {

        if (parent == null) {
            throw new BoneException("BONE-TRANSFORM-001", ERROR_PREFIX + " Parent transformer cannot be null. " + "/ 親のTransformにnullは指定できません。");
        }

        Vector3 parentPosition = parent.getWorldPosition();
        Quaternion parentRotation = parent.getWorldRotation();
        Vector3 parentScale = parent.getWorldScale();

        // Skeleton Space Scale
        Vector3 nextWorldScale = new Vector3(
                parentScale.getX() * scale.getX(),
                parentScale.getY() * scale.getY(),
                parentScale.getZ() * scale.getZ()
        );

        validateScale(nextWorldScale, "Skeleton Space Scale");

        // Skeleton Space Rotation
        Quaternion nextWorldRotation = copy(parentRotation).multiply(copy(rotation)).normalize();
        nextWorldRotation = validateQuaternion(nextWorldRotation, "Skeleton Space Rotation");

        // Scale the Local Position by the parent's accumulated Scale.
        Vector3 scaledLocalPosition = new Vector3(
                position.getX() * parentScale.getX(),
                position.getY() * parentScale.getY(),
                position.getZ() * parentScale.getZ()
        );

        // Rotate the scaled Local Position by the parent's Rotation.
        Vector3 rotatedLocalPosition = copy(parentRotation).rotate(scaledLocalPosition);

        Vector3 nextWorldPosition = new Vector3(
                parentPosition.getX() + rotatedLocalPosition.getX(),
                parentPosition.getY() + rotatedLocalPosition.getY(),
                parentPosition.getZ() + rotatedLocalPosition.getZ()
        );

        validateVector(nextWorldPosition, "Skeleton Space Position");

        // Commit only after all calculations have succeeded.
        worldPosition = nextWorldPosition;
        worldRotation = nextWorldRotation;
        worldScale = nextWorldScale;
    }

    /**
     * Root BoneのSkeleton Space Transformを更新する。
     *
     * <p>
     * RootのLocal TransformがそのままSkeleton Spaceの
     * Transformとなる。RootをIdentityにする規約は
     * SkeletonLoader側で検証する。
     * </p>
     */
    public void updateWorldTransform() {

        Vector3 nextPosition = validateVector(position, "Root Position");
        Quaternion nextRotation = validateQuaternion(rotation, "Root Rotation");
        Vector3 nextScale = validateScale(scale, "Root Scale");

        worldPosition = nextPosition;
        worldRotation = nextRotation;
        worldScale = nextScale;
    }

    // --------------------------------------------------
    // Validation / Copy
    // --------------------------------------------------

    private static Vector3 validateVector(Vector3 value, String field) {
        if (value == null) {
            throw new BoneException("BONE-TRANSFORM-002", ERROR_PREFIX + " " + field + " cannot be null. " + "/ " + field + "にnullは指定できません。");
        }

        float x = value.getX();
        float y = value.getY();
        float z = value.getZ();

        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            throw new BoneException("BONE-TRANSFORM-003", ERROR_PREFIX + " " + field + " contains NaN or Infinity. " + "/ " + field + "にNaNまたは無限大が含まれています。");
        }

        return new Vector3(x, y, z);
    }

    private static Vector3 validateScale(Vector3 value, String field) {
        Vector3 checked = validateVector(value, field);

        if (Math.abs(checked.getX()) < SCALE_EPSILON
                || Math.abs(checked.getY()) < SCALE_EPSILON
                || Math.abs(checked.getZ()) < SCALE_EPSILON) {

            throw new BoneException("BONE-TRANSFORM-004", ERROR_PREFIX + " " + field + " contains a zero or near-zero component. " + "/ " + field + "に0または極端に小さい成分が含まれています。");
        }

        return checked;
    }

    private static Quaternion validateQuaternion(
            Quaternion value,
            String field
    ) {
        if (value == null) {
            throw new BoneException("BONE-TRANSFORM-005", ERROR_PREFIX + " " + field + " cannot be null. " + "/ " + field + "にnullは指定できません。");
        }

        float x = value.getX();
        float y = value.getY();
        float z = value.getZ();
        float w = value.getW();

        if (!Float.isFinite(x)
                || !Float.isFinite(y)
                || !Float.isFinite(z)
                || !Float.isFinite(w)) {

            throw new BoneException("BONE-TRANSFORM-006", ERROR_PREFIX + " " + field + " contains NaN or Infinity. " + "/ " + field + "にNaNまたは無限大が含まれています。");
        }

        double lengthSquared =
                (double) x * x
                        + (double) y * y
                        + (double) z * z
                        + (double) w * w;

        if (lengthSquared < QUATERNION_EPSILON) {
            throw new BoneException("BONE-TRANSFORM-007", ERROR_PREFIX + " " + field + " has zero length. " + "/ " + field + "の長さが0です。");
        }

        double inverseLength = 1.0 / Math.sqrt(lengthSquared);

        return new Quaternion(
                (float) (x * inverseLength),
                (float) (y * inverseLength),
                (float) (z * inverseLength),
                (float) (w * inverseLength)
        );
    }

    private static Vector3 copy(Vector3 value) {
        return new Vector3(
                value.getX(),
                value.getY(),
                value.getZ()
        );
    }

    private static Quaternion copy(Quaternion value) {
        return new Quaternion(
                value.getX(),
                value.getY(),
                value.getZ(),
                value.getW()
        );
    }

    private static Quaternion identityQuaternion() {
        return new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    }
}