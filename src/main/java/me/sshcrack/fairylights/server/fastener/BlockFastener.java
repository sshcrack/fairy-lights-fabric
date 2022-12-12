package me.sshcrack.fairylights.server.fastener;

import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.sshcrack.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class BlockFastener extends AbstractFastener<BlockFastenerAccessor> {
    private final FastenerBlockEntity fastener;

    private final BlockView view;

    public BlockFastener(final FastenerBlockEntity fastener, final BlockView view) {
        this.fastener = fastener;
        this.view = view;
        this.bounds = new Box(fastener.getBlockPos());
        this.setWorld(fastener.getLevel());
    }

    @Override
    public Direction getFacing() {
        return this.fastener.getFacing();
    }

    @Override
    public boolean isMoving() {
        return this.view.isMoving(this.getWorld(), this.fastener.getBlockPos());
    }

    @Override
    public BlockPos getPos() {
        return this.fastener.getBlockPos();
    }

    @Override
    public Vec3d getConnectionPoint() {
        return this.view.getPosition(this.getWorld(), this.fastener.getBlockPos(), Vec3d.of(this.getPos()).add(this.fastener.getOffset()));
    }

    @Override
    public BlockFastenerAccessor createAccessor() {
        return new BlockFastenerAccessor(this);
    }
}
