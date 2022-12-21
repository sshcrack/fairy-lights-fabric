/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.util.Attribute;
import me.sshcrack.fairylights.mixin.ClientConnectionMixin;
import me.sshcrack.fairylights.mixin.IClientConnectionMixin;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class MCRegisterPacketHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MCRegisterPacketHandler INSTANCE = new MCRegisterPacketHandler();

    public static class ChannelList {
        private Set<Identifier> locations = new HashSet<>();
        private Set<Identifier> remoteLocations = Set.of();

        public void updateFrom(final Supplier<NetworkEvent.Context> source, PacketByteBuf buffer, final NetworkEvent.RegistrationChangeType changeType) {
            byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
            buffer.readBytes(data);
            Set<Identifier> oldLocations = this.locations;
            this.locations = bytesToResLocation(data);
            this.remoteLocations = Set.copyOf(this.locations);
            // ensure all locations receive updates, old and new.
            oldLocations.addAll(this.locations);
            oldLocations.stream()
                    .map(NetworkRegistry::findTarget)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(t->t.dispatchEvent(new NetworkEvent.ChannelRegistrationChangeEvent(source, changeType)));
        }

        byte[] toByteArray() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (Identifier rl : locations) {
                try {
                    bos.write(rl.toString().getBytes(StandardCharsets.UTF_8));
                    bos.write(0);
                } catch (IOException e) {
                    // fake IOE
                }
            }
            return bos.toByteArray();
        }

        private Set<Identifier> bytesToResLocation(byte[] all) {
            HashSet<Identifier> rl = new HashSet<>();
            int last = 0;
            for (int cur = 0; cur < all.length; cur++) {
                if (all[cur] == '\0') {
                    String s = new String(all, last, cur - last, StandardCharsets.UTF_8);
                    try {
                        rl.add(new Identifier(s));
                    } catch (InvalidIdentifierException ex) {
                        LOGGER.warn("Invalid channel name received: {}. Ignoring", s);
                    }
                    last = cur + 1;
                }
            }
            return rl;
        }

        /**
         * {@return the unmodifiable set of channel locations sent by the remote side}
         * This is useful for interacting with other modloaders via the network to inspect registered network channel IDs.
         */
        public Set<Identifier> getRemoteLocations() {
            return this.remoteLocations;
        }
    }

    public void addChannels(Set<Identifier> locations, ClientConnection manager) {
        getFrom(manager).locations.addAll(locations);
    }

    void registerListener(NetworkEvent evt) {
        final ChannelList channelList = getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.REGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    void unregisterListener(NetworkEvent evt) {
        final ChannelList channelList = getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.UNREGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    private static ChannelList getFrom(ClientConnection manager) {
        return fromAttr(((IClientConnectionMixin)manager).getChannel().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList getFrom(NetworkEvent event) {
        return fromAttr(event.getSource().get().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList fromAttr(Attribute<ChannelList> attr) {
        attr.setIfAbsent(new ChannelList());
        return attr.get();
    }

    public void sendRegistry(ClientConnection manager, final NetworkDirection dir) {
        PacketByteBuf pb = new PacketByteBuf(Unpooled.buffer());
        pb.writeBytes(getFrom(manager).toByteArray());
        final ICustomPacket<Packet<?>> iPacketICustomPacket = dir.buildPacket(Pair.of(pb, 0), NetworkConstants.MC_REGISTER_RESOURCE);
        manager.send(iPacketICustomPacket.getThis());
    }
}
