package me.sshcrack.fairylights.util.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OtherTags {
    public static TagKey<Item> register(String name) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("c", name));
    }

    public static final TagKey<Item> GLOWSTONE_DUSTS = register("glowstone_dusts");
    public static final TagKey<Item> NUGGETS_GOLD = register("gold_nuggets");
    public static final TagKey<Item> STRING = register("string");
    public static final TagKey<Item> BONES = register("bones");
    public static final TagKey<Block> FENCES = TagKey.of(Registry.BLOCK_KEY, new Identifier("minecraft", "fences"));
}
