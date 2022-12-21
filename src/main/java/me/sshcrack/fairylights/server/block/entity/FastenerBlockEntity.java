package me.sshcrack.fairylights.server.block.entity;

import me.sshcrack.fairylights.server.block.FLBlocks;
import me.sshcrack.fairylights.server.block.FastenerBlock;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.util.forge.capabilities.*;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public final class FastenerBlockEntity extends BlockEntity implements CapabilityHelper<BlockEntity> {
    public FastenerBlockEntity(final BlockPos pos, final BlockState state) {
        super(FLBlockEntities.FASTENER.get(), pos ,state);

        this.provider = new CapabilityProvider<>(BlockEntity.class, this);
        provider.gatherCapabilities();
    }

    //TODO Maybe important idk
    /* I dunno if that does something important we'll see
    @Override
    public Box getRenderBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().expand(1)).orElseGet(super::getRenderBoundingBox);
    }
    */

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
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void setWorld(final World world) {
        super.setWorld(world);
        this.getFastener().ifPresent(fastener -> fastener.setWorld(world));
    }

    public static void tick(World level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(fastener -> {
            if (!level.isClient() && fastener.hasNoConnections()) {
                level.removeBlock(pos, false);
            } else if (!level.isClient() && fastener.update()) {
                be.markDirty();
                level.setBlockState(pos, state, 3);
            }
        });
    }

    public static void tickClient(World level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(Fastener::update);
    }

    @Override
    public void markRemoved() {
        this.getFastener().ifPresent(Fastener::remove);
        super.markRemoved();
    }



    private CapabilityProvider<BlockEntity> provider;

    @Override
    public @NotNull CapabilityProvider<BlockEntity> getProvider() {
        return provider;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!nbt.contains(CapabilityManager.NBT_IDENTIFIER, NbtCompound.COMPOUND_TYPE))
            return;

        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null) {
            dispatcher.deserializeNBT(nbt.getCompound(CapabilityManager.NBT_IDENTIFIER));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null)
            nbt.put(CapabilityManager.NBT_IDENTIFIER, dispatcher.serializeNBT());
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return provider.getCapability(cap);
    }


    //TODO Maybe fix?
    @SuppressWarnings("unchecked")
    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }
}
