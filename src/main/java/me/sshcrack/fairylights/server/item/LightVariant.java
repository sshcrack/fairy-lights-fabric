package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import me.sshcrack.fairylights.util.EmptyProvider;
import me.sshcrack.fairylights.util.SimpleProvider;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityManager;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.Optional;

public interface LightVariant<T extends LightBehavior> {
    final class Holder {
        public static final Capability<LightVariant<?>> CAPABILITY = CapabilityManager.of(new Identifier(FairyLightsMod.ModID, "light_fastener_cap"), LightVariant.class);
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
