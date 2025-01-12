package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.network.ClientPackets.RequestSyncAutomobileComponentsPacket;
import io.github.foundationgames.automobility.util.network.ClientPackets.SyncAutomobileInputsPacket;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public enum CommonPackets {;
    public record SyncAutomobileDataPacket(byte[] data) implements CustomPacketPayload {
        public static final Type<SyncAutomobileDataPacket> TYPE = new Type<>(Automobility.rl("sync_automobile_data"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncAutomobileDataPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncAutomobileDataPacket::write, SyncAutomobileDataPacket::new);

        private SyncAutomobileDataPacket(FriendlyByteBuf buf) {
            this(ByteBufUtil.getBytes(buf));
            buf.readerIndex(buf.readerIndex() + this.data.length);
        }

        private void write(FriendlyByteBuf buf) {
            buf.writeBytes(this.data);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendSyncAutomobileDataPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        entity.writeSyncToClientData(buf);
        SyncAutomobileDataPacket packet = new SyncAutomobileDataPacket(buf);
        Platform.get().serverSendPacket(player, packet);
    }

    public record SyncAutomobileComponentsPacket(int entityId, String frameId, String wheelsId, String engineId) implements CustomPacketPayload {
        public static final Type<SyncAutomobileComponentsPacket> TYPE = new Type<>(Automobility.rl("sync_automobile_components"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncAutomobileComponentsPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                SyncAutomobileComponentsPacket::entityId,
                ByteBufCodecs.STRING_UTF8,
                SyncAutomobileComponentsPacket::frameId,
                ByteBufCodecs.STRING_UTF8,
                SyncAutomobileComponentsPacket::wheelsId,
                ByteBufCodecs.STRING_UTF8,
                SyncAutomobileComponentsPacket::engineId,
                SyncAutomobileComponentsPacket::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendSyncAutomobileComponentsPacket(AutomobileEntity entity, ServerPlayer player) {
        SyncAutomobileComponentsPacket packet = new SyncAutomobileComponentsPacket(
                entity.getId(),
                entity.getFrame().id().toString(),
                entity.getWheels().id().toString(),
                entity.getEngine().id().toString());
        Platform.get().serverSendPacket(player, packet);
    }

    public record SyncAutomobileAttachmentsPacket(int entityId, String rearAttachId, String frontAttachId) implements CustomPacketPayload {
        public static final Type<SyncAutomobileAttachmentsPacket> TYPE = new Type<>(Automobility.rl("sync_automobile_attachments"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncAutomobileAttachmentsPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                SyncAutomobileAttachmentsPacket::entityId,
                ByteBufCodecs.STRING_UTF8,
                SyncAutomobileAttachmentsPacket::rearAttachId,
                ByteBufCodecs.STRING_UTF8,
                SyncAutomobileAttachmentsPacket::frontAttachId,
                SyncAutomobileAttachmentsPacket::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendSyncAutomobileAttachmentsPacket(AutomobileEntity entity, ServerPlayer player) {
        SyncAutomobileAttachmentsPacket packet = new SyncAutomobileAttachmentsPacket(
            entity.getId(), 
            entity.getRearAttachmentType().id().toString(), 
            entity.getFrontAttachmentType().id().toString());
        Platform.get().serverSendPacket(player, packet);
    }

    public record UpdateAutomobileBannerPostPacket(int entityId, String bannerId, DyeColor baseColor, BannerPatternLayers patterns) implements CustomPacketPayload {
        public static final Type<UpdateAutomobileBannerPostPacket> TYPE = new Type<>(Automobility.rl("update_banner_attachment"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateAutomobileBannerPostPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                UpdateAutomobileBannerPostPacket::entityId,
                ByteBufCodecs.STRING_UTF8,
                UpdateAutomobileBannerPostPacket::bannerId,
                DyeColor.STREAM_CODEC,
                UpdateAutomobileBannerPostPacket::baseColor,
                BannerPatternLayers.STREAM_CODEC,
                UpdateAutomobileBannerPostPacket::patterns,
                UpdateAutomobileBannerPostPacket::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendBannerPostAttachmentUpdatePacket(AutomobileEntity entity, String bannerId, DyeColor baseColor, BannerPatternLayers patterns, ServerPlayer player) {
        if (entity.getRearAttachment() instanceof BannerPostRearAttachment) {
            UpdateAutomobileBannerPostPacket packet = new UpdateAutomobileBannerPostPacket(entity.getId(), bannerId, baseColor, patterns);
            Platform.get().serverSendPacket(player, packet);
        }
    }

    public record UpdateExtendAttachmentPacket(int entityId, Boolean extended) implements CustomPacketPayload {
        public static final Type<UpdateExtendAttachmentPacket> TYPE = new Type<>(Automobility.rl("update_extendable_attachment"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateExtendAttachmentPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                UpdateExtendAttachmentPacket::entityId,
                ByteBufCodecs.BOOL,
                UpdateExtendAttachmentPacket::extended,
                UpdateExtendAttachmentPacket::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendExtendableAttachmentUpdatePacket(AutomobileEntity entity, boolean extended, ServerPlayer player) {
        if (entity.getRearAttachment() instanceof ExtendableRearAttachment) {
            UpdateExtendAttachmentPacket packet = new UpdateExtendAttachmentPacket(entity.getId(), extended);
            Platform.get().serverSendPacket(player, packet);
        }
    }

    public static void init() {
        Platform.get().serverReceivePacket(SyncAutomobileInputsPacket.TYPE, (server, player, payload) -> {
            if (payload instanceof SyncAutomobileInputsPacket) {
                SyncAutomobileInputsPacket packet = (SyncAutomobileInputsPacket) payload;
                server.execute(() -> {
                    if (player.level().getEntity(packet.entityId()) instanceof AutomobileEntity automobile) {
                        automobile.setInputs(packet.fwd(), packet.back(), packet.left(), packet.right(), packet.space());
                        automobile.markDirty();
                    }
                });
            }
        });
        Platform.get().serverReceivePacket(RequestSyncAutomobileComponentsPacket.TYPE, (server, player, payload) -> {
            if (payload instanceof RequestSyncAutomobileComponentsPacket) {
                RequestSyncAutomobileComponentsPacket packet = (RequestSyncAutomobileComponentsPacket) payload;
                int entityId = packet.entityId();
                server.execute(() -> {
                    if (player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
                        sendSyncAutomobileComponentsPacket(automobile, player);
                        sendSyncAutomobileAttachmentsPacket(automobile, player);

                        var fAtt = automobile.getFrontAttachment();
                        if (fAtt != null)
                            fAtt.updatePacketRequested(player);

                        var rAtt = automobile.getRearAttachment();
                        if (rAtt != null)
                            rAtt.updatePacketRequested(player);
                    }
                });
            }
        });
    }
}
