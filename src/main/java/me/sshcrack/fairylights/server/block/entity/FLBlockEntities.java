package me.sshcrack.fairylights.server.block.entity;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.block.FLBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class FLBlockEntities {
    private FLBlockEntities() {}

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> typeSupplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return () -> Registry.register(Registry.BLOCK_ENTITY_TYPE, id, typeSupplier.get());
    }

    public static final Supplier<BlockEntityType<FastenerBlockEntity>> FASTENER = register("fastener", () -> BlockEntityType.Builder.create(FastenerBlockEntity::new, FLBlocks.FASTENER.get()).build(null));

    public static final Supplier<BlockEntityType<LightBlockEntity>> LIGHT = register("light", () -> BlockEntityType.Builder.create(LightBlockEntity::new,
        FLBlocks.FAIRY_LIGHT.get(),
        FLBlocks.PAPER_LANTERN.get(),
        FLBlocks.ORB_LANTERN.get(),
        FLBlocks.FLOWER_LIGHT.get(),
        FLBlocks.CANDLE_LANTERN_LIGHT.get(),
        FLBlocks.OIL_LANTERN_LIGHT.get(),
        FLBlocks.JACK_O_LANTERN.get(),
        FLBlocks.SKULL_LIGHT.get(),
        FLBlocks.GHOST_LIGHT.get(),
        FLBlocks.SPIDER_LIGHT.get(),
        FLBlocks.WITCH_LIGHT.get(),
        FLBlocks.SNOWFLAKE_LIGHT.get(),
        FLBlocks.HEART_LIGHT.get(),
        FLBlocks.MOON_LIGHT.get(),
        FLBlocks.STAR_LIGHT.get(),
        FLBlocks.ICICLE_LIGHTS.get(),
        FLBlocks.METEOR_LIGHT.get(),
        FLBlocks.OIL_LANTERN.get(),
        FLBlocks.CANDLE_LANTERN.get(),
        FLBlocks.INCANDESCENT_LIGHT.get()
    ).build(null));
}
