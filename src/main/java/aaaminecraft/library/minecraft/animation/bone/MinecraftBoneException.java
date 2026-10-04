package aaaminecraft.library.minecraft.animation.bone;

public class MinecraftBoneException extends RuntimeException {

    private final String errorCode;

    public MinecraftBoneException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MinecraftBoneException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}