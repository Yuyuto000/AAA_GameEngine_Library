package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import aaaminecraft.library.core.animation.bone.BoneTransformer;
import aaaminecraft.library.core.transform.Vector3;
import net.minecraft.client.model.geom.ModelPart;

public class MinecraftBoneApplier {

    private static final float MODEL_SCALE = 16.0f;

    public void apply(BoneMappingEntry mappingEntry) {

        Bone bone = mappingEntry.sourceBone();

        ModelPart modelPart =
                (ModelPart) mappingEntry.target();

        BoneTransformer transform = bone.getTransform();

        // AAA Libのワールド位置をMinecraftモデル座標に変換
        Vector3 position = transform.getWorldPosition();

        modelPart.x = position.getX() * MODEL_SCALE;
        modelPart.y = -position.getZ() * MODEL_SCALE;
        modelPart.z = -position.getY() * MODEL_SCALE;

        // 初期検証用の回転変換
        Vector3 rotation =
                transform.getWorldRotation().toEuler();

        modelPart.xRot = rotation.getX();
        modelPart.yRot = rotation.getY();
        modelPart.zRot = rotation.getZ();

        // スケール
        Vector3 scale = transform.getWorldScale();

        modelPart.xScale = scale.getX();
        modelPart.yScale = scale.getZ();
        modelPart.zScale = scale.getY();
    }
}
