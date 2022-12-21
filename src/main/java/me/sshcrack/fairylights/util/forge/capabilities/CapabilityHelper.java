package me.sshcrack.fairylights.util.forge.capabilities;

import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public interface CapabilityHelper<T> {
    @NotNull CapabilityProvider<T> getProvider();
    @NotNull <B> LazyOptional<B> getCapability(@NotNull final Capability<B> cap);
}
