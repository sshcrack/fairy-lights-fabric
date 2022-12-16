/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.events;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import me.sshcrack.fairylights.util.forge.events.api.GenericEvent;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fired whenever an object with Capabilities support {currently TileEntity/Item/Entity)
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public class AttachCapabilitiesEvent<T> extends GenericEvent<T>
{
    private final T obj;
    private final Map<Identifier, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<Identifier, ICapabilityProvider> view = Collections.unmodifiableMap(caps);
    private final List<Runnable> listeners = Lists.newArrayList();
    private final List<Runnable> listenersView = Collections.unmodifiableList(listeners);

    public AttachCapabilitiesEvent(Class<T> type, T obj)
    {
        super(type);
        this.obj = obj;
    }

    /**
     * Retrieves the object that is being created, Not much state is set.
     */
    public T getObject()
    {
        return this.obj;
    }

    /**
     * Adds a capability to be attached to this object.
     * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
     * If the capability is an instance of INBTSerializable, this key will be used when serializing this capability.
     *
     * @param key The name of owner of this capability provider.
     * @param cap The capability provider
     */
    public void addCapability(Identifier key, ICapabilityProvider cap)
    {
        if (caps.containsKey(key))
            throw new IllegalStateException("Duplicate Capability Key: " + key  + " " + cap);
        this.caps.put(key, cap);
    }

    /**
     * A unmodifiable view of the capabilities that will be attached to this object.
     */
    public Map<Identifier, ICapabilityProvider> getCapabilities()
    {
        return view;
    }

    /**
     * Adds a callback that is fired when the attached object is invalidated.
     * Such as a Entity/TileEntity being removed from world.
     * All attached providers should invalidate all of their held capability instances.
     */
    public void addListener(Runnable listener)
    {
        this.listeners.add(listener);
    }

    public List<Runnable> getListeners()
    {
        return this.listenersView;
    }
}
