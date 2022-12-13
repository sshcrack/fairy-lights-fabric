package me.sshcrack.fairylights.server.feature.light;

import me.sshcrack.fairylights.server.config.FLConfig;
import me.sshcrack.fairylights.server.feature.HangingFeature;
import me.sshcrack.fairylights.server.item.LightVariant;
import me.sshcrack.fairylights.server.sound.FLSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class Light<T extends LightBehavior> extends HangingFeature {
    private static final int SWAY_RATE = 10;

    private static final int SWAY_PEAK_COUNT = 5;

    private static final int SWAY_CYCLE = SWAY_RATE * SWAY_PEAK_COUNT;

    private final ItemStack item;

    private final LightVariant<T> variant;

    private final T behavior;

    private int sway;

    private boolean swaying;

    private boolean swayDirection;

    private int tick;

    private int lastJingledTick = -1;

    private boolean powered;

    public Light(final int index, final Vec3d point, final float yaw, final float pitch, final ItemStack item, final LightVariant<T> variant, final float descent) {
        super(index, point, yaw, pitch, 0.0F, descent);
        this.item = item;
        this.variant = variant;
        this.behavior = variant.createBehavior(item);
    }

    public T getBehavior() {
        return this.behavior;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public LightVariant<T> getVariant() {
        return this.variant;
    }

    public void jingle(final World world, final Vec3d origin, final int note) {
        this.jingle(world, origin, note, ParticleTypes.NOTE);
    }

    public void jingle(final World world, final Vec3d origin, final int note, final ParticleEffect particle) {
        this.jingle(world, origin, note, FLSounds.JINGLE_BELL, particle);
    }

    public void jingle(final World world, final Vec3d origin, final int note, final SoundEvent sound, final ParticleEffect... particles) {
        if (world.isClient()) {
            final double x = origin.x + this.point.x;
            final double y = origin.y + this.point.y;
            final double z = origin.z + this.point.z;
            for (final ParticleEffect particle : particles) {
                double vx = world.random.nextGaussian();
                double vy = world.random.nextGaussian();
                double vz = world.random.nextGaussian();
                final double t = world.random.nextDouble() * (0.4 - 0.2) + 0.2;
                final double mag = t / Math.sqrt(vx * vx + vy * vy + vz * vz);
                vx *= mag;
                vy *= mag;
                vz *= mag;
                world.addParticle(particle, x + vx, y + vy, z + vz, particle == ParticleTypes.NOTE ? note / 24D : 0, 0, 0);
            }
            if (this.lastJingledTick != this.tick) {
                world.playSound(x, y, z, sound, SoundCategory.BLOCKS, FLConfig.getJingleAmplitude() / 16F, (float) Math.pow(2, (note - 12) / 12F), false);
                this.startSwaying(world.random.nextBoolean());
                this.lastJingledTick = this.tick;
            }
        }
    }

    public void startSwaying(final boolean swayDirection) {
        this.swayDirection = swayDirection;
        this.swaying = true;
        this.sway = 0;
    }

    public void stopSwaying() {
        this.sway = 0;
        this.roll = 0.0F;
        this.swaying = false;
    }

    public void power(final boolean powered, final boolean now) {
        this.behavior.power(powered, now, this);
        this.powered = powered;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void tick(final World world, final Vec3d origin) {
        super.tick(world);
        this.behavior.tick(world, origin, this);
        if (this.swaying) {
            if (this.sway >= SWAY_CYCLE) {
                this.stopSwaying();
            } else {
                this.roll = (float) (Math.sin((this.swayDirection ? 1 : -1) * 2 * Math.PI / SWAY_RATE * this.sway) * Math.pow(180 / Math.PI * 2, -this.sway / (float) SWAY_CYCLE));
                this.sway++;
            }
        }
        this.tick++;
    }

    @Override
    public Box getBounds() {
        return this.getVariant().getBounds();
    }

    @Override
    public boolean parallelsCord() {
        return this.getVariant().parallelsCord();
    }
}
