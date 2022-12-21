package me.sshcrack.fairylights.util;

import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityProvider;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class SimpleProvider<T, U> implements CapabilityHelper<U> {
    private final Capability<T> capability;
    private final Optional<T> op;

    public SimpleProvider(final Capability<T> capability, final T instance) {
        this.capability = Objects.requireNonNull(capability, "capability");
        this.op = Optional.of(instance);
    }

    @Override
    public @NotNull CapabilityProvider<U> getProvider() {
        return null;
    }

    @Override
    public <U> @NotNull LazyOptional<U> getCapability(final @NotNull Capability<U> capability) {
        Optional<U> opt = this.capability.orEmpty(capability, this.op);

        if(opt.isPresent())
            return LazyOptional.empty();

        return LazyOptional.of(opt::get);
    }
}
