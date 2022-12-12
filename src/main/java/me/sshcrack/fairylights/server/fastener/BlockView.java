package me.sshcrack.fairylights.server.fastener;

import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface BlockView {
    boolean isMoving(final World world, final BlockPos source);

    Vec3 getPosition(final World world, final BlockPos source, final Vec3 pos);

    void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta);
}
