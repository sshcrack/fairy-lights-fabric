package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import me.sshcrack.fairylights.server.item.crafting.FLCraftingRecipes;
import me.sshcrack.fairylights.server.string.StringType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.HANGING_LIGHTS);
    }


    @Override
    public void appendTooltip(final ItemStack stack, @Nullable final World world, final List<Text> tooltip, final TooltipContext flag) {
        final NbtCompound compound = stack.getNbt();
        if (compound != null) {
            final Identifier name = FairyLightsMod.STRING_TYPE.getId(getString(compound));
            tooltip.add(Text.translatable("item." + name.getNamespace() + "." + name.getPath()).formatted(Formatting.GRAY));
        }
        if (compound != null && compound.contains("pattern", NbtCompound.LIST_TYPE)) {
            final NbtList tagList = compound.getList("pattern", NbtCompound.COMPOUND_TYPE);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(Text.empty());
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack lightStack = ItemStack.fromNbt(tagList.getCompound(i));
                tooltip.add(lightStack.getName());
                lightStack.getItem().appendTooltip(lightStack, world, tooltip, flag);
            }
        }
    }

    @Override
    public void appendStacks(final ItemGroup tab, final DefaultedList<ItemStack> subItems) {
        if (this.isIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                subItems.add(FLCraftingRecipes.makeHangingLights(new ItemStack(this), color));
            }
        }
    }

    public static StringType getString(final NbtCompound tag) {
        return Objects.requireNonNull(FairyLightsMod.STRING_TYPE.get(Identifier.tryParse(tag.getString("string"))));
    }

    public static void setString(final NbtCompound tag, final StringType string) {
        tag.putString("string", FairyLightsMod.STRING_TYPE.getId(string).toString());
    }
}
