package me.sshcrack.fairylights.server.net;

import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.fastener.FastenerType;
import me.sshcrack.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class ConnectionMessage implements Message {
    public BlockPos pos;

    public FastenerAccessor accessor;

    public UUID uuid;

    public ConnectionMessage() {}

    public ConnectionMessage(final Connection connection) {
        final Fastener<?> fastener = connection.getFastener();
        this.pos = fastener.getPos();
        this.accessor = fastener.createAccessor();
        this.uuid = connection.getUUID();
    }

    @Override
    public void encode(final PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(FastenerType.serialize(this.accessor));
        buf.writeUuid(this.uuid);
    }

    @Override
    public void decode(final PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.accessor = FastenerType.deserialize(Objects.requireNonNull(buf.readNbt(), "tag"));
        this.uuid = buf.readUuid();
    }

    @SuppressWarnings("unchecked")
    public static <C extends Connection> Optional<C> getConnection(final ConnectionMessage message, final Predicate<? super Connection> typePredicate, final World world) {
        return message.accessor.get(world, false).resolve().flatMap(f -> (Optional<C>) f.get(message.uuid).filter(typePredicate));
    }
}
