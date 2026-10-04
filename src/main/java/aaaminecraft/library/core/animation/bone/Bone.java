package aaaminecraft.library.core.animation.bone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AAA Animation SystemのSkeletonを構成する1本のBone。
 *
 * <p>
 * Boneは自身のLocal Transformと親子関係を保持する。
 * World Transformは親Boneから計算される派生値であり、
 * Minecraft World座標を直接保持するものではない。
 * </p>
 */
public class Bone {

    private static final String ERROR_PREFIX = "[AAA-BONE]";

    private final String name;
    private Bone parent;
    private final List<Bone> children;

    private final BoneTransformer transformer;
    private boolean hierarchyFrozen;

    public Bone(String name) {
        if (name == null) {
            throw new BoneException("BONE-001", ERROR_PREFIX + " Bone name cannot be null. / Bone名にnullは指定できません。");
        }

        if (name.isBlank()) {
            throw new BoneException("BONE-002", ERROR_PREFIX + " Bone name cannot be blank. / Bone名を空にすることはできません。");
        }

        this.name = name;
        this.children = new ArrayList<>();
        this.transformer = new BoneTransformer();
    }

    /**
     * Boneの名前を取得する。
     */
    public String getName() {
        return name;
    }

    /**
     * このBoneのTransformを取得する。
     *
     * <p>
     * Transformの責任はBoneTransformerが持つ。
     * Bone自身はTransformの計算処理を直接実装しない。
     * </p>
     */
    public BoneTransformer getTransform() {
        return transformer;
    }

    /**
     * 親Boneを取得する。
     *
     * @return 親Bone。Rootの場合はnull。
     */
    public Bone getParent() {
        return parent;
    }

    /**
     * 子Bone一覧を読み取り専用で取得する。
     *
     * <p>
     * 外部からSkeletonの階層構造を直接変更できないようにする。
     * </p>
     */
    public List<Bone> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * 子Boneを追加する。
     *
     * @param child 追加する子Bone
     */
    public void addChild(Bone child) {

        if (hierarchyFrozen) {
            throw new BoneException("BONE-010", ERROR_PREFIX + " Cannot modify a frozen Skeleton hierarchy. " + "/ 確定済みSkeletonの親子関係は変更できません。");
        }

        if (child == null) {
            throw new BoneException("BONE-003", ERROR_PREFIX + " Cannot add null child. / nullのBoneを子として追加できません。");
        }

        if (child == this) {
            throw new BoneException("BONE-004", ERROR_PREFIX + " A Bone cannot be its own child. / Bone自身を自分の子にはできません。");
        }

        if (children.contains(child)) {
            throw new BoneException("BONE-005", ERROR_PREFIX + " The Bone is already a child of this Bone. / このBoneはすでに子として登録されています。"
            );
        }

        if (child.parent != null) {
            throw new BoneException("BONE-006", ERROR_PREFIX + " The child Bone already has a parent. " + "/ 追加しようとしたBoneにはすでに親Boneが存在します。" + " Existing parent: " + child.parent.getName());
        }

        if (child.containsDescendant(this)) {
            throw new BoneException("BONE-007", ERROR_PREFIX + " Circular Bone hierarchy detected. " + "/ Bone階層に循環参照が発生するため追加できません。");
        }

        child.parent = this;
        children.add(child);
    }

    /**
     * 子Boneを削除する。
     *
     * @param child 削除する子Bone
     */
    public void removeChild(Bone child) {

        if (hierarchyFrozen) {
            throw new BoneException("BONE-010", ERROR_PREFIX + " Cannot modify a frozen Skeleton hierarchy. " + "/ 確定済みSkeletonの親子関係は変更できません。");
        }

        if (child == null) {
            throw new BoneException("BONE-008", ERROR_PREFIX + " Cannot remove null child. / nullのBoneを削除対象にはできません。");
        }

        if (!children.remove(child)) {
            throw new BoneException("BONE-009", ERROR_PREFIX + " The specified Bone is not a child of this Bone. " + "/ 指定されたBoneはこのBoneの子ではありません。");
        }

        child.parent = null;
    }

    /**
     * Skeleton階層のWorld TransformをRootから再帰的に更新する。
     *
     * <p>
     * このBoneがRootである場合、自身のLocal Transformを
     * Skeleton Spaceの基準Transformとして使用する。
     * </p>
     */
    public void updateWorldTransform() {

        if (parent == null) {
            transformer.updateWorldTransform();
        } else {
            transformer.updateWorldTransform(parent.getTransform());
        }

        for (Bone child : children) {
            child.updateWorldTransform();
        }
    }

    /**
     * 指定BoneがこのBone以下の子孫に存在するか確認する。
     */
    private boolean containsDescendant(Bone target) {

        if (children.contains(target)) {
            return true;
        }

        for (Bone child : children) {
            if (child.containsDescendant(target)) {
                return true;
            }
        }

        return false;
    }

    void freezeHierarchy() {
        hierarchyFrozen = true;

        for (Bone child : children) {
            child.freezeHierarchy();
        }
    }

    @Override
    public String toString() {
        return "Bone{name='" + name + "'}";
    }
}