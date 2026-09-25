package aaaminecraft.library.core.animation.importer;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.Skeleton;

public class PlayerAnimationImporter {

    private final BoneMapping boneMapping;

    public PlayerAnimationImporter() {
        this.boneMapping = new BoneMapping();
    }

    public Skeleton createPlayerSkeleton() {

        // Root
        Bone root = new Bone("root");

        // Upper Body
        Bone pelvis = new Bone("pelvis");
        Bone spine = new Bone("spine");
        Bone chest = new Bone("chest");
        Bone neck = new Bone("neck");
        Bone head = new Bone("head");

        // Left Arm
        Bone shoulderL = new Bone("shoulder.L");
        Bone upperArmL = new Bone("upper_arm.L");
        Bone lowerArmL = new Bone("lower_arm.L");

        // Right Arm
        Bone shoulderR = new Bone("shoulder.R");
        Bone upperArmR = new Bone("upper_arm.R");
        Bone lowerArmR = new Bone("lower_arm.R");

        // Left Leg
        Bone thighL = new Bone("thigh.L");
        Bone calfL = new Bone("calf.L");

        // Right Leg
        Bone thighR = new Bone("thigh.R");
        Bone calfR = new Bone("calf.R");

        // Hierarchy
        root.addChild(pelvis);

        // Spine
        pelvis.addChild(spine);
        spine.addChild(chest);

        // Head
        chest.addChild(neck);
        neck.addChild(head);

        // Left Arm
        chest.addChild(shoulderL);
        shoulderL.addChild(upperArmL);
        upperArmL.addChild(lowerArmL);

        // Right Arm
        chest.addChild(shoulderR);
        shoulderR.addChild(upperArmR);
        upperArmR.addChild(lowerArmR);

        // Left Leg
        pelvis.addChild(thighL);
        thighL.addChild(calfL);

        // Right Leg
        pelvis.addChild(thighR);
        thighR.addChild(calfR);

        // Create Skeleton
        return new Skeleton(root);
    }

    public BoneMapping getBoneMapping() {
        return boneMapping;
    }
}