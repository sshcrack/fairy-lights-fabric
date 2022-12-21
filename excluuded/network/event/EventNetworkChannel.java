/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network.event;


import me.sshcrack.fairylights.util.forge.network.NetworkEvent;
import me.sshcrack.fairylights.util.forge.network.NetworkInstance;
import me.sshcrack.fairylights.util.forge.network.NetworkRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An event-bus like object on which {@link NetworkEvent}s are posted.
 *
 * These events are fired from the network thread, and so should not interact with most game state by default.
 * {@link NetworkEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or client
 * thread.
 *
 * @see NetworkRegistry#newEventChannel(Identifier, Supplier, Predicate, Predicate)
 * @see NetworkRegistry.ChannelBuilder#newEventChannel(Identifier, Supplier, Predicate, Predicate)
 */
public class EventNetworkChannel
{
    private final NetworkInstance instance;

    public EventNetworkChannel(NetworkInstance instance)
    {
        this.instance = instance;
    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
    {
        instance.addListener(eventListener);
    }

    public void registerObject(Object object)
    {
        instance.registerObject(object);
    }

    public void unregisterObject(Object object)
    {
        instance.unregisterObject(object);
    }

    public boolean isRemotePresent(ClientConnection manager) {
        return instance.isRemotePresent(manager);
    }
}
