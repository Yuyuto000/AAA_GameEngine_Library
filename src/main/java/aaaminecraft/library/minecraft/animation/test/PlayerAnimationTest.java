package aaaminecraft.library.minecraft.animation.test;

import com.mojang.logging.LogUtils;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.slf4j.Logger;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.PlayerSkeletonFactory;
import aaaminecraft.library.core.animation.bone.Skeleton;
import aaaminecraft.library.core.transform.Quaternion;

import aaaminecraft.library.minecraft.animation.bone.MinecraftBoneApplier;
import aaaminecraft.library.minecraft.animation.bone.MinecraftPlayerBoneProvider;


// @Mod.EventBusSubscriber(
//         modid = "aaa_library",
//         bus = Mod.EventBusSubscriber.Bus.FORGE,
//         value = Dist.CLIENT
// )
public class PlayerAnimationTest {

    private static final Logger LOGGER =
            LogUtils.getLogger();


    // ==================================================
    // Test Configuration
    // ==================================================

    /**
     * Chest rotation around Y axis.
     *
     * 胸部をY軸周りに回転させる角度。
     */
    private static final float CHEST_ROTATION_DEGREES = 45.0f;

    /**
     * Upper arm rotation around X axis.
     *
     * 左上腕をX軸周りに回転させる角度。
     */
    private static final float UPPER_ARM_ROTATION_DEGREES = 90.0f;


    // ==================================================
    // Test State
    // ==================================================

    private static boolean initialized = false;
    private static boolean testCompleted = false;

    private static Skeleton skeleton;
    private static BoneMapping mapping;
    private static MinecraftBoneApplier applier;


    // ==================================================
    // Render Event
    // ==================================================

