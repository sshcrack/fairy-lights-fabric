package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import me.sshcrack.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import me.sshcrack.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import net.minecraft.util.math.BlockPos;

public final class FenceFastener extends EntityFastener<FenceFastenerEntity> {
    public FenceFastener(final FenceFastenerEntity entity) {
        super(entity);
    }

    @Override
    public EntityFastenerAccessor<FenceFastenerEntity> createAccessor() {
        return new FenceFastenerAccessor(this);
    }

    @Override
    public BlockPos getPos() {
        return this.entity.getBlockPos();
    }

    @Override
    public boolean isMoving() {
        return false;
    }
}
