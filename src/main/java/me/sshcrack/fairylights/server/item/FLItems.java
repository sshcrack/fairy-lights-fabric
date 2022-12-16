package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.block.FLBlocks;
import me.sshcrack.fairylights.server.block.LightBlock;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class FLItems {
    private FLItems() {}

    public static <T extends Item> Supplier<T> register(String name, Supplier<T> item) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return () -> Registry.register(Registry.ITEM, id, item.get());
    }

    public static final Supplier<ConnectionItem> HANGING_LIGHTS = register("hanging_lights", () -> new HangingLightsConnectionItem(defaultProperties()));

    public static final Supplier<ConnectionItem> PENNANT_BUNTING = register("pennant_bunting", () -> new PennantBuntingConnectionItem(defaultProperties()));

    public static final Supplier<ConnectionItem> TINSEL = register("tinsel", () -> new TinselConnectionItem(defaultProperties()));

    public static final Supplier<ConnectionItem> LETTER_BUNTING = register("letter_bunting", () -> new LetterBuntingConnectionItem(defaultProperties()));

    public static final Supplier<ConnectionItem> GARLAND = register("garland", () -> new GarlandConnectionItem(defaultProperties()));

    public static final Supplier<LightItem> FAIRY_LIGHT = register("fairy_light", FLItems.createColorLight(FLBlocks.FAIRY_LIGHT));

    public static final Supplier<LightItem> PAPER_LANTERN = register("paper_lantern", FLItems.createColorLight(FLBlocks.PAPER_LANTERN));

    public static final Supplier<LightItem> ORB_LANTERN = register("orb_lantern", FLItems.createColorLight(FLBlocks.ORB_LANTERN));

    public static final Supplier<LightItem> FLOWER_LIGHT = register("flower_light", FLItems.createColorLight(FLBlocks.FLOWER_LIGHT));

    public static final Supplier<LightItem> CANDLE_LANTERN_LIGHT = register("candle_lantern_light", FLItems.createColorLight(FLBlocks.CANDLE_LANTERN_LIGHT));

    public static final Supplier<LightItem> OIL_LANTERN_LIGHT = register("oil_lantern_light", FLItems.createColorLight(FLBlocks.OIL_LANTERN_LIGHT));

    public static final Supplier<LightItem> JACK_O_LANTERN = register("jack_o_lantern", FLItems.createColorLight(FLBlocks.JACK_O_LANTERN));

    public static final Supplier<LightItem> SKULL_LIGHT = register("skull_light", FLItems.createColorLight(FLBlocks.SKULL_LIGHT));

    public static final Supplier<LightItem> GHOST_LIGHT = register("ghost_light", FLItems.createColorLight(FLBlocks.GHOST_LIGHT));

    public static final Supplier<LightItem> SPIDER_LIGHT = register("spider_light", FLItems.createColorLight(FLBlocks.SPIDER_LIGHT));

    public static final Supplier<LightItem> WITCH_LIGHT = register("witch_light", FLItems.createColorLight(FLBlocks.WITCH_LIGHT));

    public static final Supplier<LightItem> SNOWFLAKE_LIGHT = register("snowflake_light", FLItems.createColorLight(FLBlocks.SNOWFLAKE_LIGHT));

    public static final Supplier<LightItem> HEART_LIGHT = register("heart_light", FLItems.createColorLight(FLBlocks.HEART_LIGHT));

    public static final Supplier<LightItem> MOON_LIGHT = register("moon_light", FLItems.createColorLight(FLBlocks.MOON_LIGHT));

    public static final Supplier<LightItem> STAR_LIGHT = register("star_light", FLItems.createColorLight(FLBlocks.STAR_LIGHT));

    public static final Supplier<LightItem> ICICLE_LIGHTS = register("icicle_lights", FLItems.createColorLight(FLBlocks.ICICLE_LIGHTS));

    public static final Supplier<LightItem> METEOR_LIGHT = register("meteor_light", FLItems.createColorLight(FLBlocks.METEOR_LIGHT));

    public static final Supplier<LightItem> OIL_LANTERN = register("oil_lantern", FLItems.createLight(FLBlocks.OIL_LANTERN, LightItem::new));

    public static final Supplier<LightItem> CANDLE_LANTERN = register("candle_lantern", FLItems.createLight(FLBlocks.CANDLE_LANTERN, LightItem::new));

    public static final Supplier<LightItem> INCANDESCENT_LIGHT = register("incandescent_light", FLItems.createLight(FLBlocks.INCANDESCENT_LIGHT, LightItem::new));

    public static final Supplier<Item> TRIANGLE_PENNANT = register("triangle_pennant", () -> new PennantItem(defaultProperties()));

    public static final Supplier<Item> SPEARHEAD_PENNANT = register("spearhead_pennant", () -> new PennantItem(defaultProperties()));

    public static final Supplier<Item> SWALLOWTAIL_PENNANT = register("swallowtail_pennant", () -> new PennantItem(defaultProperties()));

    public static final Supplier<Item> SQUARE_PENNANT = register("square_pennant", () -> new PennantItem(defaultProperties()));

    private static Item.Settings defaultProperties() {
        return new Item.Settings().group(FairyLightsMod.ITEM_GROUP);
    }

    private static Supplier<LightItem> createLight(final Supplier<LightBlock> block, final BiFunction<LightBlock, Item.Settings, LightItem> factory) {
        return () -> factory.apply(block.get(), defaultProperties().maxCount(16));
    }

    private static Supplier<LightItem> createColorLight(final Supplier<LightBlock> block) {
        return createLight(block, ColorLightItem::new);
    }

    public static Stream<LightItem> lights() {
        return Registry.ITEM.getEntrySet().stream()
                .map(Map.Entry::getValue)
                .filter(e -> e instanceof LightItem)
                .map(e -> (LightItem) e);
    }
}
