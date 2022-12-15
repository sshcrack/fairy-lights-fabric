package me.sshcrack.fairylights.server.capability;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import net.minecraft.util.Identifier;

public final class CapabilityHandler {
    private CapabilityHandler() {}

    public static final Identifier FASTENER_ID = new Identifier(FairyLightsMod.ModID, "fastener");

    public static final Capability<Fastener<?>> FASTENER_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register() {
    }
}
