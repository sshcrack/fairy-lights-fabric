package me.sshcrack.fairylights.server.capability;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.sshcrack.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityManager;
import net.minecraft.util.Identifier;

public final class CapabilityHandler {
    private CapabilityHandler() {}

    public static final Identifier FASTENER_ID = new Identifier(FairyLightsMod.ModID, "fastener");
    public static final Capability<Fastener<?>> FASTENER_CAP = CapabilityManager.of(FASTENER_ID, Fastener.class);

    public static void register() {
        new BlockFastenerAccessor();
    }
}
