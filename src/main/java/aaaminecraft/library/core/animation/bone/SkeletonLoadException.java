package aaaminecraft.library.core.animation.bone;

/**
 * Skeleton JSONの読み込みに失敗したことを表す例外。
 */
public class SkeletonLoadException extends BoneException {

    public SkeletonLoadException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SkeletonLoadException(
            String errorCode,
            String message,
            Throwable cause
    ) {
        super(errorCode, message, cause);
    }
}