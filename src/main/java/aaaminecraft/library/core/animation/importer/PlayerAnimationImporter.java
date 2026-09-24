package aaaminecraft.library.core.animation.importer;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;

public class PlayerAnimationImporter {

    private final BoneMapping boneMapping;

    public PlayerAnimationImporter() {
        this.boneMapping = new BoneMapping();
    }

    public Bone createPlayerSkeleton() {

        Bone root = new Bone("root");

        Bone body = new Bone("body");
        Bone chest = new Bone("chest");
        Bone head = new Bone("head");

        Bone leftArm = new Bone("arm.L");
        Bone leftHand = new Bone("hand.L");

        Bone rightArm = new Bone("arm.R");
        Bone rightHand = new Bone("hand.R");

        Bone leftLeg = new Bone("leg.L");
        Bone rightLeg = new Bone("leg.R");

        root.addChild(body);

        body.addChild(chest);

        chest.addChild(head);

        chest.addChild(leftArm);
        leftArm.addChild(leftHand);

        chest.addChild(rightArm);
        rightArm.addChild(rightHand);

        body.addChild(leftLeg);
        body.addChild(rightLeg);

        return root;
    }

    public BoneMapping getBoneMapping() {
        return boneMapping;
    }
}