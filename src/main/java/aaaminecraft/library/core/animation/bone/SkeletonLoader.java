package aaaminecraft.library.core.animation.bone;

import aaaminecraft.library.core.transform.Quaternion;
import aaaminecraft.library.core.transform.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton JSONを検証し、AAA Skeletonを生成する。
 *
 * <p>
 * このクラスはMinecraft ModelPartやRendererに依存しない。
 * JSONからAAA内部のBone階層を構築することだけを担当する。
 * </p>
 *
 * <p>
 * JSONはAAA標準座標系へ変換済みであることを前提とする。
 * unitScaleは入力位置をAAA Unitへ変換する倍率として扱う。
 * </p>
 */
public class SkeletonLoader {

    private static final String ERROR_PREFIX = "[AAA-SKELETON-LOADER]";

    private static final float ROOT_EPSILON = 1.0e-5f;
    public Skeleton load(Reader reader) {

        if (reader == null) {
            throw error("SKELETON-LOAD-001", "Reader cannot be null. / Readerにnullは指定できません。");
        }

        try {
            JsonElement rootElement = JsonParser.parseReader(reader);

            if (rootElement == null || !rootElement.isJsonObject()) {
                throw error("SKELETON-LOAD-002", "Root JSON value must be an object. " + "/ JSONの最上位要素はObjectでなければなりません。");
            }

            JsonObject json = rootElement.getAsJsonObject();
            validateCoordinateSystem(json);
            JsonArray boneArray = requireArray(json, "bones");

            if (boneArray.isEmpty()) {
                throw error("SKELETON-LOAD-003", "Bone array cannot be empty. " + "/ Bone配列が空です。");
            }

            Map<String, Bone> boneMap = new HashMap<>();
            Map<String, String> parentMap = new HashMap<>();

            // 1. Create every Bone and validate its Local/Rest Transform.
            for (int index = 0; index < boneArray.size(); index++) {

                JsonElement element = boneArray.get(index);

                if (element == null || !element.isJsonObject()) {
                    throw error("SKELETON-LOAD-004", "Bone entry must be an object. Index: " + index + " / Boneの各要素はObjectでなければなりません。Index: " + index);
                }

                JsonObject data = element.getAsJsonObject();

                String name = requireString(data, "name");

                if (boneMap.containsKey(name)) {
                    throw error("SKELETON-LOAD-005", "Duplicate Bone name: " + name + " / Bone名が重複しています。");
                }

                Vector3 position = readVector3(data, "position");
                Vector3 scale = readVector3(data, "scale");
                Quaternion rotation = readQuaternion(data, "rotation");

                Bone bone = new Bone(name);

                bone.getTransform().setRestTransform(new Vector3(
                        position.getX() * getUnitScale(json),
                        position.getY() * getUnitScale(json),
                        position.getZ() * getUnitScale(json)
                ), rotation, scale);

                boneMap.put(name, bone);

                String parentName = readParentName(data);
                parentMap.put(name, parentName);
            }

            // 2. Build the hierarchy.
            Bone root = null;

            for (Map.Entry<String, Bone> entry : boneMap.entrySet()) {

                String name = entry.getKey();
                Bone bone = entry.getValue();
                String parentName = parentMap.get(name);

                if (parentName == null) {

                    if (root != null) {
                        throw error("SKELETON-LOAD-006", "Multiple root Bones detected: " + root.getName() + " and " + name + " / Root Boneが複数存在します。");
                    }

                    root = bone;
                    continue;
                }

                if (name.equals(parentName)) {
                    throw error("SKELETON-LOAD-007", "A Bone cannot be its own parent: " + name + " / Bone自身を親に指定することはできません。");
                }

                Bone parent = boneMap.get(parentName);

                if (parent == null) {
                    throw error("SKELETON-LOAD-008", "Parent Bone not found: " + parentName + " (child: " + name + ")" + " / 親Boneが見つかりません。");
                }

                parent.addChild(bone);
            }

            if (root == null) {
                throw error("SKELETON-LOAD-009", "Root Bone not found. " + "/ Root Boneが見つかりません。");
            }

            if (!"Root".equals(root.getName())) {
                throw error("SKELETON-LOAD-010", "Root Bone must be named 'Root'. Actual: " + root.getName() + " / Root Boneの名前はRootでなければなりません。");
            }

            validateRootTransform(root);

            // 3. Construct and validate the Skeleton.
            Skeleton skeleton = new Skeleton(root);

            if (skeleton.getBoneCount() != boneMap.size()) {
                throw error("SKELETON-LOAD-011", "Not all Bones are reachable from Root. " + "Loaded: " + skeleton.getBoneCount() + ", Declared: " + boneMap.size() + " / Rootから到達できないBoneが存在します。");
            }

            // 4. Update accumulated transforms only after validation.
            skeleton.updateWorldTransforms();

            return skeleton;

        } catch (BoneException exception) {
            // Preserve our own error code and bilingual diagnostic.
            throw exception;

        } catch (JsonParseException | IllegalStateException exception) {
            throw new SkeletonLoadException("SKELETON-LOAD-012", ERROR_PREFIX + " Failed to parse Skeleton JSON. " + "/ Skeleton JSONの解析に失敗しました。" + " Details: " + exception.getMessage(), exception);
        }
    }

