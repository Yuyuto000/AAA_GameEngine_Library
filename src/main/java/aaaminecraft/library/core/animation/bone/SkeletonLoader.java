
package aaaminecraft.library.core.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class SkeletonLoader {

    public Skeleton load(Reader reader) {
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray boneArray = json.getAsJsonArray("bones");

        Map<String, Bone> boneMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();

        // 1. Boneをすべて生成し、Local Transformを設定する
        for (JsonElement element : boneArray) {
            JsonObject data = element.getAsJsonObject();

            String name = data.get("name").getAsString();
            Bone bone = new Bone(name);

            Vector3 position = readVector3(data, "position");
            Vector3 scale = readVector3(data, "scale");
            Quaternion rotation = readQuaternion(data, "rotation");

            bone.getTransform().setPosition(
                    position.getX(),
                    position.getY(),
                    position.getZ()
            );

            bone.getTransform().setRotation(
                    rotation.getX(),
                    rotation.getY(),
                    rotation.getZ(),
                    rotation.getW()
            );

            bone.getTransform().setScale(
                    scale.getX(),
                    scale.getY(),
                    scale.getZ()
            );

            boneMap.put(name, bone);
            JsonElement parentElement = data.get("parent");
            parentMap.put(name, parentElement == null || parentElement.isJsonNull() ? null : parentElement.getAsString());
        }

        // 2. 親子関係を構築する
        Bone root = null;

        for (Map.Entry<String, Bone> entry : boneMap.entrySet()) {
            String name = entry.getKey();
            Bone bone = entry.getValue();
            String parentName = parentMap.get(name);

            if (parentName == null) {
                if (root != null) {
                    throw new IllegalArgumentException("複数のRoot Boneがあります。");
                }

                root = bone;
                continue;
            }

            Bone parent = boneMap.get(parentName);
            if (parent == null) {
                throw new IllegalArgumentException("親Boneが見つかりません: " + parentName);
            }

            parent.addChild(bone);
        }

        if (root == null) {
            throw new IllegalArgumentException("Root Boneが見つかりません。");
        }

        // 3. Skeletonを生成する
        return new Skeleton(root);
    }

    private Vector3 readVector3(JsonObject data, String key) {
        JsonArray array = data.getAsJsonArray(key);

        return new Vector3(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
    }

    private Quaternion readQuaternion(JsonObject data, String key) {
        JsonArray array = data.getAsJsonArray(key);

        return new Quaternion(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat(),
                array.get(3).getAsFloat()
        );
    }
}