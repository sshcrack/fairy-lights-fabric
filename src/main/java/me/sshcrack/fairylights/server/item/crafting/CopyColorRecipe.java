package me.sshcrack.fairylights.server.item.crafting;

import me.sshcrack.fairylights.server.item.DyeableItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CopyColorRecipe extends SpecialCraftingRecipe {
    public CopyColorRecipe(final Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        int count = 0;
        for (int i = 0; i < inv.size(); i++) {
            final ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty() && (!stack.isIn(FLCraftingRecipes.DYEABLE) || count++ >= 2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(final CraftingInventory inv) {
        ItemStack original = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); i++) {
            final ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isIn(FLCraftingRecipes.DYEABLE)) {
                    if (original.isEmpty()) {
                        original = stack;
                    } else {
                        final ItemStack copy = stack.copy();
                        copy.setCount(1);
                        DyeableItem.setColor(copy, DyeableItem.getColor(original));
                        return copy;
                    }
                } else {
                    break;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(final CraftingInventory inv) {
        ItemStack original = ItemStack.EMPTY;
        final DefaultedList<ItemStack> remaining = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); i++) {
            final ItemStack stack = inv.getStack(i);
            if (stack.getItem().hasRecipeRemainder()) {
                remaining.set(i, stack.getRecipeRemainder());
            } else if (original.isEmpty() && !stack.isEmpty() && stack.isIn(FLCraftingRecipes.DYEABLE)) {
                final ItemStack rem = stack.copy();
                rem.setCount(1);
                remaining.set(i, rem);
                original = stack;
            }
        }
        return remaining;
    }

    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FLCraftingRecipes.COPY_COLOR;
    }
}
