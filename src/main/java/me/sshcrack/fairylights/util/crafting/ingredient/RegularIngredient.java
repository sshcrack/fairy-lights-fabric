package me.sshcrack.fairylights.util.crafting.ingredient;

import me.sshcrack.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface RegularIngredient extends GenericIngredient<RegularIngredient, GenericRecipe.MatchResultRegular> {
    default void matched(final ItemStack ingredient, final NbtCompound nbt) {}
}
