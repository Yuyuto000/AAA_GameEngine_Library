package aaaminecraft.library.core.animation.bone;

/**
 * Boneと外部Targetの対応情報。
 *
 * @param sourceBone AAA側のBone
 * @param target      外部システム側のTarget
 */
public record BoneMappingEntry(Bone sourceBone, Object target) {

    public BoneMappingEntry {

        if (sourceBone == null) {
            throw new BoneException("BONE-MAP-ENTRY-001", "[AAA-BONE-MAPPING] Source Bone cannot be null. " + "/ Source Boneにnullは指定できません。");
        }

        if (target == null) {
            throw new BoneException("BONE-MAP-ENTRY-002", "[AAA-BONE-MAPPING] Target cannot be null. " + "/ Targetにnullは指定できません。");
        }
    }
}