    @SubscribeEvent
    public static void onRenderPlayer(
            RenderPlayerEvent.Pre event
    ) {

        // ----------------------------------------------
        // Only test the local player.
        // ローカルプレイヤーだけをテスト対象にする。
        // ----------------------------------------------

        if (!(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        LocalPlayer player =
                (LocalPlayer) event.getEntity();

        PlayerModel<?> playerModel =
                event.getRenderer().getModel();


        // ----------------------------------------------
        // Initialize once.
        // 初回のみ初期化する。
        // ----------------------------------------------

        if (!initialized) {

            if (!initialize(playerModel)) {
                return;
            }

            initialized = true;

            LOGGER.info(
                    "[AAA Animation Test] Test system initialized for player: {}",
                    player.getName().getString()
            );
        }


        // ----------------------------------------------
        // Update AAA skeleton.
        // AAA SkeletonのWorld Transformを更新する。
        // ----------------------------------------------

        skeleton.getRoot().updateWorldTransform();


        // ----------------------------------------------
        // Run logical tests once.
        // 論理テストは一度だけ実行する。
        // ----------------------------------------------

        if (!testCompleted) {

            runSkeletonPropagationTest();

            testCompleted = true;

            LOGGER.info(
                    "[AAA Animation Test] Skeleton propagation tests completed."
            );
        }


        // ----------------------------------------------
        // Apply test result to Minecraft.
        // テスト結果をMinecraft側へ適用する。
        // ----------------------------------------------

        applyMinecraftTest(playerModel);
    }


    // ==================================================
    // Initialization
    // ==================================================

    private static boolean initialize(
            PlayerModel<?> playerModel
    ) {

        LOGGER.info(
                "[AAA Animation Test] Initializing test system..."
        );


        // ==================================================
        // ① AAA Skeleton生成
        // ==================================================

        try {

            PlayerSkeletonFactory factory =
                    new PlayerSkeletonFactory();

            skeleton =
                    factory.createPlayerSkeleton();

        } catch (Exception exception) {

            LOGGER.error(
                    "[AAA Animation Test] Failed to create player skeleton.",
                    exception
            );

            return false;
        }


        if (skeleton == null ||
                skeleton.getRoot() == null) {

            LOGGER.error(
                    "[AAA Animation Test] Player skeleton or root bone is null."
            );

            return false;
        }


        LOGGER.info(
                "[AAA Animation Test] Player skeleton created."
        );


        // ==================================================
        // ② Minecraft PlayerModelとのMapping
        // ==================================================

        try {

            MinecraftPlayerBoneProvider provider =
                    new MinecraftPlayerBoneProvider(playerModel);

            mapping =
                    provider.createMapping(
                            skeleton.getRoot()
                    );

        } catch (Exception exception) {

            LOGGER.error(
                    "[AAA Animation Test] Failed to create Minecraft bone mapping.",
                    exception
            );

            return false;
        }


        if (mapping == null) {

            LOGGER.error(
                    "[AAA Animation Test] Bone mapping is null."
            );

            return false;
        }


        LOGGER.info(
                "[AAA Animation Test] Bone mapping created."
        );


        // ==================================================
        // ③ Minecraft Bone Applier
        // ==================================================

        applier =
                new MinecraftBoneApplier();


        // ==================================================
        // ④ Required Bone Check
        // ==================================================

        if (!checkRequiredBones()) {
            return false;
        }


        // ==================================================
        // ⑤ Test Pose Setup
        // ==================================================

        setupTestPose();


        LOGGER.info(
                "[AAA Animation Test] Test pose initialized."
        );

        return true;
    }


    // ==================================================
    // Required Bone Check
    // ==================================================

    private static boolean checkRequiredBones() {

        String[] requiredBones = {
                "chest",
                "head",
                "upper_arm.L",
                "lower_arm.L"
        };


        for (String boneName : requiredBones) {

            Bone bone =
                    skeleton.getBone(boneName);

            if (bone == null) {

                LOGGER.error(
                        "[AAA Animation Test] Required bone not found: {}",
                        boneName
                );

                return false;
            }

            LOGGER.info(
                    "[AAA Animation Test] Bone found: {}",
                    boneName
            );
        }


        // ----------------------------------------------
        // Minecraft mapping check
        // Minecraft側Mappingも確認する。
        // ----------------------------------------------

        if (!mapping.contains("head")) {

            LOGGER.error(
                    "[AAA Animation Test] Required Minecraft mapping not found: head"
            );

            return false;
        }


        LOGGER.info(
                "[AAA Animation Test] Required bone mappings found."
        );

        return true;
    }


    // ==================================================
    // Test Pose
    // ==================================================

    private static void setupTestPose() {

        // ==================================================
        // Test 1
        // chestをY軸45度回転
        //
        // chest
        //   └─ head
        //
        // headのWorld Rotationへ伝播することを確認する。
        // ==================================================

        Bone chest =
                skeleton.getBone("chest");

        Quaternion chestRotation =
                createRotationAroundY(
                        CHEST_ROTATION_DEGREES
                );

        chest.getTransform().setRotation(
                chestRotation.getX(),
                chestRotation.getY(),
                chestRotation.getZ(),
                chestRotation.getW()
        );


        // ==================================================
        // Test 2
        // upper_arm.LをX軸90度回転
        //
        // upper_arm.L
        //       └─ lower_arm.L
        //
        // lower_arm.Lへ回転が伝播することを確認する。
        // ==================================================

        Bone upperArmL =
                skeleton.getBone("upper_arm.L");

        Quaternion upperArmRotation =
                createRotationAroundX(
                        UPPER_ARM_ROTATION_DEGREES
                );

        upperArmL.getTransform().setRotation(
                upperArmRotation.getX(),
                upperArmRotation.getY(),
                upperArmRotation.getZ(),
                upperArmRotation.getW()
        );
    }


    // ==================================================
    // Skeleton Propagation Test
    // ==================================================

    private static void runSkeletonPropagationTest() {

        LOGGER.info(
                "[AAA Animation Test] Running skeleton propagation tests..."
        );


        // ----------------------------------------------
        // World Transform更新
        // ----------------------------------------------

        skeleton.getRoot().updateWorldTransform();


        // ==================================================
        // Test 1: chest -> head
        // ==================================================

        Bone chest =
                skeleton.getBone("chest");

        Bone head =
                skeleton.getBone("head");


        Quaternion chestWorldRotation =
                chest
                        .getTransform()
                        .getWorldRotation();

        Quaternion headWorldRotation =
                head
                        .getTransform()
                        .getWorldRotation();


        LOGGER.info(
                "[AAA Animation Test] chest World Rotation: x={}, y={}, z={}, w={}",
                chestWorldRotation.getX(),
                chestWorldRotation.getY(),
                chestWorldRotation.getZ(),
                chestWorldRotation.getW()
        );

        LOGGER.info(
                "[AAA Animation Test] head World Rotation: x={}, y={}, z={}, w={}",
                headWorldRotation.getX(),
                headWorldRotation.getY(),
                headWorldRotation.getZ(),
                headWorldRotation.getW()
        );


        // ==================================================
        // Test 2: upper_arm.L -> lower_arm.L
        // ==================================================

        Bone upperArmL =
                skeleton.getBone("upper_arm.L");

        Bone lowerArmL =
                skeleton.getBone("lower_arm.L");


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
    }


    // ==================================================
    // Minecraft Application Test
    // ==================================================

    private static void applyMinecraftTest(
            PlayerModel<?> playerModel
    ) {

        if (applier == null) {
            return;
        }

        if (mapping == null) {
            return;
        }

        if (!mapping.contains("head")) {
            return;
        }


        try {

            applier.apply(
                    mapping.get("head")
            );

        } catch (Exception exception) {

            LOGGER.error(
                    "[AAA Animation Test] Failed to apply head transform to Minecraft ModelPart.",
                    exception
            );
        }
    }


    // ==================================================
    // Quaternion Helpers
    // ==================================================

    /**
     * Create a rotation around the X axis.
     *
     * X軸周りの回転Quaternionを生成する。
     */
    private static Quaternion createRotationAroundX(
            float degrees
    ) {

        float halfAngle =
                (float) Math.toRadians(
                        degrees * 0.5f
                );

        return new Quaternion(
                (float) Math.sin(halfAngle),
                0.0f,
                0.0f,
                (float) Math.cos(halfAngle)
        );
    }


    /**
     * Create a rotation around the Y axis.
     *
     * Y軸周りの回転Quaternionを生成する。
     */
    private static Quaternion createRotationAroundY(
            float degrees
    ) {

        float halfAngle =
                (float) Math.toRadians(
                        degrees * 0.5f
                );

        return new Quaternion(
                0.0f,
                (float) Math.sin(halfAngle),
                0.0f,
                (float) Math.cos(halfAngle)
        );
    }
}