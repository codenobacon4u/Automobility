package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.network.CommonPackets.*;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public enum ClientPackets {;
    public record SyncAutomobileInputsPacket(boolean fwd, boolean back, boolean left, boolean right, boolean space, int entityId) implements CustomPacketPayload{
        public static final Type<SyncAutomobileInputsPacket> TYPE = new Type<>(Automobility.rl("sync_automobile_inputs"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncAutomobileInputsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SyncAutomobileInputsPacket::fwd,
            ByteBufCodecs.BOOL,
            SyncAutomobileInputsPacket::back,
            ByteBufCodecs.BOOL,
            SyncAutomobileInputsPacket::left,
            ByteBufCodecs.BOOL,
            SyncAutomobileInputsPacket::right,
            ByteBufCodecs.BOOL,
            SyncAutomobileInputsPacket::space,
            ByteBufCodecs.INT,
            SyncAutomobileInputsPacket::entityId,
            SyncAutomobileInputsPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    public static void sendSyncAutomobileInputPacket(AutomobileEntity entity, boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        SyncAutomobileInputsPacket packet = new SyncAutomobileInputsPacket(fwd, back, left, right, space, entity.getId());
        Platform.get().clientSendPacket(packet);
    }

    public record RequestSyncAutomobileComponentsPacket(int entityId) implements CustomPacketPayload {
        public static final Type<RequestSyncAutomobileComponentsPacket> TYPE = new Type<>(Automobility.rl("request_sync_automobile_components"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncAutomobileComponentsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, 
            RequestSyncAutomobileComponentsPacket::entityId, 
            RequestSyncAutomobileComponentsPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void requestSyncAutomobileComponentsPacket(AutomobileEntity entity) {
        RequestSyncAutomobileComponentsPacket packet = new RequestSyncAutomobileComponentsPacket(entity.getId());
        Platform.get().clientSendPacket(packet);
    }

    public static void initClient() {
        Platform.get().clientReceivePacket(SyncAutomobileDataPacket.TYPE, (client, payload) -> {
            if (payload instanceof SyncAutomobileDataPacket) {
                SyncAutomobileDataPacket packet = (SyncAutomobileDataPacket)payload;
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeBytes(packet.data());
                int entityId = buf.readInt();
                client.execute(() -> {
                    if (client.player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
                        automobile.readSyncToClientData(buf);
                    }
                });
            }
        });
        Platform.get().clientReceivePacket(SyncAutomobileComponentsPacket.TYPE, (client, payload) -> {
            if (payload instanceof SyncAutomobileComponentsPacket) {
                SyncAutomobileComponentsPacket packet = (SyncAutomobileComponentsPacket)payload;
                int entityId = packet.entityId();
                var frame = AutomobileFrame.REGISTRY.getOrDefault(ResourceLocation.tryParse(packet.frameId()));
                var wheel = AutomobileWheel.REGISTRY.getOrDefault(ResourceLocation.tryParse(packet.wheelsId()));
                var engine = AutomobileEngine.REGISTRY.getOrDefault(ResourceLocation.tryParse(packet.engineId()));
                client.execute(() -> {
                    if (client.player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
                        automobile.setComponents(frame, wheel, engine);
                    }
                });
            }
        });
        Platform.get().clientReceivePacket(SyncAutomobileAttachmentsPacket.TYPE, (client, payload) -> {
            if (payload instanceof SyncAutomobileAttachmentsPacket) {
                SyncAutomobileAttachmentsPacket packet = (SyncAutomobileAttachmentsPacket)payload;
                int entityId = packet.entityId();
                var rearAtt = RearAttachmentType.REGISTRY.getOrDefault(ResourceLocation.tryParse(packet.rearAttachId()));
                var frontAtt = FrontAttachmentType.REGISTRY.getOrDefault(ResourceLocation.tryParse(packet.frontAttachId()));
                client.execute(() -> {
                    if (client.player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
                        automobile.setRearAttachment(rearAtt);
                        automobile.setFrontAttachment(frontAtt);
                    }
                });
            }
        });
        Platform.get().clientReceivePacket(UpdateAutomobileBannerPostPacket.TYPE, (client, payload) -> {
            if (payload instanceof UpdateAutomobileBannerPostPacket) {
                UpdateAutomobileBannerPostPacket packet = (UpdateAutomobileBannerPostPacket)payload;
                client.execute(() -> {
                    if (client.player.level().getEntity(packet.entityId()) instanceof AutomobileEntity automobile &&
                            automobile.getRearAttachment() instanceof BannerPostRearAttachment bannerPost) {
                        bannerPost.setFromNetwork(packet.bannerId(), packet.baseColor(), packet.patterns());
                    }
                });
            }
        });
        Platform.get().clientReceivePacket(UpdateExtendAttachmentPacket.TYPE, (client, payload) -> {
            if (payload instanceof UpdateExtendAttachmentPacket) {
                UpdateExtendAttachmentPacket packet = (UpdateExtendAttachmentPacket)payload;
                client.execute(() -> {
                    if (client.player.level().getEntity(packet.entityId()) instanceof AutomobileEntity automobile &&
                            automobile.getRearAttachment() instanceof ExtendableRearAttachment att) {
                        att.setExtended(packet.extended());
                    }
                });
            }
        });
    }
}
