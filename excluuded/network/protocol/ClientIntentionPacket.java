package me.sshcrack.fairylights.util.forge.network.protocol;

import me.sshcrack.fairylights.util.forge.network.NetworkConstants;
import me.sshcrack.fairylights.util.forge.network.NetworkHooks;
import me.sshcrack.fairylights.util.forge.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

public class ClientIntentionPacket implements Packet<ServerHandshakePacketListener> {
   private static final int MAX_HOST_LENGTH = 255;
   private final int protocolVersion;
   private final String hostName;
   private final int port;
   private final NetworkState intention;
   private String fmlVersion = NetworkConstants.NETVERSION;

   public ClientIntentionPacket(String p_134726_, int p_134727_, NetworkState p_134728_) {
      this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
      this.hostName = p_134726_;
      this.port = p_134727_;
      this.intention = p_134728_;
   }

   public ClientIntentionPacket(PacketByteBuf p_179801_) {
      this.protocolVersion = p_179801_.readVarInt();
      String hostName = p_179801_.readString(255);
      this.port = p_179801_.readUnsignedShort();
      this.intention = NetworkState.byId(p_179801_.readVarInt());
      this.fmlVersion = NetworkHooks.getFMLVersion(hostName);
      this.hostName = hostName.split("\0")[0];
   }

   public void write(PacketByteBuf p_134737_) {
      p_134737_.writeVarInt(this.protocolVersion);
      p_134737_.writeString(this.hostName + "\0"+ NetworkConstants.NETVERSION+"\0");
      p_134737_.writeShort(this.port);
      p_134737_.writeVarInt(this.intention.getId());
   }

   public void apply(ServerHandshakePacketListener p_134734_) {
      p_134734_.handleIntention(this);
   }

   public NetworkState getIntention() {
      return this.intention;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getHostName() {
      return this.hostName;
   }

   public int getPort() {
      return this.port;
   }

   public String getFMLVersion() {
      return this.fmlVersion;
   }
}
