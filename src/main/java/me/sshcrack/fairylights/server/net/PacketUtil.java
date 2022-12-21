package me.sshcrack.fairylights.server.net;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class PacketUtil {
    public static PacketByteBuf msgToBuf(Message msg) {
        PacketByteBuf buf = PacketByteBufs.create();
        msg.encode(buf);

        return buf;
    }
}
