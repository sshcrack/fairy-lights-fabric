package me.sshcrack.fairylights.server.fastener.accessor;

import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.fastener.BlockFastener;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.fastener.FastenerType;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class BlockFastenerAccessor implements FastenerAccessor {
    private BlockPos pos = new BlockPos(BlockPos.ZERO);

    public BlockFastenerAccessor() {}

    public BlockFastenerAccessor(final BlockFastener fastener) {
        this(fastener.getPos());
    }

    public BlockFastenerAccessor(final BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public @NotNull LazyOptional<Fastener<?>> get(final World world, final boolean load) {
        if (load || world.isChunkLoaded(this.pos)) {
            final BlockEntity entity = world.getBlockEntity(this.pos);
            if (entity != null) {
                return ((CapabilityHelper<?>) entity).getCapability(CapabilityHandler.FASTENER_CAP);
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public boolean isGone(final World world) {
        if (world.isClient() || !world.isChunkLoaded(this.pos)) return false;
        final BlockEntity entity = world.getBlockEntity(this.pos);
        return entity == null || !((CapabilityHelper<?>)entity).getCapability(CapabilityHandler.FASTENER_CAP).isPresent();
    }

    @Override
    public FastenerType getType() {
        return FastenerType.BLOCK;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BlockFastenerAccessor) {
            return this.pos.equals(((BlockFastenerAccessor) obj).pos);
        }
        return false;
    }

    @Override
    public NbtCompound serialize() {
        return NbtHelper.fromBlockPos(this.pos);
    }

    @Override
    public void deserialize(final NbtCompound nbt) {
        this.pos = NbtHelper.toBlockPos(nbt);
    }
}
