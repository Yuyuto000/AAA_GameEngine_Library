package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;

public final class MinecraftTransformConverter {

    private static final float MODEL_SCALE = 16.0f;
    private static final String ERROR_PREFIX = "[AAAMINECRAFT-TRANSFORM_CONVERTER]";

    private MinecraftTransformConverter() {
        throw new AssertionError("MinecraftTransformConverter must not be instantiated./ MinecraftTransformConverter をインスタンス化してはなりません。");
    }

    /**
     * Converts a position from AAA skeleton coordinates
     * into Minecraft model coordinates.
     * AAAスケルトン座標系からの位置を、Minecraftモデル座標系に変換します。
     *
     * AAA:
     *   +X = right 左右
     *   +Y = forward　前後
     *   +Z = up　上下
     *
     * Minecraft model:
     *   +X = right 左右
     *   +Y = down 上下
     *   +Z = backward 前後
     *
     * NOTE:
     * The axis convention must be validated against the actual
     * Minecraft model and exporter pipeline.
     * 軸の定義は、実際のMinecraftモデルおよびエクスポートパイプラインに対して検証する必要があります。
     */

    public static Vector3 toMinecraftPosition(Vector3 aaaPosition) {

        requireVector(aaaPosition, "MINECRAFT-BONE-001", "Position must not be null.", "Positionはnullにできません。");

        return new Vector3(
                aaaPosition.getX() * MODEL_SCALE,
                -aaaPosition.getZ() * MODEL_SCALE,
                -aaaPosition.getY() * MODEL_SCALE
        );
    }

    /**
     * Converts an AAA rotation quaternion into the current Minecraft adapter rotation convention.
     * AAAの回転クォータニオンを、Minecraftアダプターの現在の回転規約に変換します。
     *
     * IMPORTANT:
     * This conversion is part of the current coordinate-system hypothesis and must be validated by axis tests.
     * この変換は、現在の座標系仮説の一部であり、軸テストによって検証されなければなりません。
     */
    public static Quaternion toMinecraftRotation(Quaternion aaaRotation) {

        if (aaaRotation == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-002", "Rotation must not be null. / " + "Rotationはnullにできません。");
        }

        Quaternion result = new Quaternion(
                -aaaRotation.getX(),
                aaaRotation.getZ(),
                aaaRotation.getY(),
                aaaRotation.getW()
        );

        try {
            return result.normalize();
        } catch (RuntimeException e) {
            throw new MinecraftBoneException("MINECRAFT-BONE-003", "Failed to normalize converted rotation. / " + "変換後の回転Quaternionを正規化できませんでした。", e);
        }
    }

    /**
     * Converts AAA scale into Minecraft model-axis order.
     * AAAスケールをMinecraftのモデル軸順に変換します。
     */
    public static Vector3 toMinecraftScale(Vector3 aaaScale) {

        requireVector(aaaScale, "MINECRAFT-BONE-004", "Scale must not be null.", "Scaleはnullにできません。");

        return new Vector3(
                aaaScale.getX(),
                aaaScale.getZ(),
                aaaScale.getY()
        );
    }

    private static void requireVector(Vector3 vector, String errorCode, String englishMessage, String japaneseMessage) {

        if (vector == null) {
            throw new MinecraftBoneException(errorCode, ERROR_PREFIX + englishMessage + " / " + japaneseMessage);
        }
    }
}