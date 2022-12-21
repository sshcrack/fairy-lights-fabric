package me.sshcrack.fairylights.server.event;

import me.sshcrack.fairylights.server.jingle.JingleManager;
import me.sshcrack.fairylights.util.forge.events.AddReloadListenerEvent;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;

public class ServerEventHandler {
    @SubscribeEvent
    public void onReloadEvent(AddReloadListenerEvent event) {
            event.addListener(JingleManager.INSTANCE);
    }
}
