package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import aaaminecraft.library.core.animation.bone.BoneTransformer;
import net.minecraft.client.model.geom.ModelPart;

public class MinecraftBoneApplier {

    public void apply(BoneMappingEntry mappingEntry) {

        Bone bone = mappingEntry.sourceBone();

        ModelPart modelPart = (ModelPart) mappingEntry.target();

        BoneTransformer transformer = bone.getTransform();

        modelPart.x = transformer.getX();;
        modelPart.y = transformer.getY();
        modelPart.z = transformer.getZ();

        modelPart.xRot = transformer.getRotX();
        modelPart.yRot = transformer.getRotY();
        modelPart.zRot = transformer.getRotZ();

        modelPart.xScale = transformer.getScaleX();
        modelPart.yScale = transformer.getScaleY();
        modelPart.zScale = transformer.getScaleZ();
    }
}
