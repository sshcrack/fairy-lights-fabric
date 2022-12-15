package me.sshcrack.fairylights.server.net;

import me.sshcrack.fairylights.util.forge.network.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageContext {
    protected final NetworkEvent.Context context;

    public MessageContext(final NetworkEvent.Context context) {
        this.context = context;
    }

    public abstract LogicalSide getSide();
}
