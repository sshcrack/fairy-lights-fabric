package me.sshcrack.fairylights.server.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

public class PennantItem extends Item {
    public PennantItem(final Item.Settings properties) {
        super(properties);
    }

    @Override
    public Text getName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void appendStacks(final ItemGroup tab, final DefaultedList<ItemStack> subItems) {
        if (this.isIn(tab)) {
            for (final DyeColor dye : DyeColor.values()) {
                subItems.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
