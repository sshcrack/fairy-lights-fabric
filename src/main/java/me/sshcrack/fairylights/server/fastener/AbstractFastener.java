package me.sshcrack.fairylights.server.fastener;

import com.google.common.collect.ImmutableList;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.ConnectionType;
import me.sshcrack.fairylights.server.fastener.accessor.FastenerAccessor;
import me.sshcrack.fairylights.util.BoxBuilder;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public abstract class AbstractFastener<F extends FastenerAccessor> implements Fastener<F> {
    private final Map<UUID, Connection> outgoing = new HashMap<>();

    private final Map<UUID, Incoming> incoming = new HashMap<>();

    protected Box bounds = new Box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    @Nullable
    private World world;

    private boolean dirty;

    @Override
    public Optional<Connection> get(final UUID id) {
        return Optional.ofNullable(this.outgoing.get(id));
    }

    @Override
    public List<Connection> getOwnConnections() {
        return ImmutableList.copyOf(this.outgoing.values());
    }

    @Override
    public List<Connection> getAllConnections() {
        final ImmutableList.Builder<Connection> list = new ImmutableList.Builder<>();
        list.addAll(this.outgoing.values());
        if (this.world != null) {
            this.incoming.values().forEach(i -> i.get(this.world).ifPresent(list::add));
        }
        return list.build();
    }

    @Override
    public Box getBounds() {
        return this.bounds;
    }

    @Override
    public abstract BlockPos getPos();

    @Override
    public void setWorld(final World world) {
        this.world = world;
        this.outgoing.values().forEach(c -> c.setWorld(world));
    }

    @Nullable
    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean update() {
        final Iterator<Connection> it = this.outgoing.values().iterator();
        final Vec3d fromOffset = this.getConnectionPoint();
        boolean dirty = this.dirty;
        this.dirty = false;
        while (it.hasNext()) {
            final Connection connection = it.next();
            if (connection.update(fromOffset)) {
                dirty = true;
            }
            if (connection.isRemoved()) {
                dirty = true;
                it.remove();
                this.incoming.remove(connection.getUUID());
                if (this.world != null) {
                    this.drop(this.world, this.getPos(), connection);
                }
            }
        }
        if (this.world != null) {
            this.incoming.values().removeIf(incoming -> incoming.gone(this.world));
        }
        if (dirty) {
            this.calculateBoundingBox();
        }
        return dirty;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    protected void calculateBoundingBox() {
        if (this.outgoing.isEmpty()) {
            this.bounds = new Box(this.getPos());
            return;
        }
        final BoxBuilder builder = new BoxBuilder();
        for (final Connection connection : this.outgoing.values()) {
            final Curve catenary = connection.getCatenary();
            if (catenary == null) {
                continue;
            }
            final Curve.SegmentIterator it = catenary.iterator();
            while (it.next()) {
                builder.include(it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F));
                if (!it.hasNext()) {
                    builder.include(it.getX(1.0F), it.getY(1.0F), it.getZ(1.0F));
                }
            }
        }
        this.bounds = builder.add(this.getConnectionPoint()).build();
    }

    @Override
    public void dropItems(final World world, final BlockPos pos) {
        for (final Connection connection : this.getAllConnections()) {
            this.drop(world, pos, connection);
        }
    }

    private void drop(final World world, final BlockPos pos, final Connection connection) {
        if (!connection.shouldDrop()) return;
        final float offsetX = world.random.nextFloat() * 0.8F + 0.1F;
        final float offsetY = world.random.nextFloat() * 0.8F + 0.1F;
        final float offsetZ = world.random.nextFloat() * 0.8F + 0.1F;
        final ItemStack stack = connection.getItemStack();
        final ItemEntity entityItem = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack);
        final float scale = 0.05F;
        entityItem.setVelocity(
            world.random.nextGaussian() * scale,
            world.random.nextGaussian() * scale + 0.2F,
            world.random.nextGaussian() * scale
        );
        world.spawnEntity(entityItem);
        connection.noDrop();
    }

    @Override
    public void remove() {
        this.outgoing.values().forEach(Connection::remove);
    }

    @Override
    public boolean hasNoConnections() {
        return this.outgoing.isEmpty() && this.incoming.isEmpty();
    }

    @Override
    public boolean hasConnectionWith(final Fastener<?> fastener) {
        return this.getConnectionTo(fastener.createAccessor()) != null;
    }

    @Nullable
    @Override
    public Connection getConnectionTo(final FastenerAccessor destination) {
        for (final Connection connection : this.outgoing.values()) {
            if (connection.isDestination(destination)) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public boolean removeConnection(final UUID uuid) {
        final Connection connection = this.outgoing.remove(uuid);
        if (connection != null) {
            connection.remove();
            this.setDirty();
            return true;
        } else if (this.incoming.remove(uuid) != null) {
            this.setDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeConnection(final Connection connection) {
        return this.removeConnection(connection.getUUID());
    }

    @Override
    public boolean reconnect(final World world, final Connection connection, final Fastener<?> newDestination) {
        if (this.equals(newDestination) || newDestination.hasConnectionWith(this)) {
            return false;
        }
        final UUID uuid = connection.getUUID();
        if (connection.getDestination().get(world, false).filter(t -> {
            t.removeConnection(uuid);
            return true;
        }).isPresent()) {
            connection.setDestination(newDestination);
            connection.setDrop();
            newDestination.createIncomingConnection(this.world, uuid, this, connection.getType());
            this.setDirty();
            return true;
        }
        return false;
    }

    @Override
    public Connection connect(final World world, final Fastener<?> destination, final ConnectionType<?> type, final NbtCompound compound, final boolean drop) {
        final UUID uuid = MathHelper.randomUuid();
        final Connection connection = this.createOutgoingConnection(world, uuid, destination, type, compound, drop);
        destination.createIncomingConnection(world, uuid, this, type);
        return connection;
    }

    @Override
    public Connection createOutgoingConnection(final World world, final UUID uuid, final Fastener<?> destination, final ConnectionType<?> type, final NbtCompound compound, final boolean drop) {
        final Connection c = type.create(world, this, uuid);
        c.deserialize(destination, compound, drop);
        this.outgoing.put(uuid, c);
        this.setDirty();
        return c;
    }

    @Override
    public void createIncomingConnection(final World world, final UUID uuid, final Fastener<?> destination, final ConnectionType<?> type) {
        this.incoming.put(uuid, new Incoming(destination.createAccessor(), uuid));
        this.setDirty();
    }

    @Override
    public NbtCompound serializeNBT() {
        final NbtCompound compound = new NbtCompound();
        final NbtList outgoing = new NbtList();
        for (final Entry<UUID, Connection> connectionEntry : this.outgoing.entrySet()) {
            final UUID uuid = connectionEntry.getKey();
            final Connection connection = connectionEntry.getValue();
            final NbtCompound connectionCompound = new NbtCompound();
            connectionCompound.put("connection", connection.serialize());
            connectionCompound.putString("type", FairyLightsMod.CONNECTION_TYPES.getId(connection.getType()).toString());
            connectionCompound.putUuid("uuid", uuid);
            outgoing.add(connectionCompound);
        }
        compound.put("outgoing", outgoing);
        final NbtList incoming = new NbtList();
        for (final Entry<UUID, Incoming> e : this.incoming.entrySet()) {
            final NbtCompound tag = new NbtCompound();
            tag.putUuid("uuid", e.getKey());
            tag.put("fastener", FastenerType.serialize(e.getValue().fastener));
            incoming.add(tag);
        }
        compound.put("incoming", incoming);
        return compound;
    }

    @Override
    public void deserializeNBT(final NbtCompound compound) {
        final NbtList listConnections = compound.getList("outgoing", NbtCompound.COMPOUND_TYPE);
        final List<UUID> nbtUUIDs = new ArrayList<>();
        for (int i = 0; i < listConnections.size(); i++) {
            final NbtCompound connectionCompound = listConnections.getCompound(i);
            final UUID uuid;
            if (connectionCompound.containsUuid("uuid")) {
                uuid = connectionCompound.getUuid("uuid");
            } else {
                uuid = MathHelper.randomUuid();
            }
            nbtUUIDs.add(uuid);
            if (this.outgoing.containsKey(uuid)) {
                final Connection connection = this.outgoing.get(uuid);
                connection.deserialize(connectionCompound.getCompound("connection"));
            } else {
                final ConnectionType<?> type = FairyLightsMod.CONNECTION_TYPES.get(Identifier.tryParse(connectionCompound.getString("type")));
                if (type != null) {
                    final Connection connection = type.create(this.world, this, uuid);
                    connection.deserialize(connectionCompound.getCompound("connection"));
                    this.outgoing.put(uuid, connection);
                }
            }
        }
        final Iterator<Entry<UUID, Connection>> connectionsIter = this.outgoing.entrySet().iterator();
        while (connectionsIter.hasNext()) {
            final Entry<UUID, Connection> connection = connectionsIter.next();
            if (!nbtUUIDs.contains(connection.getKey())) {
                connectionsIter.remove();
                connection.getValue().remove();
            }
        }
        this.incoming.clear();
        final NbtList incoming = compound.getList("incoming", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < incoming.size(); i++) {
            final NbtCompound incomingNbt = incoming.getCompound(i);
            final UUID uuid = incomingNbt.getUuid("uuid");
            final FastenerAccessor fastener = FastenerType.deserialize(incomingNbt.getCompound("fastener"));
            this.incoming.put(uuid, new Incoming(fastener, uuid));
        }
        this.setDirty();
    }

    private final Optional<Fastener<?>> lazyOptional = Optional.of(this);

    @Override
    public <T> Optional<T> getCapability(final Capability<T> capability) {
        return capability == CapabilityHandler.FASTENER_CAP ? (Optional<T>) this.lazyOptional : Optional.empty();
    }

    static class Incoming {
        final FastenerAccessor fastener;

        final UUID id;

        Incoming(final FastenerAccessor fastener, final UUID id) {
            this.fastener = fastener;
            this.id = id;
        }

        boolean gone(final World world) {
            return this.fastener.isGone(world);
        }

        Optional<Connection> get(final World world) {
            return this.fastener.get(world, false).flatMap(f -> f.get(this.id));
        }
    }
}
