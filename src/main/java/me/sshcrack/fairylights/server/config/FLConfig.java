package me.sshcrack.fairylights.server.config;

import com.mojang.datafixers.util.Pair;
import me.sshcrack.fairylights.FairyLightsMod;

public final class FLConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean JINGLE_ENABLED;
    public static int JINGLE_AMPLITUDE;

    private FLConfig() {}

    public static void register() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(FairyLightsMod.ModID + "config").provider(configs).request();
    }


    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("config.fairylights.christmas_jingles", "Just a Testing string!"), "String");
        configs.addKeyValuePair(new Pair<>("config.fairylights.jingle_amplitude", 40), "int");
    }


    public static boolean isJingleEnabled() {
        return CONFIG.getOrDefault("config.fairylights.christmas_jingles", false);
    }

    public static int getJingleAmplitude() {
        return Math.max(1, CONFIG.getOrDefault("config.fairylights.jingle_amplitude", 40));
    }
}
