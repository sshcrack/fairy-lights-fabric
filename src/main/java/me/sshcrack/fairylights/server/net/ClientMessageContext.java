package me.sshcrack.fairylights.server.net;

import me.sshcrack.fairylights.util.forge.network.LogicalSide;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class ClientMessageContext extends MessageContext {
    public ClientMessageContext(final NetworkEvent.Context context) {
        super(context);
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    public ClientWorld getWorld() {
        return Objects.requireNonNull(this.getMinecraft().world);
    }

    public PlayerEntity getPlayer() {
        return Objects.requireNonNull(this.context.getSender());
    }
}
