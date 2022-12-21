/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import me.sshcrack.fairylights.util.forge.network.simple.SimpleChannel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Means to distribute packets in various ways
 *
 * @see SimpleChannel#send(PacketTarget, Object)
 *
 * @param <T>
 */
public class PacketDistributor<T> {
    /**
     * Send to the player specified in the Supplier
     * <br/>
     * {@link #with(Supplier)} Player
     */
    public static final PacketDistributor<ServerPlayerEntity> PLAYER = new PacketDistributor<>(PacketDistributor::playerConsumer, NetworkDirection.PLAY_TO_CLIENT);

    public static final PacketDistributor<Entity> TRACKING_ENTITY_AND_SELF = new PacketDistributor<>(PacketDistributor::trackingEntityAndSelf, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to all tracking the Chunk in the Supplier
     * <br/>
     * {@link #with(Supplier)} Chunk
     */
    public static final PacketDistributor<Chunk> TRACKING_CHUNK = new PacketDistributor<>(PacketDistributor::trackingChunk, NetworkDirection.PLAY_TO_CLIENT);

    public static final class TargetPoint {

        private final ServerPlayerEntity excluded;
        private final double x;
        private final double y;
        private final double z;
        private final double r2;
        private final RegistryKey<World> dim;

        /**
         * A target point with excluded entity
         *
         * @param excluded Entity to exclude
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         */
        public TargetPoint(final ServerPlayerEntity excluded, final double x, final double y, final double z, final double r2, final RegistryKey<World> dim) {
            this.excluded = excluded;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r2 = r2;
            this.dim = dim;
        }

        /**
         * A target point without excluded entity
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         */
        public TargetPoint(final double x, final double y, final double z, final double r2, final RegistryKey<World> dim) {
            this.excluded = null;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r2 = r2;
            this.dim = dim;
        }

        /**
         * Helper to build a TargetPoint without excluded Entity
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         * @return A TargetPoint supplier
         */
        public static Supplier<TargetPoint> p(double x, double y, double z, double r2, RegistryKey<World> dim) {
            TargetPoint tp = new TargetPoint(x, y, z, r2, dim);
            return ()->tp;
        }

    }

    /**
     * A Distributor curried with a specific value instance, for actual dispatch
     *
     * @see SimpleChannel#send(PacketTarget, Object)
     *
     */
    public static class PacketTarget {
        private final Consumer<Packet<?>> packetConsumer;
        private final PacketDistributor<?> distributor;
        PacketTarget(final Consumer<Packet<?>> packetConsumer, final PacketDistributor<?> distributor) {
            this.packetConsumer = packetConsumer;
            this.distributor = distributor;
        }

        public void send(Packet<?> packet) {
            packetConsumer.accept(packet);
        }

        public NetworkDirection getDirection() {
            return distributor.direction;
        }

    }

    private final BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<Packet<?>>> functor;
    private final NetworkDirection direction;

    public PacketDistributor(BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<Packet<?>>> functor, NetworkDirection direction) {
        this.functor = functor;
        this.direction = direction;
    }

    /**
     * Apply the supplied value to the specific distributor to generate an instance for sending packets to.
     * @param input The input to apply
     * @return A curried instance
     */
    public PacketTarget with(Supplier<T> input) {
        return new PacketTarget(functor.apply(this, input), this);
    }


    private Consumer<Packet<?>> playerConsumer(final Supplier<ServerPlayerEntity> entityPlayerMPSupplier) {
        return p -> entityPlayerMPSupplier.get().networkHandler.connection.send(p);
    }

    private Consumer<Packet<?>> trackingEntity(final Supplier<Entity> entitySupplier) {
        return p-> {
            final Entity entity = entitySupplier.get();
            ((ServerChunkManager)entity.getEntityWorld().getChunkManager()).sendToOtherNearbyPlayers(entity, p);
        };
    }

    private Consumer<Packet<?>> trackingEntityAndSelf(final Supplier<Entity> entitySupplier) {
        return p-> {
            final Entity entity = entitySupplier.get();
            ((ServerChunkManager)entity.getEntityWorld().getChunkManager()).sendToNearbyPlayers(entity, p);
        };
    }

    private Consumer<Packet<?>> trackingChunk(final Supplier<WorldChunk> chunkPosSupplier) {
        return p -> {
            final WorldChunk chunk = chunkPosSupplier.get();
            ((ServerChunkManager)chunk.getWorld().getChunkManager()).threadedAnvilChunkStorage.getPlayersWatchingChunk(chunk.getPos(), false).forEach(e -> e.networkHandler.sendPacket(p));
        };
    }
}
