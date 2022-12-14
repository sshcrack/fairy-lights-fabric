package me.sshcrack.fairylights.server.feature.light;

import me.sshcrack.fairylights.server.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FixedColorBehavior implements ColorLightBehavior {
    private final float red;

    private final float green;

    private final float blue;

    public FixedColorBehavior(final float red, final float green, final float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public float getRed(final float delta) {
        return this.red;
    }

    @Override
    public float getGreen(final float delta) {
        return this.green;
    }

    @Override
    public float getBlue(final float delta) {
        return this.blue;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
    }

    public static ColorLightBehavior create(final ItemStack stack) {
        final int rgb = DyeableItem.getColor(stack);
        final float red = (rgb >> 16 & 0xFF) / 255.0F;
        final float green = (rgb >> 8 & 0xFF) / 255.0F;
        final float blue = (rgb & 0xFF) / 255.0F;
        return new FixedColorBehavior(red, green, blue);
    }
}
