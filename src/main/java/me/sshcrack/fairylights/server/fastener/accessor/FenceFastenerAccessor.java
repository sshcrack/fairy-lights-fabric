package me.sshcrack.fairylights.server.fastener.accessor;


import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import me.sshcrack.fairylights.server.fastener.EntityFastener;
import me.sshcrack.fairylights.server.fastener.FastenerType;

public final class FenceFastenerAccessor extends EntityFastenerAccessor<FenceFastenerEntity> {
    public FenceFastenerAccessor() {
        super(FenceFastenerEntity.class);
    }

    public FenceFastenerAccessor(final EntityFastener<FenceFastenerEntity> fastener) {
        super(FenceFastenerEntity.class, fastener);
    }

    @Override
    public FastenerType getType() {
        return FastenerType.FENCE;
    }
}
