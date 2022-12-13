package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import net.minecraft.item.Item;

import java.util.List;

public class PennantBuntingConnectionItem extends ConnectionItem {
    public PennantBuntingConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.PENNANT_BUNTING);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        final CompoundTag compound = stack.getTag();
        if (compound == null) {
            return;
        }
        if (compound.contains("text", Tag.TAG_COMPOUND)) {
            final CompoundTag text = compound.getCompound("text");
            final StyledString s = StyledString.deserialize(text);
            if (s.length() > 0) {
                tooltip.add(Component.translatable("format.fairylights.text", s.toTextComponent()).withStyle(ChatFormatting.GRAY));
            }
        }
        if (compound.contains("pattern", Tag.TAG_LIST)) {
            final ListTag tagList = compound.getList("pattern", Tag.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(Component.empty());
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack item = ItemStack.of(tagList.getCompound(i));
                tooltip.add(item.getHoverName());
            }
        }
    }

    @Override
    public void fillItemCategory(final CreativeModeTab tab, final NonNullList<ItemStack> subItems) {
        if (this.allowedIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                DyeableItem.setColor(stack, color);
                subItems.add(FLCraftingRecipes.makePennant(stack, color));
            }
        }
    }
}
