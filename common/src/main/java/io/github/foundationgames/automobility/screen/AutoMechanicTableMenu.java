package io.github.foundationgames.automobility.screen;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import io.github.foundationgames.automobility.recipe.RecipeWrapper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
public class AutoMechanicTableMenu extends RecipeBookMenu<RecipeWrapper, AutoMechanicTableRecipe> {
    private final Level world;
    private final ContainerLevelAccess context;
    private final DataSlot selectedRecipe = DataSlot.standalone();

    public List<AutoMechanicTableRecipe> recipes;

    public final List<Ingredient> missingIngredients = new ArrayList<>();
    public final RecipeWrapper inputInv;
    public final Slot outputSlot;

    private final int playerInvSlot;

    public AutoMechanicTableMenu(int syncId, Inventory playerInv) {
        this(syncId, playerInv, ContainerLevelAccess.NULL);
    }

    public AutoMechanicTableMenu(int syncId, Inventory playerInv, ContainerLevelAccess access) {
        super(Automobility.AUTO_MECHANIC_SCREEN.require("Auto mechanic screen not registered!"), syncId);
        this.world = playerInv.player.level();
        this.context = access;
        this.inputInv = new RecipeWrapper(new SimpleContainer(9) {
            @Override public void setChanged() { AutoMechanicTableMenu.this.onInputUpdated(); }
        });

        for(int s = 0; s < 9; s++) {
            this.addSlot(new InputSlot(this.inputInv.getContainer(), s, 8 + (s * 18), 88));
        }
        this.outputSlot = this.addSlot(new OutputSlot(new SimpleContainer(1), 0, 26, 38));

        this.playerInvSlot = this.slots.size();
        int playerInvY = 127;
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + (row * 9) + 9, 8 + (col * 18), playerInvY + (row * 18)));
            }
        }
        for(int s = 0; s < 9; s++) {
            this.addSlot(new Slot(playerInv, s, 8 + (s * 18), playerInvY + 58));
        }

        this.recipes = new ArrayList<>();
        world.getRecipeManager().getAllRecipesFor(AutoMechanicTableRecipe.TYPE).forEach(holder -> this.recipes.add(holder.value()));
        Collections.sort(this.recipes);

        this.selectedRecipe.set(-1);
        this.addDataSlot(this.selectedRecipe);
    }

    public Optional<AutoMechanicTableRecipe> getSelectedRecipe() {
        int id = this.selectedRecipe.get();
        return (id >= 0 && this.recipes.size() > 0 && id < this.recipes.size()) ? Optional.of(this.recipes.get(id)) : Optional.empty();
    }

    public int getSelectedRecipeId() {
        return this.selectedRecipe.get();
    }

    private void updateMissingIngredients() {
        this.missingIngredients.clear();

        this.getSelectedRecipe().ifPresent(recipe -> recipe.forMissingIngredients(this.inputInv, this.missingIngredients::add));
    }

    private void updateRecipeState() {
        this.updateMissingIngredients();

        this.getSelectedRecipe().ifPresent(recipe -> {
            if (recipe.matches(this.inputInv, this.world)) {
                this.outputSlot.set(recipe.getResultItem().copy());
            } else {
                this.outputSlot.set(ItemStack.EMPTY);
            }
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < this.recipes.size()) {
            this.selectRecipe(id);
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    private void onInputUpdated() {
        updateRecipeState();
    }

    private void selectRecipe(int id) {
        this.selectedRecipe.set(id);
        updateRecipeState();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        this.outputSlot.set(ItemStack.EMPTY);
        this.context.execute((world, pos) -> this.clearContainer(player, this.inputInv.getContainer()));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, AutomobilityBlocks.AUTO_MECHANIC_TABLE.require());
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.outputSlot && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int fromSlotId) {
        var newStack = ItemStack.EMPTY;
        var fromSlot = this.slots.get(fromSlotId);

        if (fromSlot.hasItem()) {
            var fromStack = fromSlot.getItem();
            var fromItem = fromStack.getItem();
            newStack = fromStack.copy();

            // Items transferred out of output slot
            if (fromSlotId == this.outputSlot.index) {
                fromItem.onCraftedBy(fromStack, player.level(), player);
                if (!this.moveItemStackTo(fromStack, this.playerInvSlot, this.playerInvSlot + 36, true)) {
                    return ItemStack.EMPTY;
                }

                fromSlot.onQuickCraft(fromStack, newStack);
            // Items transferred out of input row
            } else if (this.slots.stream().anyMatch(s -> s.container == this.inputInv && s.index == fromSlotId)) {
                if (!this.moveItemStackTo(fromStack, this.playerInvSlot, this.playerInvSlot + 36, false)) {
                    return ItemStack.EMPTY;
                }
            // Items being transferred into the input row, which match the missing ingredients
            } else if (this.missingIngredients.stream().anyMatch(ing -> ing.test(fromStack))) {
                if (!this.moveItemStackTo(fromStack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            // Items transferred from inventory to hotbar
            } else if (fromSlotId >= this.playerInvSlot && fromSlotId < this.playerInvSlot + 27) {
                if (!this.moveItemStackTo(fromStack, this.playerInvSlot + 27, this.playerInvSlot + 36, false)) {
                    return ItemStack.EMPTY;
                }
            // Items transferred from hotbar to inventory
            } else if (fromSlotId >= this.playerInvSlot + 27 && fromSlotId < this.playerInvSlot + 36 &&
                    !this.moveItemStackTo(fromStack, this.playerInvSlot, this.playerInvSlot + 27, false)) {
                return ItemStack.EMPTY;
            }

            if (fromStack.isEmpty()) {
                fromSlot.set(ItemStack.EMPTY);
            }
            fromSlot.setChanged();

            if (fromStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }
            fromSlot.onTake(player, fromStack);
            this.broadcastChanges();
        }

        return newStack;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents var1) {

    }

    @Override
    public void clearCraftingContent() {

    }

    @Override
    public boolean recipeMatches(RecipeHolder<AutoMechanicTableRecipe> recipe) {
        return recipe.value().matches(this.inputInv, this.world);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return 0;
    }

    @Override
    public int getGridHeight() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return null;
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex != this.getResultSlotIndex();
    }

    public static class InputSlot extends Slot {
        public InputSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
    }

    public class OutputSlot extends Slot {
        public OutputSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);

            AutoMechanicTableMenu.this.getSelectedRecipe()
                    .ifPresent(recipe -> {
                        recipe.assemble(AutoMechanicTableMenu.this.inputInv);
                        stack.getItem().onCraftedBy(stack, player.level(), player);
                        AutoMechanicTableMenu.this.updateRecipeState();
                    });
        }
    }
}
