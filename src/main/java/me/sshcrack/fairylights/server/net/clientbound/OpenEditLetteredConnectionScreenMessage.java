package me.sshcrack.fairylights.server.net.clientbound;

import me.sshcrack.fairylights.client.gui.EditLetteredConnectionScreen;
import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.Lettered;
import me.sshcrack.fairylights.server.net.ConnectionMessage;
import me.sshcrack.fairylights.server.net_fabric.GeneralClientHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class OpenEditLetteredConnectionScreenMessage<C extends Connection & Lettered> extends ConnectionMessage {
    public OpenEditLetteredConnectionScreenMessage() {}

    public OpenEditLetteredConnectionScreenMessage(final C connection) {
        super(connection);
    }

    public static final class Handler implements GeneralClientHandler<OpenEditLetteredConnectionScreenMessage<?>> {
        @Override
        public void accept(final OpenEditLetteredConnectionScreenMessage<?> message, final ClientPlayNetworkHandler context) {
            this.accept(message);
        }

        private <C extends Connection & Lettered> void accept(final OpenEditLetteredConnectionScreenMessage<C> message) {
            ConnectionMessage.<C>getConnection(message, c -> c instanceof Lettered, MinecraftClient.getInstance().world).ifPresent(connection -> {
                MinecraftClient.getInstance().setScreen(new EditLetteredConnectionScreen<>(connection));
            });
        }
    }
}
