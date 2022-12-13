package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.ConnectionType;
import me.sshcrack.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<NbtCompound> {
    @Override
    NbtCompound serializeNBT();

    Optional<Connection> get(final UUID id);

    List<Connection> getOwnConnections();

    List<Connection> getAllConnections();

    default Optional<Connection> getFirstConnection() {
        return this.getAllConnections().stream().findFirst();
    }

    Box getBounds();

    Vec3d getConnectionPoint();

    BlockPos getPos();

    Direction getFacing();

    void setWorld(World world);

    @Nullable
    World getWorld();

    F createAccessor();

    boolean isMoving();

    default void resistSnap(final Vec3d from) {}

    boolean update();

    void setDirty();

    void dropItems(World world, BlockPos pos);

    void remove();

    boolean hasNoConnections();

    boolean hasConnectionWith(Fastener<?> fastener);

    @Nullable
    Connection getConnectionTo(FastenerAccessor destination);

    boolean removeConnection(UUID uuid);

    boolean removeConnection(Connection connection);

    boolean reconnect(final World world, Connection connection, Fastener<?> newDestination);

    Connection connect(World world, Fastener<?> destination, ConnectionType<?> type, NbtCompound compound, final boolean drop);

    Connection createOutgoingConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType<?> type, NbtCompound compound, final boolean drop);

    void createIncomingConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType<?> type);
}
