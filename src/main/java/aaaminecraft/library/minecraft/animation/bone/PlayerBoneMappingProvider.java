package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class PlayerBoneMappingProvider {

    public BoneMapping create(Bone root, PlayerModel<?> playerModel) {
        BoneMapping mapping = new BoneMapping();

        map(mapping, root, "Chest", playerModel.body);
        map(mapping, root, "Head", playerModel.head);
        map(mapping, root, "UpperArm_R", playerModel.rightArm);
        map(mapping, root, "UpperArm_L", playerModel.leftArm);
        map(mapping, root, "Thigh_R", playerModel.rightLeg);
        map(mapping, root, "Thigh_L", playerModel.leftLeg);

        return mapping;
    }

    private void map(BoneMapping mapping, Bone root, String boneName, ModelPart target) {
        Bone bone = findBone(root, boneName);

        if (bone != null) {
            mapping.map(bone, new BoneMappingEntry(bone, target));
        }
    }

    private Bone findBone(Bone bone, String name) {
        if (bone.getName().equals(name)) {
            return bone;
        }

        for (Bone child : bone.getChildren()) {
            Bone found = findBone(child, name);

            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
