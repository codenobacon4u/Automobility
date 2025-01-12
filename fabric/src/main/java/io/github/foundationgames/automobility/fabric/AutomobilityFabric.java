package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.RegistryQueue;
import io.github.foundationgames.automobility.util.network.ClientPackets.RequestSyncAutomobileComponentsPacket;
import io.github.foundationgames.automobility.util.network.ClientPackets.SyncAutomobileInputsPacket;
import io.github.foundationgames.automobility.util.network.CommonPackets.SyncAutomobileAttachmentsPacket;
import io.github.foundationgames.automobility.util.network.CommonPackets.SyncAutomobileComponentsPacket;
import io.github.foundationgames.automobility.util.network.CommonPackets.SyncAutomobileDataPacket;
import io.github.foundationgames.automobility.util.network.CommonPackets.UpdateAutomobileBannerPostPacket;
import io.github.foundationgames.automobility.util.network.CommonPackets.UpdateExtendAttachmentPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AutomobilityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SyncAutomobileDataPacket.TYPE, SyncAutomobileDataPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncAutomobileDataPacket.TYPE, SyncAutomobileDataPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAutomobileInputsPacket.TYPE, SyncAutomobileInputsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncAutomobileInputsPacket.TYPE, SyncAutomobileInputsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateExtendAttachmentPacket.TYPE, UpdateExtendAttachmentPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(UpdateExtendAttachmentPacket.TYPE, UpdateExtendAttachmentPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAutomobileComponentsPacket.TYPE, SyncAutomobileComponentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncAutomobileComponentsPacket.TYPE, SyncAutomobileComponentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAutomobileAttachmentsPacket.TYPE, SyncAutomobileAttachmentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncAutomobileAttachmentsPacket.TYPE, SyncAutomobileAttachmentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateAutomobileBannerPostPacket.TYPE, UpdateAutomobileBannerPostPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(UpdateAutomobileBannerPostPacket.TYPE, UpdateAutomobileBannerPostPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RequestSyncAutomobileComponentsPacket.TYPE, RequestSyncAutomobileComponentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSyncAutomobileComponentsPacket.TYPE, RequestSyncAutomobileComponentsPacket.STREAM_CODEC);
        
        FabricPlatform.init();
        Automobility.init();

        register(BuiltInRegistries.BLOCK);
        register(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        register(BuiltInRegistries.ITEM);
        register(BuiltInRegistries.ENTITY_TYPE);
        register(BuiltInRegistries.PARTICLE_TYPE);
        register(BuiltInRegistries.SOUND_EVENT);
        register(BuiltInRegistries.MENU);
        register(BuiltInRegistries.RECIPE_TYPE);
        register(BuiltInRegistries.RECIPE_SERIALIZER);
        register(BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    public static <V> void register(Registry<V> registry) {
        RegistryQueue.getQueue(registry).forEach(e -> Registry.register(registry, e.rl(), e.entry().create()));
    }
}
