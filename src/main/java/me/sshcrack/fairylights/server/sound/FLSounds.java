package me.sshcrack.fairylights.server.sound;

import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class FLSounds {
    private FLSounds() {}

    public static final SoundEvent CORD_STRETCH = create("cord.stretch");

    public static final SoundEvent CORD_CONNECT = create("cord.connect");

    public static final SoundEvent CORD_DISCONNECT = create("cord.disconnect");

    public static final SoundEvent CORD_SNAP = create("cord.snap");

    public static final SoundEvent JINGLE_BELL = create("jingle_bell");

    public static final SoundEvent FEATURE_COLOR_CHANGE = create("feature.color_change");

    public static final SoundEvent FEATURE_LIGHT_TURNON = create("feature.light_turnon");

    public static final SoundEvent FEATURE_LIGHT_TURNOFF = create("feature.light_turnoff");

    private static SoundEvent create(final String name) {
        return Registry.register(Registry.SOUND_EVENT, name, new SoundEvent(new Identifier(FairyLightsMod.ModID, name)));
    }
}
