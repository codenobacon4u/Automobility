package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AutoMechanicTableBlock extends Block {
    public static final Component UI_TITLE = Component.translatable("container.automobility.auto_mechanic_table");

    public AutoMechanicTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider((syncId, playerInventory, player) ->
                new AutoMechanicTableScreenHandler(syncId, playerInventory, ContainerLevelAccess.create(world, pos)), UI_TITLE);
    }
}
