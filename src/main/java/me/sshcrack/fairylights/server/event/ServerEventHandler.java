package me.sshcrack.fairylights.server.event;

import me.sshcrack.fairylights.server.ServerProxy;
import me.sshcrack.fairylights.server.block.entity.FastenerBlockEntity;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import me.sshcrack.fairylights.server.fastener.BlockFastener;
import me.sshcrack.fairylights.server.fastener.FenceFastener;
import me.sshcrack.fairylights.server.fastener.PlayerFastener;
import me.sshcrack.fairylights.server.jingle.JingleManager;
import me.sshcrack.fairylights.util.forge.events.AddReloadListenerEvent;
import me.sshcrack.fairylights.util.forge.events.AttachCapabilitiesEvent;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ServerEventHandler {
    @SubscribeEvent
    public void onReloadEvent(AddReloadListenerEvent event) {
        event.addListener(JingleManager.INSTANCE);
    }

    @SubscribeEvent
    public void onAttachEntityCapability(final AttachCapabilitiesEvent<?> event) {
        final Object entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new PlayerFastener((PlayerEntity) entity));
        } else if (entity instanceof FenceFastenerEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new FenceFastener((FenceFastenerEntity) entity));
        }
        if (entity instanceof FastenerBlockEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new BlockFastener((FastenerBlockEntity) entity, ServerProxy.buildBlockView()));
        }
    }
}
