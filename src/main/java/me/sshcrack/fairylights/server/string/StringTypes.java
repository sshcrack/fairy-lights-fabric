package me.sshcrack.fairylights.server.string;

import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class StringTypes {
    private StringTypes() {}

    private static StringType register(String name, Supplier<StringType> type) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);

        return Registry.register(FairyLightsMod.STRING_TYPE, id, type.get());
    }

    public static final StringType BLACK_STRING = register("black_string", () -> new StringType(0x323232));

    public static final StringType WHITE_STRING = register("white_string", () -> new StringType(0xF0F0F0));
}
