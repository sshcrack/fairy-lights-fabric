/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import me.sshcrack.fairylights.mixin.ClientConnectionMixin;
import me.sshcrack.fairylights.mixin.IClientConnectionMixin;
import me.sshcrack.fairylights.util.forge.network.protocol.ClientIntentionPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Maps;

import io.netty.channel.Channel;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Instance responsible for handling the overall FML impl handshake.
 *
 * <p>An instance is created during {@link ClientIntentionPacket} handling, and attached
 *
 *
 *
 * utilizing the {@code ServerLoginPacketListenerImpl.State#NEGOTIATING} state, which is otherwise unused in vanilla code.
 *
 * <p>During client to server initiation, on the <em>server</em>, the {@link NetworkEvent.GatherLoginPayloadsEvent} is fired,
 * which solicits all registered channels at the {@link NetworkRegistry} for any
 * {@link NetworkRegistry.LoginPayload} they wish to supply.
 *
 * <p>The collected {@link NetworkRegistry.LoginPayload} are sent, one per tick, via
 * the {@code FMLLoginWrapper#wrapPacket(Identifier, net.minecraft.impl.PacketByteBuf)} mechanism to the incoming client connection. Each
 * the only mechanism available for tracking request/response pairs.
 *
 * <p>Each packet sent from the server should be replied by the client, though not necessarily in sent order. The reply
 * replies correctly automatically.
 *
 * <p>Once all packets have been dispatched, we wait for all replies to be received. Once all replies are received, the
 * final login phase will commence.
 */
public class HandshakeHandler
{
    static final Marker FMLHSMARKER = MarkerManager.getMarker("FMLHANDSHAKE").setParents(NetworkConstants.NETWORK);
    private static final Logger LOGGER = LogManager.getLogger();

    private static final LoginWrapper loginWrapper = new LoginWrapper();

    static {
    }

    private static Channel getChannel(ClientConnection manager) {
        return ((IClientConnectionMixin) manager).getChannel();
    }

    /**
     * Create a new handshake instance. Called when connection is first created during the {@link ClientIntentionPacket}
     * handling.
     *
     * @param manager The impl manager for this connection
     * @param direction The {@link NetworkDirection} for this connection: {@link NetworkDirection#LOGIN_TO_SERVER} or {@link NetworkDirection#LOGIN_TO_CLIENT}
     */
    static void registerHandshake(ClientConnection manager, NetworkDirection direction) {
        getChannel(manager).attr(NetworkConstants.FML_HANDSHAKE_HANDLER).compareAndSet(null, new HandshakeHandler(manager, direction));
    }

    static boolean tickLogin(ClientConnection networkManager)
    {
        return getChannel(networkManager).attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get().tickServer();
    }

    private List<NetworkRegistry.LoginPayload> messageList;

    private List<Integer> sentMessages = new ArrayList<>();

    private final NetworkDirection direction;
    private final ClientConnection manager;
    private int packetPosition;
    private boolean negotiationStarted = false;
    private final List<Future<Void>> pendingFutures = new ArrayList<>();

    private HandshakeHandler(ClientConnection networkManager, NetworkDirection side)
    {
        this.direction = side;
        this.manager = networkManager;
        if (networkManager.isLocal()) {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, true);
            LOGGER.debug(FMLHSMARKER, "Starting local connection.");
        } else if (NetworkHooks.getConnectionType(()->this.manager)== ConnectionType.VANILLA) {
            this.messageList = Collections.emptyList();
            LOGGER.debug(FMLHSMARKER, "Starting new vanilla impl connection.");
        } else {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, false);
            LOGGER.debug(FMLHSMARKER, "Starting new modded impl connection. Found {} messages to dispatch.", this.messageList.size());
        }
    }

    @FunctionalInterface
    public interface HandshakeConsumer<MSG extends IntSupplier>
    {
        void accept(HandshakeHandler handler, MSG msg, Supplier<NetworkEvent.Context> context);
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} based on the {@link #getHandshake(Supplier)} function.
     *
     * This should only be used for login message types.
     *
     * @param consumer A two argument instance method reference
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(HandshakeConsumer<MSG> consumer)
    {
        return (m, c) -> consumer.accept(getHandshake(c), m, c);
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} {@link #biConsumerFor(HandshakeConsumer)}, first calling the {@link #handleIndexedMessage(IntSupplier, Supplier)}
     * method to handle index tracking. Used for client to server replies.
     *
     * This should only be used for login messages.
     *
     * @param next The method reference to call after index handling
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(HandshakeConsumer<MSG> next)
    {
        final BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginIndexedMessageSupplierBiConsumer = biConsumerFor(HandshakeHandler::handleIndexedMessage);
        return loginIndexedMessageSupplierBiConsumer.andThen(biConsumerFor(next));
    }

    /**
     * Retrieve the handshake from the {@link NetworkEvent.Context}
     *
     * @param contextSupplier the {@link NetworkEvent.Context}
     * @return The handshake handler for the connection
     */
    private static HandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        return contextSupplier.get().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
    }


    <MSG extends IntSupplier> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", message.getAsInt(), message.getClass().getName());
        boolean removed = this.sentMessages.removeIf(i-> i == message.getAsInt());
        if (!removed) {
            LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", message.getAsInt());
        }
    }

    /**
     * FML will send packets, from Server to Client, from the messages queue until the queue is drained. Each message
     * will be indexed, and placed into the "pending acknowledgement" queue.
     *
     * As indexed packets are received at the server, they will be removed from the "pending acknowledgement" queue.
     *
     * Once the pending queue is drained, this method returns true - indicating that login processing can proceed to
     * the next step.
     *
     * @return true if there is no more need to tick this login connection.
     */
    public boolean tickServer()
    {
        if (!negotiationStarted) {
            negotiationStarted = true;
        }

        if (packetPosition < messageList.size()) {
            NetworkRegistry.LoginPayload message = messageList.get(packetPosition);

            LOGGER.debug(FMLHSMARKER, "Sending ticking packet info '{}' to '{}' sequence {}", message.getMessageContext(), message.getChannelName(), packetPosition);
            if (message.needsResponse())
                sentMessages.add(packetPosition);
            loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), packetPosition, this.manager);
            packetPosition++;
        }

        pendingFutures.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }

            try {
                future.get();
            } catch (ExecutionException ex) {
                LOGGER.error("Error during negotiation", ex.getCause());
            } catch (CancellationException | InterruptedException ex) {
                // no-op
            }

            return true;
        });

        // we're done when sentMessages is empty
        if (sentMessages.isEmpty() && packetPosition >= messageList.size()-1 && pendingFutures.isEmpty()) {
            // clear ourselves - we're done!

            HandshakeHandler.getChannel(this.manager).attr(NetworkConstants.FML_HANDSHAKE_HANDLER).set(null);
            LOGGER.debug(FMLHSMARKER, "Handshake complete!");
            return true;
        }
        return false;
    }

    /**
     * Helper method to determine if the S2C packet at the given packet position needs a response in form of a packet handled in {@link HandshakeHandler#handleIndexedMessage} for the handshake to progress.
     * @param mgr The impl manager for this connection
     * @param packetPosition The packet position of the packet that the status is queried of
     * @return true if the packet at the given packet position needs a response and thus may stop the handshake from progressing
     */
    public static boolean packetNeedsResponse(ClientConnection mgr, int packetPosition)
    {
        HandshakeHandler handler = HandshakeHandler.getChannel(mgr).attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
        if (handler != null)
        {
            return handler.sentMessages.contains(packetPosition);
        }
        return false;
    }
}
