/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;


import me.sshcrack.fairylights.util.forge.events.Event;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkInstance
{
    public Identifier getChannelName()
    {
        return channelName;
    }

    private final Identifier channelName;
    private final String networkProtocolVersion;
    private final Predicate<String> clientAcceptedVersions;
    private final Predicate<String> serverAcceptedVersions;
    private final IEventBus networkEventBus;

    NetworkInstance(Identifier channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion.get();
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.networkEventBus = BusBuilder.builder().setExceptionHandler(this::handleError).useModLauncher().build();
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable)
    {

    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void addGatherListener(Consumer<NetworkEvent.GatherLoginPayloadsEvent> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void registerObject(final Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(final Object object) {
        this.networkEventBus.unregister(object);
    }

    boolean dispatch(final NetworkDirection side, final ICustomPacket<?> packet, final ClientConnection manager)
    {
        final NetworkEvent.Context context = new NetworkEvent.Context(manager, side, packet.getIndex());
        this.networkEventBus.post(side.getEvent(packet, () -> context));
        return context.getPacketHandled();
    }

    String getNetworkProtocolVersion() {
        return networkProtocolVersion;
    }

    boolean tryServerVersionOnClient(final String serverVersion) {
        return this.clientAcceptedVersions.test(serverVersion);
    }

    boolean tryClientVersionOnServer(final String clientVersion) {
        return this.serverAcceptedVersions.test(clientVersion);
    }

    void dispatchGatherLogin(final List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
        this.networkEventBus.post(new NetworkEvent.GatherLoginPayloadsEvent(loginPayloadList, isLocal));
    }

    void dispatchLoginPacket(final NetworkEvent.LoginPayloadEvent loginPayloadEvent) {
        this.networkEventBus.post(loginPayloadEvent);
    }

    void dispatchEvent(final NetworkEvent networkEvent) {
        this.networkEventBus.post(networkEvent);
    }

    public boolean isRemotePresent(ClientConnection manager) {
        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        MCRegisterPacketHandler.ChannelList channelList = NetworkHooks.getChannelList(manager);
        return (connectionData != null && connectionData.getChannels().containsKey(channelName))
                // if it's not in the fml connection data, let's check if it's sent by another modloader.
                || (channelList != null && channelList.getRemoteLocations().contains(channelName));
    }
}