    private void validateCoordinateSystem(JsonObject json) {

        JsonObject coordinateSystem = requireObject(json, "coordinateSystem");

        String right = requireString(coordinateSystem, "right");
        String up = requireString(coordinateSystem, "up");
        String forward = requireString(coordinateSystem, "forward");

        if (!"X".equals(right) || !"Y".equals(up) || !"Z".equals(forward)) {
            throw error("SKELETON-LOAD-013", "Unsupported coordinate system. Expected right=X, up=Y, forward=Z. " + "/ 未対応の座標系です。right=X, up=Y, forward=Zが必要です。" + " Actual: right=" + right + ", up=" + up + ", forward=" + forward);
        }

        float unitScale = requireNumber(coordinateSystem, "unitScale");

        if (!Float.isFinite(unitScale) || unitScale <= 0.0f) {
            throw error("SKELETON-LOAD-014", "unitScale must be finite and greater than zero. " + "/ unitScaleは有限かつ0より大きい値でなければなりません。");
        }
    }

    private float getUnitScale(JsonObject json) {
        return requireNumber(
                json.getAsJsonObject("coordinateSystem"),
                "unitScale"
        );
    }

    private void validateRootTransform(Bone root) {

        BoneTransformer transform = root.getTransform();

        Vector3 position = transform.getPosition();
        Quaternion rotation = transform.getRotation();
        Vector3 scale = transform.getScale();

        boolean positionIsIdentity = near(position.getX(), 0.0f)
                                  && near(position.getY(), 0.0f)
                                  && near(position.getZ(), 0.0f);

        boolean rotationIsIdentity = near(rotation.getX(), 0.0f)
                                  && near(rotation.getY(), 0.0f)
                                  && near(rotation.getZ(), 0.0f)
                                  && near(Math.abs(rotation.getW()), 1.0f);

        boolean scaleIsIdentity = near(scale.getX(), 1.0f)
                               && near(scale.getY(), 1.0f)
                               && near(scale.getZ(), 1.0f);

        if (!positionIsIdentity || !rotationIsIdentity || !scaleIsIdentity) {

            throw error("SKELETON-LOAD-015", "Root Transform must be Identity. " + "/ Root TransformはIdentityでなければなりません。" + " Position=" + position + ", Rotation=" + rotation + ", Scale=" + scale);
        }
    }

