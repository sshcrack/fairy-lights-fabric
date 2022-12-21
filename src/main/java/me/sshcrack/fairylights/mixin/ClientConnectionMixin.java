package me.sshcrack.fairylights.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements IClientConnectionMixin {
    @Shadow
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }
}
