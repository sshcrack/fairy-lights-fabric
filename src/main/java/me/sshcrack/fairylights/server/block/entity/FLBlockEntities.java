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

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<BlockEntityType<T>> typeSupplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, typeSupplier.get());
    }

    public static final BlockEntityType<FastenerBlockEntity> FASTENER = register("fastener", () -> BlockEntityType.Builder.create(FastenerBlockEntity::new, FLBlocks.FASTENER).build(null));

    public static final BlockEntityType<LightBlockEntity> LIGHT = register("light", () -> BlockEntityType.Builder.create(LightBlockEntity::new,
        FLBlocks.FAIRY_LIGHT,
        FLBlocks.PAPER_LANTERN,
        FLBlocks.ORB_LANTERN,
        FLBlocks.FLOWER_LIGHT,
        FLBlocks.CANDLE_LANTERN_LIGHT,
        FLBlocks.OIL_LANTERN_LIGHT,
        FLBlocks.JACK_O_LANTERN,
        FLBlocks.SKULL_LIGHT,
        FLBlocks.GHOST_LIGHT,
        FLBlocks.SPIDER_LIGHT,
        FLBlocks.WITCH_LIGHT,
        FLBlocks.SNOWFLAKE_LIGHT,
        FLBlocks.HEART_LIGHT,
        FLBlocks.MOON_LIGHT,
        FLBlocks.STAR_LIGHT,
        FLBlocks.ICICLE_LIGHTS,
        FLBlocks.METEOR_LIGHT,
        FLBlocks.OIL_LANTERN,
        FLBlocks.CANDLE_LANTERN,
        FLBlocks.INCANDESCENT_LIGHT
    ).build(null));
}
