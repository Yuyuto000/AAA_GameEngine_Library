package aaaminecraft.library.core.transform;

public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion() {
        this(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion multiply(Quaternion other) {

        float newX =
                w * other.x
                        + x * other.w
                        + y * other.z
                        - z * other.y;

        float newY =
                w * other.y
                        - x * other.z
                        + y * other.w
                        + z * other.x;

        float newZ =
                w * other.z
                        + x * other.y
                        - y * other.x
                        + z * other.w;

        float newW =
                w * other.w
                        - x * other.x
                        - y * other.y
                        - z * other.z;

        return new Quaternion(
                newX,
                newY,
                newZ,
                newW
        );
    }

    public Quaternion normalize() {

        float length =
                (float) Math.sqrt(
                        x * x
                                + y * y
                                + z * z
                                + w * w
                );

        if (length == 0.0f) {
            return new Quaternion();
        }

        return new Quaternion(
                x / length,
                y / length,
                z / length,
                w / length
        );
    }

    public Vector3 rotate(Vector3 vector) {

        Quaternion vectorQuaternion =
                new Quaternion(
                        vector.getX(),
                        vector.getY(),
                        vector.getZ(),
                        0.0f
                );

        Quaternion inverse =
                new Quaternion(
                        -x,
                        -y,
                        -z,
                        w
                );

        Quaternion result =
                this
                        .multiply(vectorQuaternion)
                        .multiply(inverse);

        return new Vector3(
                result.x,
                result.y,
                result.z
        );
    }

    public Vector3 toEuler() {

        float sinXCosY = 2.0f * (w * x + y * z);
        float cosXCosY = 1.0f - 2.0f * (x * x + y * y);
        float rotX = (float) Math.atan2(sinXCosY, cosXCosY);
        float sinY = 2.0f * (w * y - z * x);
        float rotY;

        if (Math.abs(sinY) >= 1.0f) {
            rotY = (float) Math.copySign(Math.PI / 2.0, sinY);
        } else {
            rotY = (float) Math.asin(sinY);
        }

        float sinZCosY = 2.0f * (w * z + x * y);
        float cosZCosY = 1.0f - 2.0f * (y * y + z * z);
        float rotZ = (float) Math.atan2(sinZCosY, cosZCosY);

        return new Vector3(
                rotX,
                rotY,
                rotZ
        );
    }
}