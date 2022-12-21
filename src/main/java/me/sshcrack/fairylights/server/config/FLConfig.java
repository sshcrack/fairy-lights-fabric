package me.sshcrack.fairylights.server.config;

import com.mojang.datafixers.util.Pair;
import me.sshcrack.fairylights.FairyLightsMod;

public final class FLConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;


    private FLConfig() {}

    public static void register() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(FairyLightsMod.ModID + "config").provider(configs).request();
    }


    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("config.fairylights.christmas_jingles", true), "If true jingles will play during Christmas.");
        configs.addKeyValuePair(new Pair<>("config.fairylights.jingle_amplitude", 40), "The distance that jingles can be heard in blocks.");
        // TODO
        //configs.addKeyValuePair(new Pair<>("config.fairylights.tutorial.progress", "none"), "The hanging lights tutorial progress, once any light item enters the inventory a toast appears prompting to craft hanging lights. A finished tutorial progress value is 'complete' and an unstarted tutorial is 'none'.");
    }

    // TODO
    /*
    public static String getTutorialProgress() {
        return CONFIG.getOrDefault("config.fairylights.tutorial.progress", "none");
    }

    public static void setTutorialProgress(String prog) {
    }
 */

    public static boolean isJingleEnabled() {
        return CONFIG.getOrDefault("config.fairylights.christmas_jingles", false);
    }

    public static int getJingleAmplitude() {
        return Math.max(1, CONFIG.getOrDefault("config.fairylights.jingle_amplitude", 40));
    }
}
