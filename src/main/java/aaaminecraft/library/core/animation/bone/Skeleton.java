package aaaminecraft.library.core.animation.bone;

import java.util.HashMap;
import java.util.Map;

public class Skeleton {

    private final Bone root;

    private final Map<String, Bone> bones = new HashMap<>();

    public Skeleton(Bone root) {
        this.root = root;
        registerBone(root);
    }

    public Bone getRoot() {
        return root;
    }

    public Bone getBone(String name) {
        return bones.get(name);
    }

    public boolean containsBone(String name) {
        return bones.containsKey(name);
    }

    public void registerBone(Bone bone) {
        bones.put(bone.getName(), bone);
        for (Bone child : bone.getChildren()) {
            registerBone(child);
        }
    }
}
