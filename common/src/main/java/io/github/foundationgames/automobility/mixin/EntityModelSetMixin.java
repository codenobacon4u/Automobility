package io.github.foundationgames.automobility.mixin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.frame.CustomFrameModel;
import io.github.foundationgames.automobility.platform.Platform;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin {
    private static final Logger LOG = LogManager.getLogger("Automobility Mixin");

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void automobility$loadModelLayers(ResourceManager resourceManager, CallbackInfo ci) {
        List<String> modelIds = new ArrayList<>();
        LOG.info("Loading Custom Automobile Model Layers");
        for (var id : resourceManager.listResources("custom/frame", rl -> rl.getNamespace().equals("automobility")).keySet()) {
            LOG.info(id.getPath());
            try (InputStream stream = resourceManager.getResource(id).get().open()) {
                JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                String modelId = jsonObject.get("modelId").getAsString();
                String modelPath = jsonObject.get("modelPath").getAsString();
                if (!modelIds.contains(modelId)) {
                    LOG.info("Registering model layer " + modelId + ": " + modelPath);
                    CustomFrameModel.registerModelLayer(modelId, new ModelLayerLocation(Automobility.rl(modelPath), "main"));
                    Platform.get().modelLayer(CustomFrameModel.getModelLayer(modelId));
                    modelIds.add(modelId);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }
}
