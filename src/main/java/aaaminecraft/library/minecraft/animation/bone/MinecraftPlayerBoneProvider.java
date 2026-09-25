package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class MinecraftPlayerBoneProvider {

    private final PlayerModel<?> playerModel;

    public MinecraftPlayerBoneProvider(PlayerModel<?> playerModel) {
        this.playerModel = playerModel;
    }

    public ModelPart getPart(String name){

        return switch (name) {
            case "head" -> playerModel.head;
            case "body" -> playerModel.body;
            case "arm.L" -> playerModel.leftArm;
            case "arm.R" -> playerModel.rightArm;
            case "leg.L" -> playerModel.leftLeg;
            case "leg.R" -> playerModel.rightLeg;
            default -> null;
        };
    }

    public BoneMapping createMapping(Bone skeleton){

        BoneMapping mapping = new BoneMapping();
        mapBone(mapping, skeleton);
        return mapping;
    }

    private void mapBone(BoneMapping mapping, Bone bone){

        ModelPart modelPart = getPart(bone.getName());

        if (modelPart != null){
            mapping.map(
                    bone,
                    new BoneMappingEntry(bone, modelPart)
            );
        }

        for (Bone child : bone.getChildren()){
            mapBone(mapping, child);
        }
    }
}
