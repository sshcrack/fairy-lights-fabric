package me.sshcrack.fairylights.server.block.entity;

import me.sshcrack.fairylights.server.block.FLBlocks;
import me.sshcrack.fairylights.server.block.FastenerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class FastenerBlockEntity extends BlockEntity {
    public FastenerBlockEntity(final BlockPos pos, final BlockState state) {
        super(FLBlockEntities.FASTENER.get(), pos ,state);
    }

    @Override
    public Box getRenderBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().inflate(1)).orElseGet(super::getRenderBoundingBox);
    }

    public Vec3d getOffset() {
        return FLBlocks.FASTENER.get().getOffset(this.getFacing(), 0.125F);
    }

    public Direction getFacing() {
        final BlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() != FLBlocks.FASTENER.get()) {
            return Direction.UP;
        }
        return state.get(FastenerBlock.FACING);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void setLevel(final Level world) {
        super.setLevel(world);
        this.getFastener().ifPresent(fastener -> fastener.setWorld(world));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(fastener -> {
            if (!level.isClientSide() && fastener.hasNoConnections()) {
                level.removeBlock(pos, false);
            } else if (!level.isClientSide() && fastener.update()) {
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        });
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(Fastener::update);
    }

    @Override
    public void setRemoved() {
        this.getFastener().ifPresent(Fastener::remove);
        super.setRemoved();
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }
}
