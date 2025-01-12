package io.github.foundationgames.automobility.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AutoMechanicTableRecipeSerializer implements RecipeSerializer<AutoMechanicTableRecipe> {
    private static final MapCodec<AutoMechanicTableRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("category").forGetter(AutoMechanicTableRecipe::getCategory),
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(ingredients -> {
                NonNullList<Ingredient> nonNullList = NonNullList.create();
                nonNullList.addAll(ingredients);
                return nonNullList;
            }, ingredients -> ingredients).forGetter(AutoMechanicTableRecipe::getIngredients),
            ItemStack.CODEC.fieldOf("result").forGetter(AutoMechanicTableRecipe::getResultItem),
            Codec.INT.fieldOf("sortnum").forGetter(getter -> getter.sortNum)
    ).apply(builder, AutoMechanicTableRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, AutoMechanicTableRecipe> STREAM_CODEC = StreamCodec.of(AutoMechanicTableRecipeSerializer::toNetwork, AutoMechanicTableRecipeSerializer::fromNetwork);
    public static final AutoMechanicTableRecipeSerializer INSTANCE = new AutoMechanicTableRecipeSerializer();

    @Override
    public MapCodec<AutoMechanicTableRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AutoMechanicTableRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    private static AutoMechanicTableRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        var category = ResourceLocation.tryParse(buf.readUtf());

        int size = buf.readByte();
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        }

        var result = ItemStack.STREAM_CODEC.decode(buf);
        int sortNum = buf.readInt();

        return new AutoMechanicTableRecipe(category, ingredients, result, sortNum);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, AutoMechanicTableRecipe recipe) {
        buf.writeUtf(recipe.category.toString());
        buf.writeByte(recipe.ingredients.size());
        recipe.ingredients.forEach(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing));
        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        buf.writeInt(recipe.sortNum);
    }
}
