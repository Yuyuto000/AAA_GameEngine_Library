package aaaminecraft.library.core.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;

public class BoneTransformer {

    // =========================
    // Local Transform
    // =========================

    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;


    // =========================
    // World Transform
    // =========================

    private Vector3 worldPosition;
    private Quaternion worldRotation;
    private Vector3 worldScale;


    public BoneTransformer() {

        position = new Vector3();
        rotation = new Quaternion();
        scale = new Vector3(1.0f, 1.0f, 1.0f);

        worldPosition = new Vector3();
        worldRotation = new Quaternion();
        worldScale = new Vector3(1.0f, 1.0f, 1.0f);
    }


    // =========================
    // Local Position
    // =========================

    public Vector3 getPosition() {
        return position;
    }


    // =========================
    // Local Rotation
    // =========================

    public Quaternion getRotation() {
        return rotation;
    }


    // =========================
    // Local Scale
    // =========================

    public Vector3 getScale() {
        return scale;
    }


    // =========================
    // World Position
    // =========================

    public Vector3 getWorldPosition() {
        return worldPosition;
    }


    // =========================
    // World Rotation
    // =========================

    public Quaternion getWorldRotation() {
        return worldRotation;
    }


    // =========================
    // World Scale
    // =========================

    public Vector3 getWorldScale() {
        return worldScale;
    }


    // =========================
    // Set Local Transform
    // =========================

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setRotation(float x, float y, float z, float w) {
        rotation.set(x, y, z, w);
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }
}