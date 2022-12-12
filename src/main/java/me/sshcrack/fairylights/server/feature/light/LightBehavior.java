package me.sshcrack.fairylights.server.feature.light;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface LightBehavior {
    default void power(final boolean powered, final Light<?> light) {
        this.power(powered, true, light);
    }

    void power(final boolean powered, final boolean now, final Light<?> light);

    void tick(final World world, final Vec3d origin, final Light<?> light);

    default void animateTick(final World world, final Vec3d origin, final Light<?> light) {}
}
