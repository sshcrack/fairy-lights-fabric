package me.sshcrack.fairylights.util;

import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class EmptyProvider implements ICapabilityProvider {
    @Override
    public <T> Optional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return Optional.empty();
    }
}
