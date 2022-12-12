package me.sshcrack.fairylights.server.fastener.accessor;

import me.sshcrack.fairylights.server.fastener.EntityFastener;
import me.sshcrack.fairylights.server.fastener.Fastener;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class EntityFastenerAccessor<E extends Entity> implements FastenerAccessor {
    private final Class<? extends E> entityClass;

    private UUID uuid;

    @Nullable
    private E entity;

    @Nullable
    private Vec3d pos;

    public EntityFastenerAccessor(final Class<? extends E> entityClass) {
        this(entityClass, (UUID) null);
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final EntityFastener<E> fastener) {
        this(entityClass, fastener.getEntity().getUuid());
        this.entity = fastener.getEntity();
        this.pos = this.entity.position();
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final UUID uuid) {
        this.entityClass = entityClass;
        this.uuid = uuid;
    }

    @Override
    public Optional<Fastener<?>> get(final World world, final boolean load) {
        if (this.entity == null) {
            if (world instanceof ServerWorld) {
                final Entity e = ((ServerWorld) world).getEntity(this.uuid);
                if (this.entityClass.isInstance(e)) {
                    this.entity = this.entityClass.cast(e);
                }
            } else if (this.pos != null) {
                for (final E entity : world.getEntitiesByClass(this.entityClass, new Box(this.pos.subtract(1.0D, 1.0D, 1.0D), this.pos.add(1.0D, 1.0D, 1.0D)), EntityPredicates
                        .EXCEPT_SPECTATOR)) {
                    if (this.uuid.equals(entity.getUUID())) {
                        this.entity = entity;
                        break;
                    }
                }
            }
        }
        if (this.entity != null && this.entity.World == world) {
            this.pos = this.entity.position();
            return this.entity.getCapability(CapabilityHandler.FASTENER_CAP);
        }
        return Optional.empty();
    }

    @Override
    public boolean isGone(final World world) {
        return !world.isClient() && this.entity != null && (!this.entity.getCapability(CapabilityHandler.FASTENER_CAP).isPresent() || this.entity.World != world);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EntityFastenerAccessor<?>) {
            return this.uuid.equals(((EntityFastenerAccessor<?>) obj).uuid);
        }
        return false;
    }

    @Override
    public NbtCompound serialize() {
        final NbtCompound tag = new NbtCompound();
        tag.putUUID("UUID", this.uuid);
        if (this.pos != null) {
            final ListTag pos = new ListTag();
            pos.add(DoubleTag.valueOf(this.pos.x));
            pos.add(DoubleTag.valueOf(this.pos.y));
            pos.add(DoubleTag.valueOf(this.pos.z));
            tag.put("Pos", pos);
        }
        return tag;
    }

    @Override
    public void deserialize(final NbtCompound tag) {
        this.uuid = tag.getUUID("UUID");
        if (tag.contains("Pos", Tag.TAG_LIST)) {
            final ListTag pos = tag.getList("Pos", Tag.TAG_DOUBLE);
            this.pos = new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        } else {
            this.pos = null;
        }
        this.entity = null;
    }
}
