package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;

public final class MinecraftTransformConverter {

    private static final float MODEL_SCALE = 16.0f;

    private MinecraftTransformConverter() {
    }

    /**
     * Blender座標をMinecraftのモデル座標へ変換する。
     *
     * Blender:
     *   +X = 右
     *   +Y = 前
     *   +Z = 上
     *
     * Minecraftモデル:
     *   +X = 右
     *   +Y = 下
     *   +Z = 後ろ
     *
     * ※軸の向きは現在の実装仮説。
     * 実際のモデル表示で検証すること。
     */
    public static Vector3 toMinecraftPosition(Vector3 worldPosition) {
        return new Vector3(
                worldPosition.getX() * MODEL_SCALE,
                -worldPosition.getZ() * MODEL_SCALE,
                -worldPosition.getY() * MODEL_SCALE
        );
    }

    /**
     * Blender側の回転をMinecraft側の回転へ変換する。
     *
     * 座標軸の変換:
     * (x, y, z) -> (x, -z, -y)
     *
     * Quaternionのベクトル部分は、
     * この座標変換に対して (-x, z, y) となる。
     */
    public static Quaternion toMinecraftRotation(
            Quaternion worldRotation
    ) {
        return new Quaternion(
                -worldRotation.getX(),
                worldRotation.getZ(),
                worldRotation.getY(),
                worldRotation.getW()
        ).normalize();
    }

    /**
     * Blender側のスケールをMinecraft側へ変換する。
     */
    public static Vector3 toMinecraftScale(Vector3 worldScale) {
        return new Vector3(
                worldScale.getX(),
                worldScale.getZ(),
                worldScale.getY()
        );
    }
}