package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class MinecraftPlayerBoneProvider {

    private final PlayerModel<?> playerModel;
    private static final String ERROR_PREFIX = "[AAAMINECRAFT-PLAYER_BONE_PROVIDER]";

    public MinecraftPlayerBoneProvider(PlayerModel<?> playerModel) {

        if (playerModel == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-010", ERROR_PREFIX + "PlayerModel must not be null. / PlayerModelはnullにできません。");
        }

        this.playerModel = playerModel;
    }

    /**
     * Returns the Minecraft ModelPart corresponding to an AAA bone.
     * AAAボーンに対応するMinecraftのModelPartを返します。
     *
     * Unsupported bones return null intentionally.
     * This is different from an invalid mapping.
     * サポートされていないボーンは、意図的にnullを返します。
     * これは無効なマッピングとは異なります。
     */
    public ModelPart findPart(String boneName) {

        if (boneName == null || boneName.isBlank()) {
            throw new MinecraftBoneException("MINECRAFT-BONE-011", ERROR_PREFIX + "Bone name must not be null or blank. / Bone名はnullまたは空白にできません。");
        }

        return switch (boneName) {

            case "head" -> playerModel.head;
            case "chest" -> playerModel.body;
            case "upper_arm.L" -> playerModel.leftArm;
            case "upper_arm.R" -> playerModel.rightArm;
            case "thigh.L" -> playerModel.leftLeg;
            case "thigh.R" -> playerModel.rightLeg;
            default -> null;
        };
    }

    /**
     * Creates a mapping for all AAA bones that have a direct Minecraft PlayerModel counterpart.
     * MinecraftのPlayerModelと直接対応するすべてのAAAボーンに対して、マッピングを作成します。
     */
    public BoneMapping createMapping(Bone skeleton) {

        if (skeleton == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-012", ERROR_PREFIX + "Skeleton root must not be null. / Skeleton rootはnullにできません。");
        }

        BoneMapping mapping = new BoneMapping();
        mapBone(mapping, skeleton);
        return mapping;
    }

    private void mapBone(BoneMapping mapping, Bone bone) {

        if (bone == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-013", ERROR_PREFIX + "Bone hierarchy contains a null bone. / Bone階層にnullのBoneが存在します。");
        }

        ModelPart modelPart = findPart(bone.getName());

        if (modelPart != null) {

            mapping.map(bone, new BoneMappingEntry(bone, modelPart));
        }

        for (Bone child : bone.getChildren()) {
            mapBone(mapping, child);
        }
    }
}