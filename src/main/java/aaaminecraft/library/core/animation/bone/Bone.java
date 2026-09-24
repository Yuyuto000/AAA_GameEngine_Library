package aaaminecraft.library.core.animation.bone;

import java.util.ArrayList;
import java.util.List;

public class Bone {

    private final String name;
    private Bone parent;

    private final List<Bone> children = new ArrayList<>();

    public Bone(String name){
        this.name = name;
    }

    public String getName() {
        return name;
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