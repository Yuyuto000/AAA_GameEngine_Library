package aaaminecraft.library.minecraft.animation.bone;

import aaaminecraft.library.core.animation.bone.Bone;
import aaaminecraft.library.core.animation.bone.BoneMappingEntry;
import aaaminecraft.library.core.animation.bone.BoneTransformer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.geom.ModelPart;
import org.slf4j.Logger;

public class MinecraftBoneApplier {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ERROR_PREFIX = "[AAA_MINECRAFT-APPLIER]";

    /**
     * Applies a directly compatible AAA bone transform to a Minecraft ModelPart.
     * MinecraftのModelPartに対して、直接互換性のあるAAAボーン変換を適用します。
     *
     * IMPORTANT:
     * This method does NOT apply Skeleton Space position directly.
     * Minecraft ModelPart positions are local pivot positions.
     *
     * このメソッドは、スケルトンスペースの位置を直接適用するものではありません。
     * MinecraftのModelPartの位置は、ローカルピボットの位置です。
     */
    public void apply(BoneMappingEntry mappingEntry) {

        if (mappingEntry == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-020", ERROR_PREFIX + "Bone mapping entry must not be null. / BoneMappingEntryはnullにできません。");
        }

        Bone bone = mappingEntry.sourceBone();

        if (bone == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-021", ERROR_PREFIX + "Mapping entry contains a null source bone. / MappingEntryのsource Boneがnullです。");
        }

        Object target = mappingEntry.target();

        if (!(target instanceof ModelPart modelPart)) {
            String actualType = target == null ? "null" : target.getClass().getName();

            throw new MinecraftBoneException("MINECRAFT-BONE-022", ERROR_PREFIX + "Mapping target must be a ModelPart, but was " + actualType + ". / " + "Mapping targetにはModelPartが必要ですが、実際には " + actualType + " でした。");
        }

        BoneTransformer transform = bone.getTransform();

        if (transform == null) {
            throw new MinecraftBoneException("MINECRAFT-BONE-023", ERROR_PREFIX + "Bone transformer must not be null. / " + "BoneTransformerはnullにできません。");
        }

        /*
         * IMPORTANT:
         *
         * Do not copy Skeleton Space position into ModelPart.setPos().
         *
         * ModelPart.setPos() represents the model part's local pivot
         * relative to its Minecraft parent.
         *
         * Position retargeting therefore requires a dedicated rest-pose
         * retargeting stage, which is not implemented here yet.
         *
         * 重要
         * Skeleton Spaceの位置をModelPart.setPos()にコピーしないでください。
         * ModelPart.setPos()は、Minecraftの親オブジェクトを基準としたモデルパーツのローカルピボットを表します。
         *
         * したがって、位置のリターゲティングには専用のレストポーズリターゲティング段階が必要ですが、これは現時点ではまだ実装されていません。
         */
        throw new MinecraftBoneException(
                "MINECRAFT-BONE-024", ERROR_PREFIX
                        + "Direct application of AAA Skeleton Space position is "
                        + "not supported because Minecraft ModelPart uses "
                        + "local pivot coordinates. "
                        + "A rest-pose retargeting step is required. / "
                        + "AAA Skeleton Spaceの位置をMinecraft ModelPartへ "
                        + "直接適用することはできません。Minecraft ModelPartは "
                        + "親に対するローカルpivot座標を使用するため、"
                        + "Rest Poseとの差分を利用したRetargeting処理が必要です。"
        );
    }
}