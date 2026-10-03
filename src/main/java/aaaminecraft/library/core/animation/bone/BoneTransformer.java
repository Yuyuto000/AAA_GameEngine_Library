package aaaminecraft.library.core.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;

public class BoneTransformer {

    // Local Transform
    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;

    // World Transform
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

    // =============================================

    // Local Position
    public Vector3 getPosition() {
        return position;
    }


    // Local Rotation
    public Quaternion getRotation() {
        return rotation;
    }


    // Local Scale
    public Vector3 getScale() {
        return scale;
    }


    // World Position
    public Vector3 getWorldPosition() {
        return worldPosition;
    }


    // World Rotation
    public Quaternion getWorldRotation() {
        return worldRotation;
    }


    // World Scale
    public Vector3 getWorldScale() {
        return worldScale;
    }

    // Set Local Transform
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setRotation(float x, float y, float z, float w) {
        rotation.set(x, y, z, w);
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    // mixing transform
    public void updateWorldTransform(BoneTransformer parent) {

        Vector3 parentPosition = parent.getWorldPosition();
        Quaternion parentRotation = parent.getWorldRotation();
        Vector3 parentScale = parent.getWorldScale();

        // ワールドスケール
        worldScale = new Vector3(
                parentScale.getX() * scale.getX(),
                parentScale.getY() * scale.getY(),
                parentScale.getZ() * scale.getZ()
        );

        // ワールド回転
        Quaternion parentRotationCopy = new Quaternion(
                parentRotation.getX(),
                parentRotation.getY(),
                parentRotation.getZ(),
                parentRotation.getW()
        );

        Quaternion localRotationCopy = new Quaternion(
                rotation.getX(),
                rotation.getY(),
                rotation.getZ(),
                rotation.getW()
        );

        worldRotation = parentRotationCopy
                .multiply(localRotationCopy)
                .normalize();

        // ローカル位置に親のワールドスケールを適用
        Vector3 scaledPosition = new Vector3(
                position.getX() * parentScale.getX(),
                position.getY() * parentScale.getY(),
                position.getZ() * parentScale.getZ()
        );

        // 親のワールド回転を適用
        Quaternion rotationCopy = new Quaternion(
                parentRotation.getX(),
                parentRotation.getY(),
                parentRotation.getZ(),
                parentRotation.getW()
        );

        Vector3 rotatedPosition = rotationCopy.rotate(scaledPosition);

        // 親の位置をコピーしてから加算
        worldPosition = new Vector3(
                parentPosition.getX() + rotatedPosition.getX(),
                parentPosition.getY() + rotatedPosition.getY(),
                parentPosition.getZ() + rotatedPosition.getZ()
        );
    }

    public void updateWorldTransform() {

        // Rootでもローカル変換とワールド変換を別オブジェクトにする
        worldPosition = new Vector3(
                position.getX(),
                position.getY(),
                position.getZ()
        );

        worldRotation = new Quaternion(
                rotation.getX(),
                rotation.getY(),
                rotation.getZ(),
                rotation.getW()
        );

        worldScale = new Vector3(
                scale.getX(),
                scale.getY(),
                scale.getZ()
        );
    }
}