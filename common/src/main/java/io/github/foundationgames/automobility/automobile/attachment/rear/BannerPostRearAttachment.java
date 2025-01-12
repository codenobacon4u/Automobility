package io.github.foundationgames.automobility.automobile.attachment.rear;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.screen.SingleSlotScreenHandler;
import io.github.foundationgames.automobility.util.network.CommonPackets;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerPostRearAttachment extends RearAttachment {
    private static final Logger LOG = LogManager.getLogger("Automobility");
    private static final Component UI_TITLE = Component.translatable("container.automobility.banner_post");

    private String bannerId;
    private @Nullable DyeColor baseColor = null;
    private BannerPatternLayers patterns;

    public final Container inventory = new SimpleContainer(1) {
        @Override
        public void setItem(int slot, ItemStack stack) {
            super.setItem(slot, stack);

            BannerPostRearAttachment.this.setFromItem(stack);
        }
    };

    public BannerPostRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    public void sendPacket() {
        if (!this.world().isClientSide()) {
            this.automobile().forNearbyPlayers(200, false, p ->
                    CommonPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), bannerId, baseColor, patterns, p));
        }
    }

    @Override
    public void updatePacketRequested(ServerPlayer player) {
        super.updatePacketRequested(player);
        CommonPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), bannerId, baseColor, patterns, player);
    }

    public void putToNbt(CompoundTag nbt) {
        if (this.bannerId != null) {
            nbt.putString("bannerId", this.bannerId);
        }

        if (this.baseColor != null) {
            nbt.putInt("color", this.baseColor.getId());
        }

        if (this.patterns != null && !this.patterns.equals(BannerPatternLayers.EMPTY)) {
            nbt.put("patterns", (Tag)BannerPatternLayers.CODEC.encodeStart(world().registryAccess().createSerializationContext(NbtOps.INSTANCE), patterns).getOrThrow());
        }
    }

    public void setFromNbt(CompoundTag nbt) {
        if (nbt.contains("bannerId")) {
            this.bannerId = nbt.getString("bannerId");
        }

        if (nbt.contains("color")) {
            this.baseColor = DyeColor.byId(nbt.getInt("color"));
        } else {
            this.baseColor = null;
        }

        if (nbt.contains("patterns")) {
            BannerPatternLayers.CODEC.parse(world().registryAccess().createSerializationContext(NbtOps.INSTANCE), nbt.get("patterns")).resultOrPartial((string) -> LOG.error("Failed to parse banner patterns: '{}'", string)).ifPresent((bannerPatternLayers) -> this.patterns = bannerPatternLayers);
        }
    }

    public void setFromNetwork(String bannerId, DyeColor baseColor, BannerPatternLayers patterns) {
        this.bannerId = bannerId;
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    public void setFromItem(ItemStack stack) {
        if (stack.getItem() instanceof BannerItem banner) {
            this.baseColor = banner.getColor();
        } else {
            this.erase();
            return;
        }

        this.patterns = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);

        if (!this.world().isClientSide()) {
            this.sendPacket();
        }
    }

    public void erase() {
        this.baseColor = null;

        if (!this.world().isClientSide()) {
            this.sendPacket();
        }
    }

    public @Nullable DyeColor getBaseColor() {
        return this.baseColor;
    }

    public BannerPatternLayers getPatterns() {
        return this.patterns;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        var pos = this.pos();
        Containers.dropItemStack(this.world(), pos.x, pos.y, pos.z, this.inventory.getItem(0));
    }

    @Override
    public void writeNbt(CompoundTag nbt) {
        super.writeNbt(nbt);
        this.putToNbt(nbt);

        // var item = new CompoundTag();
        // this.inventory.getItem(0).save(world().registryAccess(), item);
        // nbt.put("banner", item);
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.setFromNbt(nbt);
        
        var item = BuiltInRegistries.ITEM.get(new ResourceLocation(bannerId));
        var itemStack = new ItemStack(Holder.direct(item), 1, DataComponentPatch.builder().set(DataComponents.BANNER_PATTERNS, patterns).set(DataComponents.BASE_COLOR, baseColor).build());

        this.inventory.setItem(0, itemStack);
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public @Nullable MenuProvider createMenu(ContainerLevelAccess ctx) {
        return new SimpleMenuProvider((syncId, playerInv, player) ->
                new SingleSlotScreenHandler(syncId, playerInv, this.inventory), UI_TITLE);
    }
}
