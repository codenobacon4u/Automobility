package io.github.foundationgames.automobility.mixin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.CustomFrame;
import io.github.foundationgames.automobility.automobile.AutomobileFrame.FrameModel;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.WheelBase.WheelPos;
import io.github.foundationgames.automobility.automobile.render.AutomobileModels;
import io.github.foundationgames.automobility.automobile.render.frame.CustomFrameModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    private static final Logger LOG = LogManager.getLogger("Automobility Mixin");

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void automobility$loadCustomModels(ResourceManager resourceManager, CallbackInfo ci) {
        List<String> modelIds = new ArrayList<>();
        LOG.info("Loading Custom Automobile Parts Models");
        for (var id : resourceManager.listResources("custom/frame", rl -> rl.getNamespace().equals("automobility")).keySet()) {
            LOG.info(id.getPath());
            try (InputStream stream = resourceManager.getResource(id).get().open()) {
                CustomFrame frame = new Gson().fromJson(new InputStreamReader(stream), CustomFrame.class);
                AutomobileFrame.REGISTRY.register(
                        new AutomobileFrame(
                                Automobility.rl(frame.name),
                                frame.weight,
                                new FrameModel(
                                        Automobility.rl(frame.texturePath),
                                        Automobility.rl(frame.modelId),
                                        new WheelBase(frame.wheelBase.toArray(new WheelPos[0])),
                                        frame.lengthPx,
                                        frame.seatHeight,
                                        frame.enginePosBack,
                                        frame.enginePosUp,
                                        frame.rearAttachmentPos,
                                        frame.frontAttachmentPos)));
                if (!modelIds.contains(frame.modelId)) {
                    LOG.info("Registering model " + frame.modelId + ": " + frame.modelPath);
                    AutomobileModels.register(Automobility.rl(frame.modelId), ctx -> new CustomFrameModel(frame.modelId, ctx, Optional.ofNullable(frame.scale), Optional.ofNullable(frame.yRot)));
                    modelIds.add(frame.modelId);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

}
