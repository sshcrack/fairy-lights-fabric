package me.sshcrack.fairylights.client;

import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class FLModelLayers {

    public static final EntityModelLayer BOW = main("bow");
    public static final EntityModelLayer GARLAND_RINGS = main("garland_rings");
    public static final EntityModelLayer TINSEL_STRIP = main("tinsel_strip");
    public static final EntityModelLayer FAIRY_LIGHT = main("fairy_light");
    public static final EntityModelLayer PAPER_LANTERN = main("paper_lantern");
    public static final EntityModelLayer ORB_LANTERN = main("orb_lantern");
    public static final EntityModelLayer FLOWER_LIGHT = main("flower_light");
    public static final EntityModelLayer CANDLE_LANTERN_LIGHT = main("color_candle_lantern");
    public static final EntityModelLayer OIL_LANTERN_LIGHT = main("color_oil_lantern_light");
    public static final EntityModelLayer JACK_O_LANTERN = main("jack_o_lantern");
    public static final EntityModelLayer SKULL_LIGHT = main("skull_light");
    public static final EntityModelLayer GHOST_LIGHT = main("ghost_light");
    public static final EntityModelLayer SPIDER_LIGHT = main("spider_light");
    public static final EntityModelLayer WITCH_LIGHT = main("witch_light");
    public static final EntityModelLayer SNOWFLAKE_LIGHT = main("snowflake_light");
    public static final EntityModelLayer HEART_LIGHT = main("heart_light");
    public static final EntityModelLayer MOON_LIGHT = main("moon_light");
    public static final EntityModelLayer STAR_LIGHT = main("star_light");
    public static final EntityModelLayer ICICLE_LIGHTS_1 = main("icicle_lights_1");
    public static final EntityModelLayer ICICLE_LIGHTS_2 = main("icicle_lights_2");
    public static final EntityModelLayer ICICLE_LIGHTS_3 = main("icicle_lights_3");
    public static final EntityModelLayer ICICLE_LIGHTS_4 = main("icicle_lights_4");
    public static final EntityModelLayer METEOR_LIGHT = main("meteor_light");
    public static final EntityModelLayer OIL_LANTERN = main("oil_lantern");
    public static final EntityModelLayer CANDLE_LANTERN = main("candle_lantern");
    public static final EntityModelLayer INCANDESCENT_LIGHT = main("incandescent_light");
    public static final EntityModelLayer LETTER_WIRE = main("letter_wire");
    public static final EntityModelLayer PENNANT_WIRE = main("pennant_wire");
    public static final EntityModelLayer TINSEL_WIRE = main("tinsel_wire");
    public static final EntityModelLayer VINE_WIRE = main("vine_wire");
    public static final EntityModelLayer LIGHTS_WIRE = main("lights_wire");

    private static EntityModelLayer main(String name) {
        return layer(name, "main");
    }

    private static EntityModelLayer layer(String name, String layer) {
        return new EntityModelLayer(new Identifier(FairyLightsMod.ModID, name), layer);
    }
}
