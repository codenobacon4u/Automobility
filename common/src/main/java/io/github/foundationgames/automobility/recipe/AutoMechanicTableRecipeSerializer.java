package io.github.foundationgames.automobility.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AutoMechanicTableRecipeSerializer implements RecipeSerializer<AutoMechanicTableRecipe> {
    private static final Codec<AutoMechanicTableRecipe> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
        ResourceLocation.CODEC.fieldOf("category").forGetter(AutoMechanicTableRecipe::getCategory),
        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(ingredients -> {
            NonNullList<Ingredient> nonNullList = NonNullList.create();
            nonNullList.addAll(ingredients);
            return nonNullList;
        }, ingredients -> ingredients).forGetter(AutoMechanicTableRecipe::getIngredients),
        ItemStack.CODEC.fieldOf("result").forGetter(AutoMechanicTableRecipe::getResultItem),
        Codec.INT.fieldOf("sortnum").forGetter(getter -> getter.sortNum)
    ).apply(builder, AutoMechanicTableRecipe::new));
    public static final AutoMechanicTableRecipeSerializer INSTANCE = new AutoMechanicTableRecipeSerializer();


    @Override
    public Codec<AutoMechanicTableRecipe> codec() {
        return CODEC;
    }

    @Override
    public AutoMechanicTableRecipe fromNetwork(FriendlyByteBuf buf) {
        var category = ResourceLocation.tryParse(buf.readUtf());

        int size = buf.readByte();
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromNetwork(buf));
        }

        var result = buf.readItem();
        int sortNum = buf.readInt();

        return new AutoMechanicTableRecipe(category, ingredients, result, sortNum);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, AutoMechanicTableRecipe recipe) {
        buf.writeUtf(recipe.category.toString());
        buf.writeByte(recipe.ingredients.size());
        recipe.ingredients.forEach(ing -> ing.toNetwork(buf));
        buf.writeItem(recipe.result);
        buf.writeInt(recipe.sortNum);
    }
}
