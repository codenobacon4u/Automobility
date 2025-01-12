package io.github.foundationgames.automobility.recipe;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AutoMechanicTableRecipe implements Recipe<RecipeWrapper>, Comparable<AutoMechanicTableRecipe> {
    public static final ResourceLocation ID = Automobility.rl("auto_mechanic_table");
    public static final RecipeType<AutoMechanicTableRecipe> TYPE = new RecipeType<>() {};
    //private final ResourceLocation id;

    protected final ResourceLocation category;
    protected final NonNullList<Ingredient> ingredients;
    protected final ItemStack result;
    protected final int sortNum;

    public AutoMechanicTableRecipe(ResourceLocation category, NonNullList<Ingredient> ingredients, ItemStack result, int sortNum) {
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
        this.sortNum = sortNum;
    }

    public ResourceLocation getCategory() {
        return this.category;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level lvl) {
        boolean[] result = {true};
        this.forMissingIngredients(inv, ing -> result[0] = false);

        return result[0];
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider var2) {
        return assemble(inv);
    }

    public ItemStack assemble(RecipeInput inv) {
        for (var ing : this.ingredients) {
            for (int i = 0; i < inv.size(); i++) {
                var stack = inv.getItem(i);
                if (ing.test(stack)) {
                    stack.shrink(1);
                    break;
                }
            }
        }

        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AutoMechanicTableRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public void forMissingIngredients(RecipeInput inv, Consumer<Ingredient> action) {
        var invCopy = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.size(); i++) {
            invCopy.add(inv.getItem(i));
        }

        for (var ing : this.ingredients) {
            if (invCopy.stream().noneMatch(ing)) {
                action.accept(ing);
            } else {
                invCopy.remove(invCopy.stream().filter(ing).collect(Collectors.toList()).get(0));
            }
        }
    }

    @Override
    public int compareTo(@NotNull AutoMechanicTableRecipe o) {
        int diff = this.getCategory().compareTo(o.getCategory());
        if (diff != 0) return diff;

        diff = Integer.compare(this.sortNum, o.sortNum);
        if (diff != 0) return diff;

        return this.getId().compareTo(o.getId());
    }
}
