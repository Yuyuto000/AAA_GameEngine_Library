package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;

import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import aaaminecraft.library.core.animation.bone.BoneTransformer;
import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.geom.ModelPart;
import org.slf4j.Logger;

public class MinecraftBoneApplier {

    private static final Logger LOGGER = LogUtils.getLogger();
    public void apply(BoneMappingEntry mappingEntry) {

        Bone bone = mappingEntry.sourceBone();
        ModelPart modelPart = (ModelPart) mappingEntry.target();
        BoneTransformer transform = bone.getTransform();

        // AAA側で計算されたワールド変換を取得する
        Vector3 worldPosition = transform.getWorldPosition();
        Quaternion worldRotation = transform.getWorldRotation();
        Vector3 worldScale = transform.getWorldScale();

        // Minecraftのモデル座標へ変換
        Vector3 position = MinecraftTransformConverter.toMinecraftPosition(worldPosition);
        Quaternion rotation = MinecraftTransformConverter.toMinecraftRotation(worldRotation);
        Vector3 scale = MinecraftTransformConverter.toMinecraftScale(worldScale);

        // 位置
        modelPart.setPos(position.getX(), position.getY(), position.getZ());
        LOGGER.info("[AAA Animation] Bone: {} | Position: {}, {}, {}", bone.getName(), position.getX(), position.getY(), position.getZ());

        // 回転
        Vector3 euler = rotation.toEuler();
        modelPart.xRot = euler.getX();
        modelPart.yRot = euler.getY();
        modelPart.zRot = euler.getZ();
        LOGGER.info("[AAA Animation] Bone: {} | Rot: {}, {}, {}", bone.getName(), euler.getX(), euler.getY(), euler.getZ());

        // スケール
        modelPart.xScale = scale.getX();
        modelPart.yScale = scale.getY();
        modelPart.zScale = scale.getZ();
        LOGGER.info("[AAA Animation] Bone: {} | Scale: {}, {}, {}", bone.getName(), scale.getX(), scale.getY(), scale.getZ());
    }
}