package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import me.sshcrack.fairylights.util.EmptyProvider;
import me.sshcrack.fairylights.util.SimpleProvider;
import me.sshcrack.fairylights.util.forge.capabilities.*;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public interface LightVariant<T extends LightBehavior> extends ICapabilitySerializable<NbtCompound> {
    final class Holder {
        public static final Capability<LightVariant<?>> CAPABILITY = CapabilityManager.of(new Identifier(FairyLightsMod.ModID, "light_fastener_cap"), LightVariant.class);
    }

    boolean parallelsCord();

    float getSpacing();

    Box getBounds();

    double getFloorOffset();

    T createBehavior(final ItemStack stack);

    boolean isOrientable();

    static LazyOptional<LightVariant<?>> get(final CapabilityHelper<?> provider) {
        return provider.getCapability(Holder.CAPABILITY);
    }
}
