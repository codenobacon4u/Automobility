package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

@Environment(EnvType.CLIENT)
public class ModModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.resolveModel().register((context -> {
            if (context.id() != null && SlopeUnbakedModel.DEFAULT_MODELS.containsKey(context.id())) {
                Automobility.LOGGER.info("Resolving ResourceID: " + context.id().toString());
                return SlopeUnbakedModel.DEFAULT_MODELS.get(context.id()).get();
            } else {
                return null;
            }
        }));
    }
    
}
