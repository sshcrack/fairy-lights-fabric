package me.sshcrack.fairylights.server.block;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.item.LightVariant;
import me.sshcrack.fairylights.server.item.SimpleLightVariant;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class FLBlocks {
    private FLBlocks() {
    }

    public static <T extends Block> T register(String name, Supplier<Block> blockSupplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        
        return (T) Registry.register(Registry.BLOCK, id, blockSupplier.get());
    }

    public static final FastenerBlock FASTENER = register("fastener", () -> new FastenerBlock(AbstractBlock.Settings.of(Material.DECORATION)));

    public static final LightBlock FAIRY_LIGHT = register("fairy_light", FLBlocks.createLight(SimpleLightVariant.FAIRY_LIGHT));

    public static final LightBlock PAPER_LANTERN = register("paper_lantern", FLBlocks.createLight(SimpleLightVariant.PAPER_LANTERN));

    public static final LightBlock ORB_LANTERN = register("orb_lantern", FLBlocks.createLight(SimpleLightVariant.ORB_LANTERN));

    public static final LightBlock FLOWER_LIGHT = register("flower_light", FLBlocks.createLight(SimpleLightVariant.FLOWER_LIGHT));

    public static final LightBlock CANDLE_LANTERN_LIGHT = register("candle_lantern_light", FLBlocks.createLight(SimpleLightVariant.CANDLE_LANTERN_LIGHT));

    public static final LightBlock OIL_LANTERN_LIGHT = register("oil_lantern_light", FLBlocks.createLight(SimpleLightVariant.OIL_LANTERN_LIGHT));

    public static final LightBlock JACK_O_LANTERN = register("jack_o_lantern", FLBlocks.createLight(SimpleLightVariant.JACK_O_LANTERN));

    public static final LightBlock SKULL_LIGHT = register("skull_light", FLBlocks.createLight(SimpleLightVariant.SKULL_LIGHT));

    public static final LightBlock GHOST_LIGHT = register("ghost_light", FLBlocks.createLight(SimpleLightVariant.GHOST_LIGHT));

    public static final LightBlock SPIDER_LIGHT = register("spider_light", FLBlocks.createLight(SimpleLightVariant.SPIDER_LIGHT));

    public static final LightBlock WITCH_LIGHT = register("witch_light", FLBlocks.createLight(SimpleLightVariant.WITCH_LIGHT));

    public static final LightBlock SNOWFLAKE_LIGHT = register("snowflake_light", FLBlocks.createLight(SimpleLightVariant.SNOWFLAKE_LIGHT));

    public static final LightBlock HEART_LIGHT = register("heart_light", FLBlocks.createLight(SimpleLightVariant.HEART_LIGHT));

    public static final LightBlock MOON_LIGHT = register("moon_light", FLBlocks.createLight(SimpleLightVariant.MOON_LIGHT));

    public static final LightBlock STAR_LIGHT = register("star_light", FLBlocks.createLight(SimpleLightVariant.STAR_LIGHT));

    public static final LightBlock ICICLE_LIGHTS = register("icicle_lights", FLBlocks.createLight(SimpleLightVariant.ICICLE_LIGHTS));

    public static final LightBlock METEOR_LIGHT = register("meteor_light", FLBlocks.createLight(SimpleLightVariant.METEOR_LIGHT));

    public static final LightBlock OIL_LANTERN = register("oil_lantern", FLBlocks.createLight(SimpleLightVariant.OIL_LANTERN));

    public static final LightBlock CANDLE_LANTERN = register("candle_lantern", FLBlocks.createLight(SimpleLightVariant.CANDLE_LANTERN));

    public static final LightBlock INCANDESCENT_LIGHT = register("incandescent_light", FLBlocks.createLight(SimpleLightVariant.INCANDESCENT_LIGHT));

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant) {
        return createLight(variant, LightBlock::new);
    }

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant, final BiFunction<AbstractBlock.Settings, LightVariant<?>, LightBlock> factory) {
        return () -> factory.apply(AbstractBlock.Settings.of(Material.DECORATION).emissiveLighting(state -> state.getValue(LightBlock.LIT) ? 15 : 0).noCollission(), variant);
    }
}
