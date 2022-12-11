package me.sshcrack.fairylights.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class OreDictUtils {
    private OreDictUtils() {}

    public static boolean isDye(final ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof DyeItem) {
                return true;
            }
            return stack.isIn(ConventionalItemTags.DYES);
        }
        return false;
    }

    public static DyeColor getDyeColor(final ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof DyeItem) {
                return ((DyeItem) stack.getItem()).getColor();
            }
            for (final Dye dye : Dye.values()) {
                if (stack.isIn(dye.getName())) {
                    return dye.getColor();
                }
            }
        }
        return DyeColor.YELLOW;
    }

    public static ImmutableList<ItemStack> getDyes(final DyeColor color) {
        return getDyeItemStacks().get(color).asList();
    }

    public static ImmutableList<ItemStack> getAllDyes() {
        return getDyeItemStacks().values().asList();
    }

    private static ImmutableMultimap<DyeColor, ItemStack> getDyeItemStacks() {
        final ImmutableMultimap.Builder<DyeColor, ItemStack> bob = ImmutableMultimap.builder();
        for (final Dye dye : Dye.values()) {
            TagKey<Item> key = dye.getName();

            List<Item> dyes = Utils.getItemsWithTag(key);
            for (final Item holder : dyes) {
                bob.put(dye.getColor(), holder.getDefaultStack());
            }
        }
        return bob.build();
    }

    private enum Dye {
        WHITE(ConventionalItemTags.WHITE_DYES, DyeColor.WHITE),
        ORANGE(ConventionalItemTags.ORANGE_DYES, DyeColor.ORANGE),
        MAGENTA(ConventionalItemTags.MAGENTA_DYES, DyeColor.MAGENTA),
        LIGHT_BLUE(ConventionalItemTags.LIGHT_BLUE_DYES, DyeColor.LIGHT_BLUE),
        YELLOW(ConventionalItemTags.YELLOW_DYES, DyeColor.YELLOW),
        LIME(ConventionalItemTags.LIME_DYES, DyeColor.LIME),
        PINK(ConventionalItemTags.PINK_DYES, DyeColor.PINK),
        GRAY(ConventionalItemTags.GRAY_DYES, DyeColor.GRAY),
        LIGHT_GRAY(ConventionalItemTags.LIGHT_GRAY_DYES, DyeColor.LIGHT_GRAY),
        CYAN(ConventionalItemTags.CYAN_DYES, DyeColor.CYAN),
        PURPLE(ConventionalItemTags.PURPLE_DYES, DyeColor.PURPLE),
        BLUE(ConventionalItemTags.BLUE_DYES, DyeColor.BLUE),
        BROWN(ConventionalItemTags.BROWN_DYES, DyeColor.BROWN),
        GREEN(ConventionalItemTags.GREEN_DYES, DyeColor.GREEN),
        RED(ConventionalItemTags.RED_DYES, DyeColor.RED),
        BLACK(ConventionalItemTags.BLACK_DYES, DyeColor.BLACK);

        private final TagKey<Item> name;

        private final DyeColor color;

        Dye(final TagKey<Item> name, final DyeColor color) {
            this.name = name;
            this.color = color;
        }

        private TagKey<Item> getName() {
            return this.name;
        }

        private DyeColor getColor() {
            return this.color;
        }
    }
}
