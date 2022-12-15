package me.sshcrack.fairylights.server.entity;

import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class FLEntities {
    private FLEntities() {}
    public static <T extends Entity> EntityType<T> register(String name, Supplier<EntityType<T>> entity) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return Registry.register(Registry.ENTITY_TYPE, id, entity.get());
    }

    public static final EntityType<FenceFastenerEntity> FASTENER = register("fastener", () ->
        EntityType.Builder.<FenceFastenerEntity>create(FenceFastenerEntity::new, SpawnGroup.MISC)
            .setDimensions(1.15F, 2.8F)
            .maxTrackingRange(10)
            .trackingTickInterval(Integer.MAX_VALUE)
                //TODO fix fastener here
            //.setShouldReceiveVelocityUpdates(false)
            //.setCustomClientFactory((message, world) -> new FenceFastenerEntity(world))
            .build(FairyLightsMod.ModID + ":fastener")
    );
}
