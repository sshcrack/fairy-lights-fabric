package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.block.LightBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Item.Settings properties) {
        super(light, properties);
    }

    @Override
    public Text getName(final ItemStack stack) {
        final NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("colors", NbtElement.LIST_TYPE)) {
            return Text.translatable("format.fairylights.color_changing", super.getName(stack));
        }
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void appendStacks(final ItemGroup group, final DefaultedList<ItemStack> items) {
        if (this.isIn(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
