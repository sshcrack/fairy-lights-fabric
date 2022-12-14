package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import me.sshcrack.fairylights.server.item.crafting.FLCraftingRecipes;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class PennantBuntingConnectionItem extends ConnectionItem {
    public PennantBuntingConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.PENNANT_BUNTING);
    }

    @Override
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> tooltip, final TooltipContext flag) {
        final NbtCompound compound = stack.getNbt();
        if (compound == null) {
            return;
        }
        if (compound.contains("text", NbtCompound.COMPOUND_TYPE)) {
            final NbtCompound text = compound.getCompound("text");
            final StyledString s = StyledString.deserialize(text);
            if (s.length() > 0) {
                tooltip.add(Text.translatable("format.fairylights.text", s.toTextComponent()).formatted(Formatting.GRAY));
            }
        }
        if (compound.contains("pattern", NbtCompound.LIST_TYPE)) {
            final NbtList tagList = compound.getList("pattern", NbtCompound.COMPOUND_TYPE);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(Text.empty());
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack item = ItemStack.fromNbt(tagList.getCompound(i));
                tooltip.add(item.getName());
            }
        }
    }

    @Override
    public void appendStacks(final ItemGroup tab, final DefaultedList<ItemStack> subItems) {
        if (this.isIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                DyeableItem.setColor(stack, color);
                subItems.add(FLCraftingRecipes.makePennant(stack, color));
            }
        }
    }
}
