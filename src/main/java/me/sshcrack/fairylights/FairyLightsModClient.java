package me.sshcrack.fairylights;

import me.sshcrack.fairylights.client.ClientEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class FairyLightsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FairyLightsMod.EVENT_BUS.registerEventHandler(new ClientEventHandler());
    }
}
