package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.fastener.accessor.EntityFastenerAccessor;

import javax.swing.text.html.parser.Entity;

public abstract class EntityFastener<E extends Entity> extends AbstractFastener<EntityFastenerAccessor<E>> {
    protected final E entity;

    public EntityFastener(final E entity) {
        this.entity = entity;
        this.setWorld(entity.level);
    }

    @Override
    public Direction getFacing() {
        return Direction.UP;
    }

    public E getEntity() {
        return this.entity;
    }

    @Override
    public BlockPos getPos() {
        return this.entity.blockPosition();
    }

    @Override
    public Vec3 getConnectionPoint() {
        return this.entity.position();
    }
}
