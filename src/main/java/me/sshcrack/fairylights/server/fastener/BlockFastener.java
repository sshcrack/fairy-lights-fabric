package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.block.entity.FastenerBlockEntity;
import me.sshcrack.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BlockFastener extends AbstractFastener<BlockFastenerAccessor> {
    private final FastenerBlockEntity fastener;

    private final BlockView view;

    public BlockFastener(final FastenerBlockEntity fastener, final BlockView view) {
        this.fastener = fastener;
        this.view = view;
        this.bounds = new Box(fastener.getPos());
        this.setWorld(fastener.getWorld());
    }

    @Override
    public Direction getFacing() {
        return this.fastener.getFacing();
    }

    @Override
    public boolean isMoving() {
        return this.view.isMoving(this.getWorld(), this.fastener.getPos());
    }

    @Override
    public BlockPos getPos() {
        return this.fastener.getPos();
    }

    @Override
    public Vec3d getConnectionPoint() {
        return this.view.getPosition(this.getWorld(), this.fastener.getPos(), Vec3d.of(this.getPos()).add(this.fastener.getOffset()));
    }

    @Override
    public BlockFastenerAccessor createAccessor() {
        return new BlockFastenerAccessor(this);
    }
}
