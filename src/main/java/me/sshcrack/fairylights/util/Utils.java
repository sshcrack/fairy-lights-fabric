package me.sshcrack.fairylights.util;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Utils {
    private Utils() {}

    public static <E extends Enum<E>> E getEnumValue(final Class<E> clazz, final int ordinal) {
        final E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
        return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
    }

    public static Text formatRecipeTooltip(final String key) {
        return formatRecipeTooltipValue(Language.getInstance().get(key));
    }

    private static Text formatRecipeTooltipValue(final String value) {
        return Text.translatable("recipe.ingredient.tooltip", value);
    }

    public static boolean impliesNbt(@Nullable NbtElement antecedent, @Nullable NbtElement consequent) {
        if (antecedent == consequent) return true;
        if ((antecedent == null) != (consequent == null)) return false;
        if (!antecedent.getClass().equals(consequent.getClass())) return false;
        if (antecedent instanceof NbtCompound) {
            for (String key : ((NbtCompound) antecedent).getKeys()) {
                if (!impliesNbt(((NbtCompound) antecedent).get(key), ((NbtCompound) consequent).get(key))) {
                    return false;
                }
            }
            return true;
        }
        return antecedent.equals(consequent);
    }

    public static List<Item> getItemsWithTag(TagKey<Item> key) {
        return Registry.ITEM.getEntrySet().stream().filter(e -> e.getValue().getDefaultStack().isIn((key)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
