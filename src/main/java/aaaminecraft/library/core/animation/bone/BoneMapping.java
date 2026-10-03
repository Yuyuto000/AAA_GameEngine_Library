package aaaminecraft.library.core.animation.bone;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AAA Boneと外部Targetの対応関係を管理する。
 *
 * <p>
 * BoneMapping自身はMinecraftやRendererを認識しない。
 * 外部Targetが何であるかはBoneMappingEntryに委ねる。
 * </p>
 */
public class BoneMapping {

    private static final String ERROR_PREFIX = "[AAA-BONE-MAPPING]";

    private final Map<String, BoneMappingEntry> mappings = new HashMap<>();

    /**
     * BoneとTargetの対応を登録する。
     */
    public void map(Bone sourceBone, BoneMappingEntry target) {

        if (sourceBone == null) {
            throw new BoneException("BONE-MAP-001", ERROR_PREFIX + " Source Bone cannot be null. / Source Boneにnullは指定できません。");
        }

        if (target == null) {
            throw new BoneException("BONE-MAP-002", ERROR_PREFIX + " Mapping entry cannot be null. / Mapping Entryにnullは指定できません。");
        }

        String boneName = sourceBone.getName();

        if (mappings.containsKey(boneName)) {
            throw new BoneException("BONE-MAP-003", ERROR_PREFIX + " Bone mapping already exists. " + "/ このBoneにはすでにMappingが存在します。" + " Bone: " + boneName);
        }

        if (target.sourceBone() != sourceBone) {
            throw new BoneException("BONE-MAP-004", ERROR_PREFIX + " Mapping source Bone mismatch. " + "/ MappingEntryのSource Boneと登録対象Boneが一致しません。" + " Bone: " + boneName
            );
        }

        mappings.put(boneName, target);
    }

    /**
     * Mappingを取得する。
     *
     * @throws BoneException Mappingが存在しない場合
     */
    public BoneMappingEntry get(String sourceBone) {

        if (sourceBone == null || sourceBone.isBlank()) {
            throw new BoneException("BONE-MAP-005", ERROR_PREFIX + " Source Bone name cannot be null or blank. " + "/ Source Bone名にnullまたは空文字は指定できません。");
        }

        BoneMappingEntry entry = mappings.get(sourceBone);

        if (entry == null) {
            throw new BoneException("BONE-MAP-006", ERROR_PREFIX + " No mapping exists for Bone: " + sourceBone + " / 指定されたBoneのMappingが存在しません。");
        }

        return entry;
    }

    /**
     * Mappingが存在するか確認する。
     */
    public boolean contains(String sourceBone) {

        if (sourceBone == null || sourceBone.isBlank()) {
            return false;
        }

        return mappings.containsKey(sourceBone);
    }

    /**
     * 登録済みMapping数を取得する。
     */
    public int size() {
        return mappings.size();
    }

    /**
     * 読み取り専用Mapping一覧を取得する。
     */
    public Map<String, BoneMappingEntry> getMappings() {
        return Collections.unmodifiableMap(mappings);
    }
}