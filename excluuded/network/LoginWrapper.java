/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import io.netty.buffer.Unpooled;
import me.sshcrack.fairylights.util.forge.network.event.EventNetworkChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

/**
 * Wrapper for custom login packets. Transforms unnamed login channel messages into channels dispatched the same
 * as regular custom packets.
 */
public class LoginWrapper
{
    private static final Logger LOGGER = LogManager.getLogger();
    @ApiStatus.Internal
    public static final Identifier WRAPPER = new Identifier("fml:loginwrapper");
    private EventNetworkChannel wrapperChannel;

    LoginWrapper() {
        wrapperChannel = NetworkRegistry.ChannelBuilder.named(LoginWrapper.WRAPPER).
                clientAcceptedVersions(a->true).
                serverAcceptedVersions(a->true).
                networkProtocolVersion(()-> NetworkConstants.NETVERSION)
                .eventNetworkChannel();
        wrapperChannel.addListener(this::wrapperReceived);
    }

    private <T extends NetworkEvent> void wrapperReceived(final T packet) {
        // we don't care about channel registration change events on this channel
        if (packet instanceof NetworkEvent.ChannelRegistrationChangeEvent) return;
        final NetworkEvent.Context wrappedContext = packet.getSource().get();
        final PacketByteBuf payload = packet.getPayload();
        Identifier targetNetworkReceiver = NetworkConstants.FML_HANDSHAKE_RESOURCE;
        PacketByteBuf data = null;
        if (payload != null) {
            targetNetworkReceiver = payload.readIdentifier();
            final int payloadLength = payload.readVarInt();
            data = new PacketByteBuf(payload.readBytes(payloadLength));
        }
        final int loginSequence = packet.getLoginIndex();
        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", targetNetworkReceiver, loginSequence);
        final NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), (rl, buf) -> {
            LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, loginSequence);
            wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket(rl, buf));
        });
        final NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> context, loginSequence);
        NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent(ni -> {
            ni.dispatchLoginPacket(loginPayloadEvent);
            wrappedContext.setPacketHandled(context.getPacketHandled());
        });
    }

    private PacketByteBuf wrapPacket(final Identifier rl, final PacketByteBuf buf) {
        PacketByteBuf pb = new PacketByteBuf(Unpooled.buffer(buf.capacity()));
        pb.writeIdentifier(rl);
        pb.writeVarInt(buf.readableBytes());
        pb.writeBytes(buf);
        return pb;
    }

    void sendServerToClientLoginPacket(final Identifier Identifier, final PacketByteBuf buffer, final int index, final ClientConnection manager) {
        PacketByteBuf pb = wrapPacket(Identifier, buffer);
        manager.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(pb, index), WRAPPER).getThis());
    }
}
