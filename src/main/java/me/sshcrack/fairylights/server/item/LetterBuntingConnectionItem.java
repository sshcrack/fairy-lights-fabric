package me.sshcrack.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class LetterBuntingConnectionItem extends ConnectionItem {
    public LetterBuntingConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.LETTER_BUNTING);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final World world, final List<Text> tooltip, final TooltipFlag flag) {
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
    }

    @Override
    public void fillItemCategory(final CreativeModeTab tab, final NonNullList<ItemStack> items) {
        if (this.allowedIn(tab)) {
            final ItemStack bunting = new ItemStack(this, 1);
            bunting.getOrCreateTag().put("text", StyledString.serialize(new StyledString()));
            items.add(bunting);
        }
    }
}