    private Vector3 readVector3(JsonObject data, String key) {

        JsonArray array = requireArray(data, key);

        if (array.size() != 3) {
            throw error("SKELETON-LOAD-016", key + " must contain exactly 3 numbers. " + "/ " + key + "は数値3個で構成されなければなりません。");
        }

        return new Vector3(
                requireFiniteFloat(array.get(0), key + "[0]"),
                requireFiniteFloat(array.get(1), key + "[1]"),
                requireFiniteFloat(array.get(2), key + "[2]")
        );
    }

    private Quaternion readQuaternion(JsonObject data, String key) {

        JsonArray array = requireArray(data, key);

        if (array.size() != 4) {
            throw error("SKELETON-LOAD-017", key + " must contain exactly 4 numbers. " + "/ " + key + "は数値4個で構成されなければなりません。");
        }

        return new Quaternion(
                requireFiniteFloat(array.get(0), key + "[0]"),
                requireFiniteFloat(array.get(1), key + "[1]"),
                requireFiniteFloat(array.get(2), key + "[2]"),
                requireFiniteFloat(array.get(3), key + "[3]")
        );
    }

    private String readParentName(JsonObject data) {

        if (!data.has("parent") || data.get("parent").isJsonNull()) {
            return null;
        }

        JsonElement element = data.get("parent");

        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw error("SKELETON-LOAD-018", "parent must be a string or null. " + "/ parentは文字列またはnullでなければなりません。");
        }

        String parentName = element.getAsString();

        if (parentName.isBlank()) {
            throw error("SKELETON-LOAD-019", "parent cannot be blank. " + "/ parentに空文字は指定できません。");
        }

        return parentName;
    }

    private JsonObject requireObject(JsonObject parent, String key) {

        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            throw error("SKELETON-LOAD-020", key + " must be an object. " + "/ " + key + "はObjectでなければなりません。");
        }

        return parent.getAsJsonObject(key);
    }

    private JsonArray requireArray(JsonObject parent, String key) {

        if (!parent.has(key) || !parent.get(key).isJsonArray()) {
            throw error("SKELETON-LOAD-021", key + " must be an array. " + "/ " + key + "はArrayでなければなりません。");
        }

        return parent.getAsJsonArray(key);
    }

    private String requireString(JsonObject parent, String key) {

        if (!parent.has(key)) {
            throw error("SKELETON-LOAD-022", "Missing required field: " + key + " / 必須フィールドがありません。");
        }

        JsonElement element = parent.get(key);

        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw error("SKELETON-LOAD-023", key + " must be a string. " + "/ " + key + "は文字列でなければなりません。");
        }

        String value = element.getAsString();

        if (value.isBlank()) {
            throw error("SKELETON-LOAD-024", key + " cannot be blank. " + "/ " + key + "に空文字は指定できません。");
        }

        return value;
    }

    private float requireNumber(JsonObject parent, String key) {

        if (!parent.has(key)) {
            throw error("SKELETON-LOAD-025", "Missing numeric field: " + key + " / 数値の必須フィールドがありません。");
        }

        return requireFiniteFloat(parent.get(key), key);
    }

    private float requireFiniteFloat(JsonElement element, String field) {

        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw error("SKELETON-LOAD-026", field + " must be a number. " + "/ " + field + "は数値でなければなりません。");
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        float value;

        try {
            value = primitive.getAsFloat();
        } catch (NumberFormatException exception) {
            throw new SkeletonLoadException("SKELETON-LOAD-027", ERROR_PREFIX + " Invalid number at " + field + " / " + field + "の数値が不正です。", exception);
        }

        if (!Float.isFinite(value)) {
            throw error("SKELETON-LOAD-028", field + " contains NaN or Infinity. " + "/ " + field + "にNaNまたは無限大が含まれています。");
        }

        return value;
    }

    private static boolean near(float actual, float expected) {
        return Math.abs(actual - expected) <= ROOT_EPSILON;
    }

    private SkeletonLoadException error(String code, String message) {
        return new SkeletonLoadException(code, ERROR_PREFIX + " [" + code + "] " + message);
    }
}