package me.sshcrack.fairylights.util.forge.events;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityDispatcher;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class EventFactory {
    @Nullable
    public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
        return gatherCapabilities(type, provider, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
    }

    public static <T>  CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, ICapabilityProvider tObj) {
        FairyLightsMod.EVENT_BUS.fireEvent(event);

        return event.getCapabilities().size() > 0 || tObj != null ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), tObj) : null;

    }
}
