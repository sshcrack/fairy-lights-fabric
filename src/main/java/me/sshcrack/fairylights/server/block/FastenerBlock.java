package me.sshcrack.fairylights.server.block;


import me.sshcrack.fairylights.server.block.entity.FLBlockEntities;
import me.sshcrack.fairylights.server.block.entity.FastenerBlockEntity;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.connection.HangingLightsConnection;
import me.sshcrack.fairylights.server.event.ServerEventHandler;
import me.sshcrack.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FastenerBlock extends FacingBlock implements BlockEntityProvider {
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    private static final VoxelShape NORTH_AABB = Block.createCuboidShape(6.0D, 6.0D, 12.0D, 10.0D, 10.0D, 16.0D);

    private static final VoxelShape SOUTH_AABB = Block.createCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 4.0D);

    private static final VoxelShape WEST_AABB = Block.createCuboidShape(12.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    private static final VoxelShape EAST_AABB = Block.createCuboidShape(0.0D, 6.0D, 6.0D, 4.0D, 10.0D, 10.0D);

    private static final VoxelShape DOWN_AABB = Block.createCuboidShape(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    private static final VoxelShape UP_AABB = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D);

    public FastenerBlock(final AbstractBlock.Settings properties) {
        super(properties);
        this.setDefaultState(this.getStateManager()
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TRIGGERED, false)
        );
    }

    @Override
    protected void appendProperties(final StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView worldIn, final BlockPos pos, final ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
            case DOWN:
                return DOWN_AABB;
            case UP:
            default:
                return UP_AABB;
        }
    }

    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return new FastenerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final World level, final BlockState state, final BlockEntityType<T> type) {
        if (level.isClient()) {
            return createTickerHelper(type, FLBlockEntities.FASTENER.get(), FastenerBlockEntity::tickClient);
        }
        return createTickerHelper(type, FLBlockEntities.FASTENER.get(), FastenerBlockEntity::tick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expect, BlockEntityTicker<? super E> ticker) {
        return expect == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
        if (!state.isOf(newState.getBlock())) {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof FastenerBlockEntity) {
                ((CapabilityHelper<?>)entity).getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.dropItems(world, pos));
            }
            super.onStateReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean canPlaceAt(final BlockState state, final WorldView world, final BlockPos pos) {
        final Direction facing = state.get(FACING);
        final BlockPos attachedPos = pos.offset(facing.getOpposite());
        final BlockState attachedState = world.getBlockState(attachedPos);
        return attachedState.isIn(BlockTags.LEAVES) || attachedState.isSideSolidFullSquare(world, attachedPos, facing) || facing == Direction.UP && attachedState.isIn(BlockTags.WALLS);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext context) {
        BlockState result = this.getDefaultState();
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos();
        for (final Direction dir : context.getPlacementDirections()) {
            result = result.with(FACING, dir.getOpposite());
            if (result.canPlaceAt(world, pos)) {
                return result.with(TRIGGERED, world.isReceivingRedstonePower(pos.offset(dir)));
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block blockIn, final BlockPos fromPos, final boolean isMoving) {
        if (state.canPlaceAt(world, pos)) {
            final boolean receivingPower = world.isReceivingRedstonePower(pos);
            final boolean isPowered = state.get(TRIGGERED);
            if (receivingPower && !isPowered) {
                world.createAndScheduleBlockTick(pos, this, 2);
                world.setBlockState(pos, state.with(TRIGGERED, true), 4);
            } else if (!receivingPower && isPowered) {
                world.setBlockState(pos, state.with(TRIGGERED, false), 4);
            }
        } else {
            final BlockEntity entity = world.getBlockEntity(pos);
            dropStacks(state, world, pos, entity);
            world.removeBlock(pos, false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity == null) return super.getComparatorOutput(state, world, pos);
        return ((CapabilityHelper<?>)entity).getCapability(CapabilityHandler.FASTENER_CAP).map(f -> f.getAllConnections().stream()).orElse(Stream.empty())
            .filter(HangingLightsConnection.class::isInstance)
            .map(HangingLightsConnection.class::cast)
            .mapToInt(c -> (int) Math.ceil(c.getJingleProgress() * 15))
            .max().orElse(0);
    }

    public void tick(final BlockState state, final ServerWorld world, final BlockPos pos, final Random random) {
        this.jingle(world, pos);
    }

    private void jingle(final World world, final BlockPos pos) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof FastenerBlockEntity)) {
            return;
        }
        //TODO
        /*
        ((CapabilityHelper<?>)entity).getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> fastener.getAllConnections().stream()
            .filter(HangingLightsConnection.class::isInstance)
            .map(HangingLightsConnection.class::cast)
            .filter(conn -> conn.canCurrentlyPlayAJingle() && conn.isDestination(new BlockFastenerAccessor(fastener.getPos())) && world.getBlockState(fastener.getPos()).getValue(TRIGGERED))
            .findFirst().ifPresent(conn -> ServerEventHandler.tryJingle(world, conn))
        );*/
    }

    public Vec3d getOffset(final Direction facing, final float offset) {
        return getFastenerOffset(facing, offset);
    }

    public static Vec3d getFastenerOffset(final Direction facing, final float offset) {
        double x = offset, y = offset, z = offset;
        switch (facing) {
            case DOWN:
                y += 0.75F;
            case UP:
                x += 0.375F;
                z += 0.375F;
                break;
            case WEST:
                x += 0.75F;
            case EAST:
                z += 0.375F;
                y += 0.375F;
                break;
            case NORTH:
                z += 0.75F;
            case SOUTH:
                x += 0.375F;
                y += 0.375F;
        }
        return new Vec3d(x, y, z);
    }
}
