package me.sshcrack.fairylights;

import me.sshcrack.fairylights.server.net.Message;
import me.sshcrack.fairylights.server.net.PacketList;
import me.sshcrack.fairylights.server.net_fabric.GeneralServerHandler;
import me.sshcrack.fairylights.server.net_fabric.ServerTriplet;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class FairyLightsModServer implements DedicatedServerModInitializer {

    //TODO
    @SuppressWarnings("unchecked")
    @Override
    public void onInitializeServer() {
        for (ServerTriplet<? extends Message, ?> msgPair : PacketList.SERVER_PACKETS) {
            Identifier id = PacketList.getId(msgPair);
            ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, responseSender) -> {
                GeneralServerHandler<Message> serverHandler = (GeneralServerHandler<Message>) msgPair.getC().get();

                serverHandler.accept(msgPair.getB().get(), handler);
            });
        }
    }
}
