package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import me.sshcrack.fairylights.util.forge.events.Event;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

public class CollectFastenersEvent extends Event {
    private final World world;

    private final Box region;

    private final Set<Fastener<?>> fasteners;

    public CollectFastenersEvent(final World world, final Box region, final Set<Fastener<?>> fasteners) {
        this.world = world;
        this.region = region;
        this.fasteners = fasteners;
    }

    public World getWorld() {
        return this.world;
    }

    public Box getRegion() {
        return this.region;
    }

    public void accept(final Chunk chunk) {
        try {
            Field field = Chunk.class.getDeclaredField("blockEntities");
            field.setAccessible(true);

            Collection<BlockEntity> entities = (Collection<BlockEntity>) field.get(chunk);
            for (final BlockEntity entity : entities) {
                //TODO fix capability things here
                this.accept(entity);
            }
        } catch (final ConcurrentModificationException | NoSuchFieldException | IllegalAccessException e) {
            // RenderChunk's may find an invalid block entity while building and trigger a remove not on main thread
        }
    }

    public void accept(final ICapabilityProvider provider) {
        provider.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(this::accept);
    }

    public void accept(final Fastener<?> fastener) {
        if (this.region.contains(fastener.getConnectionPoint())) {
            this.fasteners.add(fastener);
        }
    }
}
