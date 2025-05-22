package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import me.sshcrack.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public abstract class EntityFastener<E extends Entity> extends AbstractFastener<EntityFastenerAccessor<E>> {
    protected final E entity;

    public EntityFastener(final E entity) {
        this.entity = entity;
        this.setWorld(entity.world);
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
        return this.entity.getBlockPos();
    }

    @Override
    public Vec3d getConnectionPoint() {
        return this.entity.getPos();
    }
}
