package aaaminecraft.library.core.animation.bone;

/**
 * Bone System内部で発生した不正状態を表す例外。
 */
public class BoneException extends RuntimeException {

    private final String errorCode;

    public BoneException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
