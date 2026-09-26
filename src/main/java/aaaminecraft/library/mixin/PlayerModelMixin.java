package aaaminecraft.library.mixin;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.minecraft.animation.bone.MinecraftBoneApplier;
import aaaminecraft.library.minecraft.animation.bone.MinecraftPlayerBoneProvider;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> {

    @Inject(
            method = "setupAnim",
            at = @At("TAIL")
    )
    private void aaa$testAnimation(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {

        PlayerModel<?> playerModel =
                (PlayerModel<?>) (Object) this;

        Bone skeleton =
                createSkeleton();

        MinecraftPlayerBoneProvider provider =
                new MinecraftPlayerBoneProvider(playerModel);

        BoneMapping mapping =
                provider.createMapping(skeleton);

        Bone head =
                findBone(skeleton, "head");

        if (head == null) {
            return;
        }

        // 左右に45度
        float rotation =
                (float) Math.toRadians(45.0);

        head.getTransform().setRotation(
                0.0f,
                rotation,
                0.0f,
                0.0f
        );

        if (!mapping.contains("head")) {
            return;
        }

        MinecraftBoneApplier applier =
                new MinecraftBoneApplier();

        applier.apply(
                mapping.get("head")
        );
    }

    private static Bone createSkeleton() {

        Bone root =
                new Bone("root");

        Bone body =
                new Bone("body");

        Bone chest =
                new Bone("chest");

        Bone head =
                new Bone("head");

        Bone leftArm =
                new Bone("arm.L");

        Bone rightArm =
                new Bone("arm.R");

        Bone leftLeg =
                new Bone("leg.L");

        Bone rightLeg =
                new Bone("leg.R");

        root.addChild(body);

        body.addChild(chest);

        chest.addChild(head);

        chest.addChild(leftArm);
        chest.addChild(rightArm);

        body.addChild(leftLeg);
        body.addChild(rightLeg);

        return root;
    }

    private static Bone findBone(
            Bone bone,
            String name
    ) {

        if (bone.getName().equals(name)) {
            return bone;
        }

        for (Bone child : bone.getChildren()) {

            Bone result =
                    findBone(child, name);

            if (result != null) {
                return result;
            }
        }

        return null;
    }
}