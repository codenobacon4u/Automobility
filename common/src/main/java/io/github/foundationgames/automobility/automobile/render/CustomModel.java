package io.github.foundationgames.automobility.automobile.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CustomModel extends BaseModel {
    private static Map<String, ModelLayerLocation> MODEL_LAYERS = new HashMap<>();

    private Optional<Vector3f> scale;
    private Optional<Float> yRot;

    public CustomModel(String modelId, EntityRendererProvider.Context ctx, Optional<Vector3f> scale, Optional<Float> yRot) {
        super(RenderType::entityCutout, ctx, MODEL_LAYERS.get(modelId));
        this.scale = scale;
        this.yRot = yRot;
    }

    @Override
    protected void prepare(PoseStack matrices) {
        if (yRot.isPresent()) {
            matrices.mulPose(Axis.YP.rotationDegrees(yRot.get()));
        }
        if (scale.isPresent()) {
            matrices.scale(scale.get().x, scale.get().y, scale.get().z);
        }
    }

    public static void registerModelLayer(String modelId, ModelLayerLocation layerLocation) {
        if (!MODEL_LAYERS.containsKey(modelId)) {
            MODEL_LAYERS.put(modelId, layerLocation);
        }
    }

    public static ModelLayerLocation getModelLayer(String modelId) {
        if (!MODEL_LAYERS.containsKey(modelId) || MODEL_LAYERS.get(modelId) == null) {
            throw new IllegalArgumentException("No model layer for id "  + modelId);
        }
        return MODEL_LAYERS.get(modelId);
    }
}
