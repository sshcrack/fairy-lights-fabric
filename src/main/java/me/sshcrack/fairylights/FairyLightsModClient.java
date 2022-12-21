package me.sshcrack.fairylights;

import me.sshcrack.fairylights.client.ClientEventHandler;
import me.sshcrack.fairylights.server.net.Message;
import me.sshcrack.fairylights.server.net.PacketList;
import me.sshcrack.fairylights.server.net_fabric.ClientTriplet;
import me.sshcrack.fairylights.server.net_fabric.GeneralClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class FairyLightsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FairyLightsMod.EVENT_BUS.registerEventHandler(new ClientEventHandler());
        this.listenForPackets();
    }

    //TODO
    @SuppressWarnings("unchecked")
    public void listenForPackets() {
        for (ClientTriplet<? extends Message, ?> msgPair : PacketList.CLIENT_PACKETS) {
            Identifier id = PacketList.getId(msgPair);
            ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
                GeneralClientHandler<Message> clientHandler = (GeneralClientHandler<Message>) msgPair.getC().get();

                clientHandler.accept(msgPair.getB().get(), handler);
            });
        }
    }
}
