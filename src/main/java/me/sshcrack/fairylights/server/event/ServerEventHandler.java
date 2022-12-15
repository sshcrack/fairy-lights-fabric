package me.sshcrack.fairylights.server.event;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.events.AddReloadListenerEvent;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;

public class ServerEventHandler {
    @SubscribeEvent
    public void onReloadEvent(AddReloadListenerEvent event) {
            e.addListener(JingleManager.INSTANCE);
    }
}
