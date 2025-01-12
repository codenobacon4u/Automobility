package io.github.foundationgames.automobility.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class RecipeWrapper implements RecipeInput {

    private final SimpleContainer container;

    public RecipeWrapper(SimpleContainer container) {
        this.container = container;
    }

    public Container getContainer() {
        return this.container;
    }

    @Override
    public ItemStack getItem(int slot) {
        return container.getItem(slot);
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }
}
