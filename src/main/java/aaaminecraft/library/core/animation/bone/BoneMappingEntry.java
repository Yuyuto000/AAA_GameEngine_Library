package aaaminecraft.library.core.animation.bone;

public class BoneMappingEntry {

    private final Bone sourceBone;
    private final Object target;

    public BoneMappingEntry(Bone sourceBone, Object target) {
        this.sourceBone = sourceBone;
        this.target = target;
    }

    public Bone getSourceBone() {
        return sourceBone;
    }

    public Object getTarget() {
        return target;
    }
}