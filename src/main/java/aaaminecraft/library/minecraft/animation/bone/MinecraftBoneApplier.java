package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import aaaminecraft.library.core.animation.bone.BoneTransformer;
import aaaminecraft.library.core.transform.Vector3;
import net.minecraft.client.model.geom.ModelPart;

public class MinecraftBoneApplier {

    public void apply(BoneMappingEntry mappingEntry) {

        Bone bone = mappingEntry.sourceBone();
        ModelPart modelPart = (ModelPart) mappingEntry.target();
        BoneTransformer transformer = bone.getTransform();

        // position
        Vector3 position = transformer.getWorldPosition();

        modelPart.x = position.getX();
        modelPart.y = position.getY();
        modelPart.z = position.getZ();

        //rotation
        Vector3 rotation = transformer.getWorldRotation().toEuler();

        modelPart.xRot = rotation.getX();
        modelPart.yRot = rotation.getY();
        modelPart.zRot = rotation.getZ();

        //scale
        Vector3 scale = transformer.getWorldScale();

        modelPart.xScale = scale.getX();
        modelPart.yScale = scale.getY();
        modelPart.zScale = scale.getZ();
    }
}
