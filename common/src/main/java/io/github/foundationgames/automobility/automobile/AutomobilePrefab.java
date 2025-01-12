package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.item.AutomobilityItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record AutomobilePrefab(ResourceLocation id, AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
    public ItemStack toStack() {
        ItemStack stack = new ItemStack(AutomobilityItems.AUTOMOBILE.require());
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag automobile = new CompoundTag();
        automobile.putString("frame", frame().getId().toString());
        automobile.putString("wheels", wheel().getId().toString());
        automobile.putString("engine", engine().getId().toString());
        automobile.putBoolean("isPrefab", true);
        data.put("Automobile", automobile);
        var display = new CompoundTag();
        display.putString("Name", String.format("{\"translate\":\"prefab.%s.%s\",\"italic\":\"false\"}", id().getNamespace(), id().getPath()));
        data.put("display", display);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        return stack;
    }
}
