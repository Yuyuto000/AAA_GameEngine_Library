package aaaminecraft.library.core.transform;

public class TransformException extends RuntimeException {

    private final String errorCode;

    public TransformException(String ErrorCode, String message) {
        super(message);
        this.errorCode = ErrorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
