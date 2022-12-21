
package me.sshcrack.fairylights.util.forge.network.protocol.handshake;

import me.sshcrack.fairylights.util.forge.network.protocol.ClientIntentionPacket;
import net.minecraft.network.listener.ServerPacketListener;

public interface ServerHandshakePacketListener extends ServerPacketListener {
   void handleIntention(ClientIntentionPacket p_134739_);
}