package me.sshcrack.fairylights.server.connection;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ConnectionTypes {
    private ConnectionTypes() {}

    public static final DeferredRegister<ConnectionType<?>> REG = DeferredRegister.create(FairyLights.CONNECTION_TYPE, FairyLights.ID);

    public static final RegistryObject<ConnectionType<me.paulf.fairylights.server.connection.HangingLightsConnection>> HANGING_LIGHTS = REG.register("hanging_lights",
        () -> ConnectionType.Builder.create(me.paulf.fairylights.server.connection.HangingLightsConnection::new).item(FLItems.HANGING_LIGHTS).build()
    );

    public static final RegistryObject<ConnectionType<GarlandVineConnection>> VINE_GARLAND = REG.register("vine_garland",
        () -> ConnectionType.Builder.create(GarlandVineConnection::new).item(FLItems.GARLAND).build()
    );

    public static final RegistryObject<ConnectionType<GarlandTinselConnection>> TINSEL_GARLAND = REG.register("tinsel_garland",
        () -> ConnectionType.Builder.create(GarlandTinselConnection::new).item(FLItems.TINSEL).build()
    );

    public static final RegistryObject<ConnectionType<PennantBuntingConnection>> PENNANT_BUNTING = REG.register("pennant_bunting",
        () -> ConnectionType.Builder.create(PennantBuntingConnection::new).item(FLItems.PENNANT_BUNTING).build()
    );

    public static final RegistryObject<ConnectionType<me.paulf.fairylights.server.connection.LetterBuntingConnection>> LETTER_BUNTING = REG.register("letter_bunting",
        () -> ConnectionType.Builder.create(me.paulf.fairylights.server.connection.LetterBuntingConnection::new).item(FLItems.LETTER_BUNTING).build()
    );
}
