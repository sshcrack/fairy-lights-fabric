package me.sshcrack.fairylights.util;

import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class SimpleProvider<T> implements ICapabilityProvider {
    private final Capability<T> capability;

    private final Optional<T> op;

    public SimpleProvider(final Capability<T> capability, final T instance) {
        this.capability = Objects.requireNonNull(capability, "capability");
        this.op = Optional.of(instance);
    }

    @Override
    public <U> Optional<U> getCapability(final Capability<U> capability, @Nullable final Direction facing) {
        return this.capability.orEmpty(capability, this.op);
    }
}
