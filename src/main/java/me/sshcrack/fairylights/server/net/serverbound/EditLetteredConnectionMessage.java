package me.sshcrack.fairylights.server.net.serverbound;

import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.Lettered;
import me.sshcrack.fairylights.server.net.ConnectionMessage;
import me.sshcrack.fairylights.server.net_fabric.GeneralServerHandler;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class EditLetteredConnectionMessage<C extends Connection & Lettered> extends ConnectionMessage {
    private StyledString text;

    public EditLetteredConnectionMessage() {}

    public EditLetteredConnectionMessage(final C connection, final StyledString text) {
        super(connection);
        this.text = text;
    }

    @Override
    public void encode(final PacketByteBuf buf) {
        super.encode(buf);
        buf.writeNbt(StyledString.serialize(this.text));
    }

    @Override
    public void decode(final PacketByteBuf buf) {
        super.decode(buf);
        this.text = StyledString.deserialize(Objects.requireNonNull(buf.readNbt()));
    }

    public static final class Handler implements GeneralServerHandler<EditLetteredConnectionMessage<?>> {
        @Override
        public void accept(final EditLetteredConnectionMessage<?> message, final ServerPlayNetworkHandler context) {
            final ServerPlayerEntity player = context.getPlayer();
            this.accept(message, player);
        }

        private <C extends Connection & Lettered> void accept(final EditLetteredConnectionMessage<C> message, final ServerPlayerEntity player) {
            if (player != null) {
                ConnectionMessage.<C>getConnection(message, c -> c instanceof Lettered, player.world).ifPresent(connection -> {
                    if (connection.isModifiable(player) && connection.isSupportedText(message.text)) {
                        connection.setText(message.text);
                    }
                });
            }
        }
    }
}
