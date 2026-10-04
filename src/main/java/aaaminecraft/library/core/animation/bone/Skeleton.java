package aaaminecraft.library.core.animation.bone;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Bone階層全体を管理するSkeleton。
 *
 * <p>
 * Skeleton生成時に階層を検証し、Bone名を一意に登録する。
 * 生成完了後は階層を固定し、登録表との不整合を防止する。
 * </p>
 */
public class Skeleton {

    private static final String ERROR_PREFIX = "[AAA-SKELETON]";

    private final Bone root;
    private final Map<String, Bone> bones;

    public Skeleton(Bone root) {

        if (root == null) {
            throw new BoneException("SKELETON-001", ERROR_PREFIX + " Root Bone cannot be null. " + "/ Root Boneにnullは指定できません。");
        }

        if (root.getParent() != null) {
            throw new BoneException("SKELETON-002", ERROR_PREFIX + " Root Bone must not have a parent. " + "/ Root Boneに親Boneが存在してはいけません。");
        }

        this.root = root;
        Map<String, Bone> registeredBones = new HashMap<>();
        Set<Bone> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        registerHierarchy(root, null, registeredBones, visited);
        this.bones = Collections.unmodifiableMap(registeredBones);

        // Freeze only after the hierarchy has passed validation.
        root.freezeHierarchy();
    }

    public Bone getRoot() {
        return root;
    }

    /**
     * 名前でBoneを取得する。
     *
     * @throws BoneException 指定名のBoneが存在しない場合
     */
    public Bone getBone(String name) {

        validateBoneName(name);
        Bone bone = bones.get(name);

        if (bone == null) {
            throw new BoneException("SKELETON-003", ERROR_PREFIX + " Bone not found: " + name + " / 指定されたBoneが見つかりません。");
        }

        return bone;
    }

    /**
     * Boneが存在する場合に取得する。
     * 存在しない場合はOptional.empty()を返す。
     */
    public Optional<Bone> findBone(String name) {

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(bones.get(name));
    }

    public boolean containsBone(String name) {
        return name != null && bones.containsKey(name);
    }

    public int getBoneCount() {
        return bones.size();
    }

    /**
     * 読み取り専用のBone登録表を返す。
     */
    public Map<String, Bone> getBones() {
        return bones;
    }

    /**
     * Rootから全BoneのSkeleton Space Transformを更新する。
     */
    public void updateWorldTransforms() {
        root.updateWorldTransform();
    }

    private static void registerHierarchy(Bone bone, Bone expectedParent, Map<String, Bone> registeredBones, Set<Bone> visited) {

        if (bone == null) {
            throw new BoneException("SKELETON-004", ERROR_PREFIX + " Null Bone detected in hierarchy. " + "/ Bone階層内にnullが検出されました。");
        }

        if (!visited.add(bone)) {
            throw new BoneException("SKELETON-005", ERROR_PREFIX + " Duplicate reference or circular hierarchy detected. " + "/ Boneの重複参照または循環階層を検出しました。");
        }

        if (bone.getParent() != expectedParent) {
            throw new BoneException("SKELETON-006", ERROR_PREFIX + " Parent-child relationship is inconsistent for Bone: " + bone.getName() + " / Boneの親子関係が一致しません。");
        }

        Bone previous = registeredBones.putIfAbsent(
                bone.getName(),
                bone
        );

        if (previous != null) {
            throw new BoneException("SKELETON-007", ERROR_PREFIX + " Duplicate Bone name: " + bone.getName() + " / Bone名が重複しています。");
        }

        for (Bone child : bone.getChildren()) {
            registerHierarchy(child, bone, registeredBones, visited);
        }
    }

    private static void validateBoneName(String name) {
        if (name == null || name.isBlank()) {
            throw new BoneException("SKELETON-008", ERROR_PREFIX + " Bone name cannot be null or blank. " + "/ Bone名にnullまたは空文字は指定できません。");
        }
    }
}