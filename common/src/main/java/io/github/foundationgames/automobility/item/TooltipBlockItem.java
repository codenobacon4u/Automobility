package io.github.foundationgames.automobility.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import java.util.List;

public class TooltipBlockItem extends BlockItem {
    private final Component tooltip;

    public TooltipBlockItem(Block block, Component tooltip, Properties settings) {
        super(block, settings);
        this.tooltip = tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(this.tooltip);
    }
}
