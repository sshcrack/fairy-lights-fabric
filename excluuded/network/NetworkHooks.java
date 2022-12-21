/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.sshcrack.fairylights.mixin.IClientConnectionMixin;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NetworkHooks
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], NetworkConstants.NETVERSION) ? NetworkConstants.NETVERSION : ip.split("\0")[1] : NetworkConstants.NOVERSION;
    }

    public static ConnectionType getConnectionType(final Supplier<ClientConnection> connection)
    {
        return getConnectionType(connection.get().channel());
    }

    public static ConnectionType getConnectionType(ChannelHandlerContext context)
    {
        return getConnectionType(context.channel());
    }

    private static ConnectionType getConnectionType(Channel channel)
    {
        return ConnectionType.forVersionFlag(channel.attr(NetworkConstants.FML_NETVERSION).get());
    }

    private static boolean validateSideForProcessing(final ICustomPacket<?> packet, final NetworkInstance ni, final ClientConnection manager) {
        if (packet.getDirection().getReceptionSide() != EffectiveSide.get()) {
            manager.disconnect(Text.literal("Illegal packet received, terminating connection"));
            return false;
        }
        return true;
    }

    public static void validatePacketDirection(final NetworkDirection packetDirection, final Optional<NetworkDirection> expectedDirection, final ClientConnection connection) {
        if (packetDirection != expectedDirection.orElse(packetDirection)) {
            connection.disconnect(Text.literal("Illegal packet received, terminating connection"));
            throw new IllegalStateException("Invalid packet received, aborting connection");
        }
    }
    public static void registerServerLoginChannel(ClientConnection manager, ClientIntentionPacket packet)
    {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set(packet.getFMLVersion());
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public synchronized static void registerClientLoginChannel(ClientConnection manager)
    {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set(NetworkConstants.NOVERSION);
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public synchronized static void sendMCRegistryPackets(ClientConnection manager, String direction) {
        NetworkFilters.injectIfNecessary(manager);
        final Set<Identifier> Identifiers = NetworkRegistry.buildChannelVersions().keySet().stream().
                filter(rl -> !Objects.equals(rl.getNamespace(), "minecraft")).
                collect(Collectors.toSet());
        MCRegisterPacketHandler.INSTANCE.addChannels(Identifiers, manager);
        MCRegisterPacketHandler.INSTANCE.sendRegistry(manager, NetworkDirection.valueOf(direction));
    }

    //TODO Dimensions..
/*    public synchronized static void sendDimensionDataPacket(NetworkManager manager, ServerPlayerEntity player) {
        // don't send vanilla dims
        if (player.dimension.isVanilla()) return;
        // don't sent to local - we already have a valid dim registry locally
        if (manager.isLocalChannel()) return;
        FMLNetworkConstants.playChannel.sendTo(new FMLPlayMessages.DimensionInfoMessage(player.dimension), manager, NetworkDirection.PLAY_TO_CLIENT);
    }*/

    public static boolean isVanillaConnection(ClientConnection manager)
    {
        if (manager == null || manager.channel() == null) throw new NullPointerException("ARGH! Network Manager is null (" + manager != null ? "CHANNEL" : "MANAGER"+")" );
        return getConnectionType(() -> manager) == ConnectionType.VANILLA;
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     */
    public static void openScreen(ServerPlayerEntity player, MenuProvider containerSupplier)
    {
        openScreen(player, containerSupplier, buf -> {});
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param pos A block pos, which will be encoded into the auxillary data for this request
     */
    public static void openScreen(ServerPlayerEntity player, NamedScreenHandlerFactory containerSupplier, BlockPos pos)
    {
        openScreen(player, containerSupplier, buf -> buf.writeBlockPos(pos));
    }
    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The maximum size for #extraDataWriter is 32600 bytes.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param extraDataWriter Consumer to write any additional data the GUI needs
     */
    public static void openScreen(ServerPlayerEntity player, NamedScreenHandlerFactory containerSupplier, Consumer<PacketByteBuf> extraDataWriter)
    {
        if (player.world.isClient) return;
        player.doCloseContainer();
        player.nextContainerCounter();
        int openContainerId = player.containerCounter;
        PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        PacketByteBuf output = new PacketByteBuf(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        }
        AbstractContainerMenu c = containerSupplier.createMenu(openContainerId, player.getInventory(), player);
        MenuType<?> type = c.getType();
        PlayMessages.OpenContainer msg = new PlayMessages.OpenContainer(type, openContainerId, containerSupplier.getDisplayName(), output);
        NetworkConstants.playChannel.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);

        player.containerMenu = c;
        player.initMenu(player.containerMenu);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }

    @Nullable
    public static ConnectionData getConnectionData(ClientConnection mgr)
    {
        return ((IClientConnectionMixin) mgr).getChannel().attr(NetworkConstants.FML_CONNECTION_DATA).get();
    }

    @Nullable
    public static MCRegisterPacketHandler.ChannelList getChannelList(ClientConnection mgr)
    {
        return ((IClientConnectionMixin) mgr).getChannel().attr(NetworkConstants.FML_MC_REGISTRY).get();
    }
}
