package me.sshcrack.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public final class TinselConnectionItem extends ConnectionItem {
    public TinselConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.TINSEL_GARLAND);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return me.paulf.fairylights.server.item.DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void fillItemCategory(final CreativeModeTab tab, final NonNullList<ItemStack> items) {
        if (this.allowedIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                items.add(me.paulf.fairylights.server.item.DyeableItem.setColor(new ItemStack(this), color));
            }
        }
    }
}
