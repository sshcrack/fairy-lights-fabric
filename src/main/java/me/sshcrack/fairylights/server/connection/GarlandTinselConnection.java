package me.sshcrack.fairylights.server.connection;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.item.DyeableItem;
import me.sshcrack.fairylights.server.fastener.Fastener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.util.UUID;

public final class GarlandTinselConnection extends Connection {
    private int color;

    public GarlandTinselConnection(final ConnectionType<? extends GarlandTinselConnection> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
        this.color = DyeableItem.getColor(DyeColor.LIGHT_GRAY);
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public float getRadius() {
        return 0.125F;
    }

    @Override
    public CompoundTag serializeLogic() {
        return DyeableItem.setColor(super.serializeLogic(), this.color);
    }

    @Override
    public void deserializeLogic(final NbtCompound compound) {
        super.deserializeLogic(compound);
        this.color = DyeableItem.getColor(compound);
    }
}
