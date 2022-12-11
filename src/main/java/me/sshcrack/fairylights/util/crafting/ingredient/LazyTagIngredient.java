package me.sshcrack.fairylights.util.crafting.ingredient;

import me.sshcrack.fairylights.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;

public class LazyTagIngredient {
    public static Ingredient of(final TagKey<Item> tag) {
        return Ingredient.ofStacks(Utils.getItemsWithTag(tag).stream().map(ItemStack::new).toArray(ItemStack[]::new));
    }
}
