package aaaminecraft.library.core.animation.bone;

import java.util.ArrayList;
import java.util.List;

public class Bone {

    private final String name;
    private Bone parent;
    private final List<Bone> children = new ArrayList<>();

    private final BoneTransformer transformer;

    public Bone(String name){
        this.name = name;
        this.transformer = new BoneTransformer();
    }

    public String getName() {
        return name;
    }

    public BoneTransformer getTransform() {
        return transformer;
    }

    public Bone getParent() {
        return parent;
    }

    public List<Bone> getChildren() {
        return children;
    }

    public void addChild(Bone child){
        child.parent = this;
        children.add(child);
    }
}