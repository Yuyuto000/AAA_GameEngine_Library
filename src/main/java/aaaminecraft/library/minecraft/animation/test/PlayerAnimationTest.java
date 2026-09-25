package aaaminecraft.library.minecraft.animation.test;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.minecraft.animation.bone.MinecraftBoneApplier;
import aaaminecraft.library.minecraft.animation.bone.MinecraftPlayerBoneProvider;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "aaa_library",
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class PlayerAnimationTest {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {

        if (!(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        LocalPlayer player =
                (LocalPlayer) event.getEntity();

        PlayerModel<?> playerModel =
                event.getRenderer().getModel();

        LOGGER.info(
                "[AAA Animation Test] Starting test for player: {}",
                player.getName().getString()
        );

        runTest(playerModel);
    }

    private static void runTest(PlayerModel<?> playerModel) {

        // ① AAA側のPlayer Skeletonを作る
        Bone skeleton = createSkeleton();

        LOGGER.info(
                "[AAA Animation Test] Player skeleton created."
        );

        // ② Minecraft PlayerModelとのMappingを作る
        MinecraftPlayerBoneProvider provider =
                new MinecraftPlayerBoneProvider(playerModel);

        BoneMapping mapping =
                provider.createMapping(skeleton);

        LOGGER.info(
                "[AAA Animation Test] Bone mapping created."
        );

        // ③ head Boneを取得
        Bone head =
                findBone(skeleton, "head");

        if (head == null) {

            LOGGER.error(
                    "[AAA Animation Test] head bone not found!"
            );

            return;
        }

        LOGGER.info(
                "[AAA Animation Test] head bone found."
        );

        // ④ 45度回転
        float rotation =
                (float) Math.toRadians(45.0);

        head.getTransform().setRotation(
                rotation,
                0.0f,
                0.0f
        );

        LOGGER.info(
                "[AAA Animation Test] Head rotation set to 45 degrees."
        );

        // ⑤ Mappingされたheadを取得
        if (!mapping.contains("head")) {

            LOGGER.error(
                    "[AAA Animation Test] head mapping not found!"
            );

            return;
        }

        // ⑥ MinecraftへTransformを適用
        MinecraftBoneApplier applier =
                new MinecraftBoneApplier();

        applier.apply(
                mapping.get("head")
        );

        LOGGER.info(
                "[AAA Animation Test] Head transform applied to Minecraft ModelPart."
        );

        LOGGER.info(
                "[AAA Animation Test] Test completed successfully."
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