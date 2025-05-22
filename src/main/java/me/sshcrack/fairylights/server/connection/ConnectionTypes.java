package me.sshcrack.fairylights.server.connection;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.item.FLItems;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class ConnectionTypes {
    private static final ArrayList<Runnable> registerFuncs = new ArrayList<>();
    private ConnectionTypes() {}

    public static void initialize() {
        for (Runnable registerFunc : registerFuncs) {
            registerFunc.run();
        }
    }

    public static final Supplier<ConnectionType<HangingLightsConnection>> HANGING_LIGHTS = register("hanging_lights",
        () -> ConnectionType.Builder.create(HangingLightsConnection::new).item(FLItems.HANGING_LIGHTS).build()
    );

    public static final Supplier<ConnectionType<GarlandVineConnection>> VINE_GARLAND = register("vine_garland",
        () -> ConnectionType.Builder.create(GarlandVineConnection::new).item(FLItems.GARLAND).build()
    );

    public static final Supplier<ConnectionType<GarlandTinselConnection>> TINSEL_GARLAND = register("tinsel_garland",
        () -> ConnectionType.Builder.create(GarlandTinselConnection::new).item(FLItems.TINSEL).build()
    );

    public static final Supplier<ConnectionType<PennantBuntingConnection>> PENNANT_BUNTING = register("pennant_bunting",
        () -> ConnectionType.Builder.create(PennantBuntingConnection::new).item(FLItems.PENNANT_BUNTING).build()
    );

    public static final Supplier<ConnectionType<LetterBuntingConnection>> LETTER_BUNTING = register("letter_bunting",
        () -> ConnectionType.Builder.create(LetterBuntingConnection::new).item(FLItems.LETTER_BUNTING).build()
    );

    public static <T extends Connection> Supplier<ConnectionType<T>> register(String name, Supplier<ConnectionType<T>> supplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        AtomicReference<ConnectionType<T>> reference = new AtomicReference<>(null);

        registerFuncs.add(() -> reference.set(Registry.register(FairyLightsMod.CONNECTION_TYPES, id, supplier.get())));
        return () -> reference.get();
    }
}
