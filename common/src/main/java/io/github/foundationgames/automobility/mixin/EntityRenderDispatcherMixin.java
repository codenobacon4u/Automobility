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
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileEngine.EngineModel;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.AutomobileWheel.WheelModel;
import io.github.foundationgames.automobility.automobile.CustomEngine;
import io.github.foundationgames.automobility.automobile.CustomFrame;
import io.github.foundationgames.automobility.automobile.CustomWheel;
import io.github.foundationgames.automobility.automobile.AutomobileFrame.FrameModel;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.WheelBase.WheelPos;
import io.github.foundationgames.automobility.automobile.render.AutomobileModels;
import io.github.foundationgames.automobility.automobile.render.CustomModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    private static final Logger LOG = LogManager.getLogger("Automobility Mixin");

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void automobility$loadCustomModels(ResourceManager resourceManager, CallbackInfo ci) {
        List<String> modelIds = new ArrayList<>();
        LOG.info("Loading Custom Automobile Engine Models");
        for (var id : resourceManager.listResources("custom/engine", rl -> rl.getNamespace().equals("automobility")).keySet()) {
            LOG.info(id.getPath());
            try (InputStream stream = resourceManager.getResource(id).get().open()) {
                CustomEngine engine = new Gson().fromJson(new InputStreamReader(stream), CustomEngine.class);
                AutomobileEngine.REGISTRY.register(
                        new AutomobileEngine(
                                Automobility.rl(engine.name),
                                engine.torque,
                                engine.speed,
                                () -> SoundEvent.createVariableRangeEvent(Automobility.rl(engine.soundPath)),
                                new EngineModel(
                                        Automobility.rl(engine.texturePath),
                                        Automobility.rl(engine.modelId),
                                        engine.exhaustPos.toArray(new AutomobileEngine.ExhaustPos[0]))));
                if (!modelIds.contains(engine.modelId)) {
                    LOG.info("Registering model " + engine.modelId + ": " + engine.modelPath);
                    AutomobileModels.register(Automobility.rl(engine.modelId), ctx -> new CustomModel(engine.modelId,ctx, Optional.ofNullable(engine.scale), Optional.ofNullable(engine.yRot)));
                    modelIds.add(engine.modelId);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
        LOG.info("Loading Custom Automobile Frame Models");
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
                    AutomobileModels.register(Automobility.rl(frame.modelId), ctx -> new CustomModel(frame.modelId, ctx, Optional.ofNullable(frame.scale), Optional.ofNullable(frame.yRot)));
                    modelIds.add(frame.modelId);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
        LOG.info("Loading Custom Automobile Wheel Models");
        for (var id : resourceManager.listResources("custom/wheel", rl -> rl.getNamespace().equals("automobility")).keySet()) {
            LOG.info(id.getPath());
            try (InputStream stream = resourceManager.getResource(id).get().open()) {
                CustomWheel wheel = new Gson().fromJson(new InputStreamReader(stream), CustomWheel.class);
                AutomobileWheel.REGISTRY.register(
                        new AutomobileWheel(
                                Automobility.rl(wheel.name),
                                wheel.size,
                                wheel.grip,
                                new WheelModel(
                                        wheel.radius,
                                        wheel.width,
                                        Automobility.rl(wheel.texturePath),
                                        Automobility.rl(wheel.modelId))));
                if (!modelIds.contains(wheel.modelId)) {
                    LOG.info("Registering model " + wheel.modelId + ": " + wheel.modelPath);
                    AutomobileModels.register(Automobility.rl(wheel.modelId), ctx -> new CustomModel(wheel.modelId, ctx, Optional.ofNullable(wheel.scale), Optional.ofNullable(wheel.yRot)));
                    modelIds.add(wheel.modelId);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

}
