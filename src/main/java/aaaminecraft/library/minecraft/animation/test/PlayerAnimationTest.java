package aaaminecraft.library.minecraft.animation.test;

import com.mojang.logging.LogUtils;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.Skeleton;
import aaaminecraft.library.core.animation.importer.PlayerAnimationImporter;
import aaaminecraft.library.core.transform.Quaternion;

import aaaminecraft.library.minecraft.animation.bone.MinecraftBoneApplier;
import aaaminecraft.library.minecraft.animation.bone.MinecraftPlayerBoneProvider;


// @Mod.EventBusSubscriber(modid = "aaa_library", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerAnimationTest {

    private static final Logger LOGGER =
            LogUtils.getLogger();


    @SubscribeEvent
    public static void onRenderPlayer(
            RenderPlayerEvent.Pre event
    ) {

        if (!(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        LocalPlayer player =
                (LocalPlayer) event.getEntity();

        PlayerModel<?> playerModel =
                event.getRenderer().getModel();

        LOGGER.info(
                "[AAA Animation Test] Starting transform test for player: {}",
                player.getName().getString()
        );

        runTest(playerModel);
    }


    private static void runTest(
            PlayerModel<?> playerModel
    ) {

        // ==================================================
        // ① AAA Skeletonを生成
        // ==================================================

        PlayerAnimationImporter importer = new PlayerAnimationImporter();

        Skeleton skeleton = importer.createPlayerSkeleton();

        LOGGER.info("[AAA Animation Test] Player skeleton created.");


        // ==================================================
        // ② Minecraft PlayerModelとのMapping
        // ==================================================

        MinecraftPlayerBoneProvider provider =
                new MinecraftPlayerBoneProvider(playerModel);

        BoneMapping mapping =
                provider.createMapping(
                        skeleton.getRoot()
                );

        LOGGER.info(
                "[AAA Animation Test] Bone mapping created."
        );


        // ==================================================
        // ③ Test 1
        // chestを回転させてheadへ伝播するか確認
        // ==================================================

        Bone chest =
                skeleton.getBone("chest");

        Bone head =
                skeleton.getBone("head");

        if (chest == null || head == null) {

            LOGGER.error(
                    "[AAA Animation Test] Required bones not found."
            );

            return;
        }

        LOGGER.info(
                "[AAA Animation Test] chest and head bones found."
        );


        // Y軸45度のQuaternion
        float halfAngle =
                (float) Math.toRadians(45.0f / 2.0f);

        Quaternion chestRotation =
                new Quaternion(
                        0.0f,
                        (float) Math.sin(halfAngle),
                        0.0f,
                        (float) Math.cos(halfAngle)
                );

        chest.getTransform().setRotation(
                chestRotation.getX(),
                chestRotation.getY(),
                chestRotation.getZ(),
                chestRotation.getW()
        );


        // ==================================================
        // ④ Skeleton全体のWorld Transformを更新
        // ==================================================

        skeleton.getRoot().updateWorldTransform();

        LOGGER.info(
                "[AAA Animation Test] World transform updated."
        );


        // ==================================================
        // ⑤ headのWorld Rotationを確認
        // ==================================================

        Quaternion headWorldRotation =
                head.getTransform().getWorldRotation();

        LOGGER.info(
                "[AAA Animation Test] Head World Rotation: x={}, y={}, z={}, w={}",
                headWorldRotation.getX(),
                headWorldRotation.getY(),
                headWorldRotation.getZ(),
                headWorldRotation.getW()
        );


        // ==================================================
        // ⑥ Test 2
        // upper_arm.Lを回転させて
        // lower_arm.Lへ伝播するか確認
        // ==================================================

        Bone upperArmL =
                skeleton.getBone("upper_arm.L");

        Bone lowerArmL =
                skeleton.getBone("lower_arm.L");

        if (upperArmL == null || lowerArmL == null) {

            LOGGER.error(
                    "[AAA Animation Test] Arm bones not found."
            );

            return;
        }


        float armHalfAngle =
                (float) Math.toRadians(90.0f / 2.0f);

        Quaternion armRotation =
                new Quaternion(
                        (float) Math.sin(armHalfAngle),
                        0.0f,
                        0.0f,
                        (float) Math.cos(armHalfAngle)
                );

        upperArmL.getTransform().setRotation(
                armRotation.getX(),
                armRotation.getY(),
                armRotation.getZ(),
                armRotation.getW()
        );


        // 再計算
        skeleton.getRoot().updateWorldTransform();


        // ==================================================
        // ⑦ upper_arm.L / lower_arm.Lを確認
        // ==================================================

        Quaternion upperArmWorldRotation =
                upperArmL
                        .getTransform()
                        .getWorldRotation();

        Quaternion lowerArmWorldRotation =
                lowerArmL
                        .getTransform()
                        .getWorldRotation();

        LOGGER.info(
                "[AAA Animation Test] upper_arm.L World Rotation: x={}, y={}, z={}, w={}",
                upperArmWorldRotation.getX(),
                upperArmWorldRotation.getY(),
                upperArmWorldRotation.getZ(),
                upperArmWorldRotation.getW()
        );

        LOGGER.info(
                "[AAA Animation Test] lower_arm.L World Rotation: x={}, y={}, z={}, w={}",
                lowerArmWorldRotation.getX(),
                lowerArmWorldRotation.getY(),
                lowerArmWorldRotation.getZ(),
                lowerArmWorldRotation.getW()
        );


        // ==================================================
        // ⑧ Minecraft側へheadを適用
        // ==================================================

        if (!mapping.contains("head")) {

            LOGGER.error(
                    "[AAA Animation Test] head mapping not found!"
            );

            return;
        }

        MinecraftBoneApplier applier =
                new MinecraftBoneApplier();

        applier.apply(
                mapping.get("head")
        );

        LOGGER.info(
                "[AAA Animation Test] Head transform applied to Minecraft ModelPart."
        );


        LOGGER.info(
                "[AAA Animation Test] Transform test completed."
        );
    }
}