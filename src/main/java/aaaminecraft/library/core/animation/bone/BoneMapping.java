package aaaminecraft.library.core.animation.bone;

import java.util.HashMap;
import java.util.Map;

public class BoneMapping {

    private final Map<String, BoneMappingEntry> mappings = new HashMap<>();

    public void map(Bone sourceBone, BoneMappingEntry target) {
        mappings.put(sourceBone.getName(), target);
    }

    public BoneMappingEntry get(String sourceBone) {
        return mappings.get(sourceBone);
    }

    public boolean contains(String sourceBone) {
        return mappings.containsKey(sourceBone);
    }
}