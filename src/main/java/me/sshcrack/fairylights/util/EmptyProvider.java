package me.sshcrack.fairylights.util;

import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityProvider;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class EmptyProvider<U> implements CapabilityHelper<U> {
    @Override
    public @NotNull CapabilityProvider<U> getProvider() {
        return null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap) {
        return LazyOptional.empty();
    }
}
