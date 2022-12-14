package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

public final class TinselConnectionItem extends ConnectionItem {
    public TinselConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.TINSEL_GARLAND);
    }

    @Override
    public Text getName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void appendStacks(final ItemGroup tab, final DefaultedList<ItemStack> items) {
        if (this.isIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), color));
            }
        }
    }
}
