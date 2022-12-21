
/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import me.sshcrack.fairylights.mixin.IClientConnectionMixin;
import me.sshcrack.fairylights.util.forge.events.Event;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NetworkEvent extends Event
{
    private final PacketByteBuf payload;
    private final Supplier<Context> source;
    private final int loginIndex;

    private NetworkEvent(final ICustomPacket<?> payload, final Supplier<Context> source)
    {
        this.payload = payload.getInternalData();
        this.source = source;
        this.loginIndex = payload.getIndex();
    }

    private NetworkEvent(final PacketByteBuf payload, final Supplier<Context> source, final int loginIndex)
    {
        this.payload = payload;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public NetworkEvent(final Supplier<Context> source) {
        this.source = source;
        this.payload = null;
        this.loginIndex = -1;
    }

    public PacketByteBuf getPayload()
    {
        return payload;
    }

    public Supplier<Context> getSource()
    {
        return source;
    }

    public int getLoginIndex()
    {
        return loginIndex;
    }

    public static class ServerCustomPayloadEvent extends NetworkEvent
    {
        ServerCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ClientCustomPayloadEvent extends NetworkEvent
    {
        ClientCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ServerCustomPayloadLoginEvent extends ServerCustomPayloadEvent {
        ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
        }
    }

    public static class ClientCustomPayloadLoginEvent extends ClientCustomPayloadEvent {
        ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
        }
    }

    public static class GatherLoginPayloadsEvent extends Event {
        private final List<NetworkRegistry.LoginPayload> collected;
        private final boolean isLocal;

        public GatherLoginPayloadsEvent(final List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
            this.collected = loginPayloadList;
            this.isLocal = isLocal;
        }

        public void add(PacketByteBuf buffer, Identifier channelName, String context) {
            collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
        }

        public void add(PacketByteBuf buffer, Identifier channelName, String context, boolean needsResponse)
        {
            collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context, needsResponse));
        }

        public boolean isLocal() {
            return isLocal;
        }
    }

    public static class LoginPayloadEvent extends NetworkEvent {
        LoginPayloadEvent(final PacketByteBuf payload, final Supplier<Context> source, final int loginIndex) {
            super(payload, source, loginIndex);
        }
    }

    public enum RegistrationChangeType {
        REGISTER, UNREGISTER;
    }

    /**
     * Fired when the channel registration (see minecraft custom channel documentation) changes. Note the payload
     * is not exposed. This fires to the resource location that owns the channel, when it's registration changes state.
     *
     * It seems plausible that this will fire multiple times for the same state, depending on what the server is doing.
     * It just directly dispatches upon receipt.
     */
    public static class ChannelRegistrationChangeEvent extends NetworkEvent {
        private final RegistrationChangeType changeType;

        ChannelRegistrationChangeEvent(final Supplier<Context> source, RegistrationChangeType changeType) {
            super(source);
            this.changeType = changeType;
        }

        public RegistrationChangeType getRegistrationChangeType() {
            return this.changeType;
        }
    }
    /**
     * Context for {@link NetworkEvent}
     */
    public static class Context
    {
        /**
         * The {@link ClientConnection} for this message.
         */
        private final ClientConnection networkManager;

        /**
         * The {@link NetworkDirection} this message has been received on.
         */
        private final NetworkDirection networkDirection;

        /**
         * The packet dispatcher for this event. Sends back to the origin.
         */
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        Context(ClientConnection netHandler, NetworkDirection networkDirection, int index)
        {
            this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection.reply()::buildPacket));
        }

        Context(ClientConnection networkManager, NetworkDirection networkDirection, final BiConsumer<Identifier, PacketByteBuf> packetSink) {
            this(networkManager, networkDirection, new PacketDispatcher(packetSink));
        }

        Context(ClientConnection networkManager, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.networkDirection = networkDirection;
            this.packetDispatcher = dispatcher;
        }

        public NetworkDirection getDirection() {
            return networkDirection;
        }

        public PacketDispatcher getPacketDispatcher() {
            return packetDispatcher;
        }

        public Channel getConnectionChannel() {
            return ((IClientConnectionMixin) networkManager).getChannel();
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return getConnectionChannel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled()
        {
            return packetHandled;
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection().getReceptionSide());
            // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
            // Same logic as ThreadTaskExecutor#runImmediately without the join
            if (!executor.isSameThread()) {
                return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice
            } else {
                runnable.run();
                return CompletableFuture.completedFuture(null);
            }
        }

        /**
         * When available, gets the sender for packets that are sent from a client to the server.
         */
        @Nullable
        public ServerPlayerEntity getSender()
        {
            PacketListener netHandler = networkManager.getPacketListener();
            if (netHandler instanceof ServerGamePacketListenerImpl)
            {
                ServerGamePacketListenerImpl netHandlerPlayServer = (ServerGamePacketListenerImpl) netHandler;
                return netHandlerPlayServer.player;
            }
            return null;
        }

        public ClientConnection getNetworkManager() {
            return networkManager;
        }
    }

    /**
     * Dispatcher for sending packets in response to a received packet. Abstracts out the difference between wrapped packets
     * and unwrapped packets.
     */
    public static class PacketDispatcher {
        BiConsumer<Identifier, PacketByteBuf> packetSink;

        PacketDispatcher(final BiConsumer<Identifier, PacketByteBuf> packetSink) {
            this.packetSink = packetSink;
        }

        private PacketDispatcher() {

        }

        public void sendPacket(Identifier Identifier, PacketByteBuf buffer) {
            packetSink.accept(Identifier, buffer);
        }

        static class NetworkManagerDispatcher extends PacketDispatcher
        {
            private final ClientConnection manager;
            private final int packetIndex;
            private final BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier;

            NetworkManagerDispatcher(ClientConnection manager, int packetIndex, BiFunction<Pair<PacketByteBuf, Integer>, Identifier, ICustomPacket<?>> customPacketSupplier) {
                super();
                this.packetSink = this::dispatchPacket;
                this.manager = manager;
                this.packetIndex = packetIndex;
                this.customPacketSupplier = customPacketSupplier;
            }

            private void dispatchPacket(final Identifier Identifier, final PacketByteBuf buffer) {
                final ICustomPacket<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), Identifier);
                this.manager.send(packet.getThis());
            }
        }
    }
}
