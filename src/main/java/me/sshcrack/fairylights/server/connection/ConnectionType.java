package me.sshcrack.fairylights.server.connection;

import me.sshcrack.fairylights.server.fastener.Fastener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Supplier;

public class ConnectionType<T extends Connection> {
    private final Factory<T> factory;

    private final Item item;

    public ConnectionType(final Builder<T> builder) {
        this.factory = builder.factory;
        this.item = builder.item;
    }

    public T create(final World world, final Fastener<?> fastener, final UUID uuid) {
        return this.factory.create(this, world, fastener, uuid);
    }

    public Item getItem() {
        return this.item;
    }

    public static final class Builder<T extends Connection> {
        final Factory<T> factory;

        Item item = Items.AIR;

        private Builder(final Factory<T> factory) {
            this.factory = factory;
        }

        public Builder<T> item(final Item item) {
            this.item = item;
            return this;
        }

        public ConnectionType<T> build() {
            return new ConnectionType<>(this);
        }

        public static <T extends Connection> Builder<T> create(final Factory<T> factory) {
            return new Builder<>(factory);
        }
    }

    public interface Factory<T extends Connection> {
        T create(final ConnectionType<T> type, final World world, final Fastener<?> fastener, final UUID uuid);
    }
}
