package me.sshcrack.fairylights.server.feature;

import com.mojang.serialization.Lifecycle;
import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class FeatureType {
    private static final DefaultedRegistry<FeatureType> REGISTRY = new DefaultedRegistry<>(
        "default",
        RegistryKey.ofRegistry(new Identifier(FairyLightsMod.ModID, "feature")),
        Lifecycle.experimental(),
        null
    );

    public static final FeatureType DEFAULT = register("default");

    private FeatureType() {}

    public int getId() {
        return REGISTRY.getRawId(this);
    }

    public static FeatureType register(final String name) {
        return Registry.register(REGISTRY, new Identifier(name), new FeatureType());
    }

    public static FeatureType fromId(final int id) {
        return REGISTRY.get(id);
    }
}
