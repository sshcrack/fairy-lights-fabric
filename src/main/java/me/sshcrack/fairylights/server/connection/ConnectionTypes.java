package me.sshcrack.fairylights.server.connection;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.item.FLItems;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class ConnectionTypes {
    private ConnectionTypes() {}

    public static final ConnectionType<HangingLightsConnection> HANGING_LIGHTS = register("hanging_lights",
        () -> ConnectionType.Builder.create(HangingLightsConnection::new).item(FLItems.HANGING_LIGHTS.get()).build()
    );

    public static final ConnectionType<GarlandVineConnection> VINE_GARLAND = register("vine_garland",
        () -> ConnectionType.Builder.create(GarlandVineConnection::new).item(FLItems.GARLAND.get()).build()
    );

    public static final ConnectionType<GarlandTinselConnection> TINSEL_GARLAND = register("tinsel_garland",
        () -> ConnectionType.Builder.create(GarlandTinselConnection::new).item(FLItems.TINSEL.get()).build()
    );

    public static final ConnectionType<PennantBuntingConnection> PENNANT_BUNTING = register("pennant_bunting",
        () -> ConnectionType.Builder.create(PennantBuntingConnection::new).item(FLItems.PENNANT_BUNTING.get()).build()
    );

    public static final ConnectionType<LetterBuntingConnection> LETTER_BUNTING = register("letter_bunting",
        () -> ConnectionType.Builder.create(LetterBuntingConnection::new).item(FLItems.LETTER_BUNTING.get()).build()
    );

    public static <T extends Connection> ConnectionType<T> register(String name, Supplier<ConnectionType<T>> supplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return Registry.register(FairyLightsMod.CONNECTION_TYPES, id, supplier.get());
    }
}
