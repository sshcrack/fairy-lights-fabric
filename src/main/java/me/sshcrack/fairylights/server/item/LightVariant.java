package me.sshcrack.fairylights.server.item;

import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.util.EmptyProvider;
import me.paulf.fairylights.util.SimpleProvider;
import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.swing.*;
import java.util.Optional;

public interface LightVariant<T extends LightBehavior> {
    final class Holder {
        public static Capability<LightVariant<?>> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    }

    boolean parallelsCord();

    float getSpacing();

    Box getBounds();

    double getFloorOffset();

    T createBehavior(final ItemStack stack);

    boolean isOrientable();

    static Optional<LightVariant<?>> get(final ICapabilityProvider provider) {
        return provider.getCapability(Holder.CAPABILITY);
    }

    static ICapabilityProvider provider(final LightVariant<?> variant) {
        return Holder.CAPABILITY == null ? new EmptyProvider() : new SimpleProvider<>(Holder.CAPABILITY, variant);
    }
}
