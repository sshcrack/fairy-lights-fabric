package me.sshcrack.fairylights.server.feature.light;

public interface ColorLightBehavior extends LightBehavior {
    float getRed(final float delta);

    float getGreen(final float delta);

    float getBlue(final float delta);
}
