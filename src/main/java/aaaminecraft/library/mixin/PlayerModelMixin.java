package aaaminecraft.library.mixin;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMapping;
import aaaminecraft.library.core.animation.bone.Skeleton;
import aaaminecraft.library.core.animation.bone.SkeletonLoader;
import aaaminecraft.library.minecraft.animation.bone.MinecraftBoneApplier;
import aaaminecraft.library.minecraft.animation.bone.MinecraftPlayerBoneProvider;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> {

    @Unique
    private static final Logger AAA$LOGGER = LogUtils.getLogger();

    // このPlayerModelで使用するSkeleton
    @Unique
    private Skeleton aaa$skeleton;

    // MinecraftのModelPartとの対応表
    @Unique
    private BoneMapping aaa$mapping;

    // ボーン変換を適用する処理
    @Unique
    private MinecraftBoneApplier aaa$applier;

    // 初期化を何度も実行しないためのフラグ
    @Unique
    private boolean aaa$initialized;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void aaa$applyAnimation(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        // JSONの読み込みと対応付けは初回だけ行う
        if (!aaa$initialized) {
            aaa$initialized = true;

            if (!aaa$initializeAnimation()) {
                return;
            }
        }

        if (aaa$skeleton == null || aaa$mapping == null) {
            return;
        }

        // 独自Skeletonのワールド変換を更新
        aaa$skeleton.getRoot().updateWorldTransform();

        // 対応するボーンをMinecraftのModelPartへ適用
        aaa$applyBone("Chest");
        aaa$applyBone("Head");
        aaa$applyBone("UpperArm_R");
        aaa$applyBone("UpperArm_L");
        aaa$applyBone("Thigh_R");
        aaa$applyBone("Thigh_L");
    }

    @Unique
    private boolean aaa$initializeAnimation() {

        try {
            ResourceLocation resourceId = new ResourceLocation("aaaminecraft", "animation/player.json");
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(resourceId);

            if (resource.isEmpty()) {
                AAA$LOGGER.error("Animation JSON not found: {}", resourceId);
                return false;
            }

            // JSONからSkeletonを構築
            SkeletonLoader loader = new SkeletonLoader();

            try (
                    InputStream stream = resource.get().open();
                    Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
            ) {
                aaa$skeleton = loader.load(reader);
            }

            // MinecraftのPlayerModelを取得
            PlayerModel<?> playerModel = (PlayerModel<?>) (Object) this;

            // JSONのボーンとMinecraftのModelPartを対応付ける
            MinecraftPlayerBoneProvider provider = new MinecraftPlayerBoneProvider(playerModel);
            aaa$mapping = provider.createMapping(aaa$skeleton.getRoot());
            aaa$applier = new MinecraftBoneApplier();
            AAA$LOGGER.info("Loaded player animation skeleton successfully.");

            return true;

        } catch (IOException | RuntimeException exception) {
            AAA$LOGGER.error("Failed to load player animation skeleton.", exception);
            return false;
        }
    }

    @Unique
    private void aaa$applyBone(String boneName) {

        if (aaa$mapping.contains(boneName)) {
            aaa$applier.apply(aaa$mapping.get(boneName));
        }
    }
}