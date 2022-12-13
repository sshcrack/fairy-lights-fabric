package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

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
