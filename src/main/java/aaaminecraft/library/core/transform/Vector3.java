package aaaminecraft.library.core.transform;

public class Vector3 {

    private float x;
    private float y;
    private float z;

    public Vector3() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(
                x + other.x,
                y + other.y,
                z + other.z
        );
    }

    public Vector3 subtrack(Vector3 other) {
        return new Vector3(
                x - other.x,
                y - other.y,
                z - other.z
        );
    }

    public Vector3 multiply(float scalar) {
        return new Vector3(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    public Vector3 multiply(Vector3 other) {
        return new Vector3(
                x * other.x,
                y * other.y,
                z * other.z
        );
    }
}