package me.sshcrack.fairylights;

import me.sshcrack.fairylights.client.ClientProxy;
import me.sshcrack.fairylights.server.ServerProxy;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.connection.ConnectionType;
import me.sshcrack.fairylights.server.item.FLItems;
import me.sshcrack.fairylights.server.string.StringType;
import me.sshcrack.fairylights.util.CalendarEvent;
import me.sshcrack.fairylights.util.forge.events.EventBus;
import me.sshcrack.fairylights.util.forge.fml.DistExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Month;

public class FairyLightsMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("FairyLights");
    public static final String ModID = "fairylights";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder
            .create(new Identifier(ModID, "main_group"))
            .icon(() -> new ItemStack(FLItems.HANGING_LIGHTS.get()))
            .build();

    public static final Identifier STRING_TYPE_ID = new Identifier(ModID, "string_type");
    public static final Identifier CONNECTION_TYPES_ID = new Identifier(ModID, "connection_types");
    public static final SimpleRegistry<StringType> STRING_TYPE = FabricRegistryBuilder.createSimple(StringType.class, FairyLightsMod.STRING_TYPE_ID).buildAndRegister();
    @SuppressWarnings("rawtypes")
    public static final SimpleRegistry<ConnectionType> CONNECTION_TYPES = FabricRegistryBuilder.createSimple(ConnectionType.class, FairyLightsMod.CONNECTION_TYPES_ID).buildAndRegister();
    public static final EventBus EVENT_BUS = new EventBus();

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);
    public static final CalendarEvent HALLOWEEN = new CalendarEvent(Month.OCTOBER, 31, 31);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        CapabilityHandler.register();
        final ServerProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        proxy.init();
    }
}
