
/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.capabilities;

import com.google.common.annotations.VisibleForTesting;
import me.sshcrack.fairylights.util.forge.events.EventFactory;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class CapabilityProvider<B> implements ICapabilityProviderImpl<B>
{
    @VisibleForTesting
    static boolean SUPPORTS_LAZY_CAPABILITIES = true;

    private final @NotNull Class<B> baseClass;
    private @Nullable CapabilityDispatcher capabilities;
    private boolean valid = true;

    private boolean                       isLazy             = false;
    private Supplier<ICapabilityProvider> lazyParentSupplier = null;
    private NbtCompound                   lazyData           = null;
    private boolean initialized = false;
    private B parent;

    public CapabilityProvider(Class<B> baseClass, B parent)
    {
        this(baseClass, false, parent);
    }

    protected CapabilityProvider(final @NotNull Class<B> baseClass, final boolean isLazy, B parent)
    {
        this.parent = parent;
        this.baseClass = baseClass;
        this.isLazy = SUPPORTS_LAZY_CAPABILITIES && isLazy;
    }

    public final void gatherCapabilities()
    {
        gatherCapabilities(() -> null);
    }

    public final void gatherCapabilities(@Nullable ICapabilityProvider parent)
    {
        gatherCapabilities(() -> parent);
    }

    public final void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent)
    {
        if (isLazy && !initialized)
        {
            lazyParentSupplier = parent == null ? () -> null : parent;
            return;
        }

        doGatherCapabilities(parent == null ? null : parent.get());
    }

    private void doGatherCapabilities(@Nullable ICapabilityProvider parent)
    {
        this.capabilities = EventFactory.gatherCapabilities(baseClass, getProvider(), parent);
        this.initialized = true;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    B getProvider()
    {
        return parent;
    }

    public final @Nullable CapabilityDispatcher getCapabilities()
    {
        if (isLazy && !initialized)
        {
            doGatherCapabilities(lazyParentSupplier == null ? null : lazyParentSupplier.get());
            if (lazyData != null)
            {
                deserializeCaps(lazyData);
            }
        }

        return capabilities;
    }

    public final boolean areCapsCompatible(CapabilityProvider<B> other)
    {
        return areCapsCompatible(other.getCapabilities());
    }

    public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other)
    {
        final CapabilityDispatcher disp = getCapabilities();
        if (disp == null)
        {
            if (other == null)
            {
                return true;
            }
            else
            {
                return other.areCompatible(null);
            }
        }
        else
        {
            return disp.areCompatible(other);
        }
    }

    protected final @Nullable NbtCompound serializeCaps()
    {
        if (isLazy && !initialized)
        {
            return lazyData;
        }

        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
        {
            return disp.serializeNBT();
        }
        return null;
    }

    protected final void deserializeCaps(NbtCompound tag)
    {
        if (isLazy && !initialized)
        {
            lazyData = tag;
            return;
        }

        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
        {
            disp.deserializeNBT(tag);
        }
    }

    /*
     * Invalidates all the contained caps, and prevents getCapability from returning a value.
     * This is usually called when the object in question is removed from the world.
     * However there may be cases where modders want to copy these 'invalid' caps.
     * They should call reviveCaps while they are doing their work, and then call invalidateCaps again
     * when they are finished.
     * Be sure to make your invalidate callbaks recursion safe.
     */
    public void invalidateCaps()
    {
        this.valid = false;
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
            disp.invalidate();
    }

    /*
     * This function will allow getCability to return values again.
     * Modders can use this if they need to copy caps from one removed provider to a new one.
     * It is expected the modders who call this function, then call invalidateCaps() to invalidate the provider again.
     */
    public void reviveCaps()
    {
        this.valid = true; //Stupid players don't copy the entity when transporting across worlds.
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        final CapabilityDispatcher disp = getCapabilities();
        return !valid || disp == null ? LazyOptional.empty() : disp.getCapability(cap);
    }

    /**
     * Special implementation for cases which have a superclass and can't extend CapabilityProvider directly.
     * See {@link net.minecraft.world.chunk.WorldChunk}
     */
    public static class AsField<B extends ICapabilityProviderImpl<B>> extends CapabilityProvider<B>
    {
        private final B owner;

        public AsField(Class<B> baseClass, B owner, B parent)
        {
            super(baseClass, parent);
            this.owner = owner;
        }

        public AsField(Class<B> baseClass, B owner, boolean isLazy, B parent)
        {
            super(baseClass, isLazy, parent);
            this.owner = owner;
        }

        public void initInternal()
        {
            gatherCapabilities();
        }

        @Nullable
        public NbtCompound serializeInternal()
        {
            return serializeCaps();
        }

        public void deserializeInternal(NbtCompound tag)
        {
            deserializeCaps(tag);
        }

        @Override
        @NotNull
        B getProvider()
        {
            return owner;
        }
    };

}
