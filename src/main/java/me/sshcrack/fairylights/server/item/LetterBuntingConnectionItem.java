package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class LetterBuntingConnectionItem extends ConnectionItem {
    public LetterBuntingConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.LETTER_BUNTING);
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
    }

    @Override
    public void appendStacks(final ItemGroup tab, final DefaultedList<ItemStack> items) {
        if (this.isIn(tab)) {
            final ItemStack bunting = new ItemStack(this, 1);
            bunting.getOrCreateNbt().put("text", StyledString.serialize(new StyledString()));
            items.add(bunting);
        }
    }
}
