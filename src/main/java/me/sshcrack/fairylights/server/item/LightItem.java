package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.block.LightBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightItem extends BlockItem {
    private final LightBlock light;

    public LightItem(final LightBlock light, final Settings properties) {
        super(light, properties);
        this.light = light;
    }

    @Override
    public LightBlock getBlock() {
        return this.light;
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NbtCompound nbt) {
        return LightVariant.provider(this.light.getVariant());
    }

    @Override
    public void appendTooltip(final ItemStack stack, @Nullable final World world, final List<Text> tooltip, final TooltipContext flag) {
        super.appendTooltip(stack, world, tooltip, flag);
        final NbtCompound tag = stack.getNbt();
        if (tag != null) {
            if (tag.getBoolean("twinkle")) {
                tooltip.add(Text.translatable("item.fairyLights.twinkle").formatted(Formatting.GRAY, Formatting.ITALIC));
            }
            if (tag.contains("colors", NbtElement.LIST_TYPE)) {
                final NbtList colors = tag.getList("colors", NbtElement.INT_TYPE);
                for (int i = 0; i < colors.size(); i++) {
                    tooltip.add(DyeableItem.getColorName(colors.getInt(i)).copy().formatted(Formatting.GRAY));
                }
            }
        }
    }
}
