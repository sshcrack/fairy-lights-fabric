package me.sshcrack.fairylights.server.net;


import net.minecraft.network.PacketByteBuf;

public interface Message {
    void encode(final PacketByteBuf buf);

    void decode(final PacketByteBuf buf);
}
