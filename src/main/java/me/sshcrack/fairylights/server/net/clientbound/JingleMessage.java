package me.sshcrack.fairylights.server.net.clientbound;

import me.sshcrack.fairylights.server.connection.HangingLightsConnection;
import me.sshcrack.fairylights.server.jingle.Jingle;
import me.sshcrack.fairylights.server.net.ConnectionMessage;
import me.sshcrack.fairylights.server.net_fabric.GeneralClientHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.function.BiConsumer;

public final class JingleMessage extends ConnectionMessage {
    private int lightOffset;

    public Jingle jingle;

    public JingleMessage() {}

    public JingleMessage(final HangingLightsConnection connection, final int lightOffset, final Jingle jingle) {
        super(connection);
        this.lightOffset = lightOffset;
        this.jingle = jingle;
    }

    @Override
    public void encode(final PacketByteBuf buf) {
        super.encode(buf);
        buf.writeVarInt(this.lightOffset);
        this.jingle.write(buf);
    }

    @Override
    public void decode(final PacketByteBuf buf) {
        super.decode(buf);
        this.lightOffset = buf.readVarInt();
        this.jingle = Jingle.read(buf);
    }

    public static class Handler implements GeneralClientHandler<JingleMessage> {
        @Override
        public void accept(final JingleMessage message, final ClientPlayNetworkHandler context) {
            final Jingle jingle = message.jingle;
            if (jingle != null) {
                ConnectionMessage.<HangingLightsConnection>getConnection(message, c -> c instanceof HangingLightsConnection, MinecraftClient.getInstance().world).ifPresent(connection ->
                    connection.play(jingle, message.lightOffset));
            }
        }
    }
}
