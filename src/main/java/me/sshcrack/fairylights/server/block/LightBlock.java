package me.sshcrack.fairylights.server.block;

import me.sshcrack.fairylights.server.block.entity.LightBlockEntity;
import me.sshcrack.fairylights.server.item.LightVariant;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class LightBlock extends WallMountedBlock implements BlockEntityProvider {
    public static final BooleanProperty LIT = Properties.LIT;

    private static final VoxelShape MIN_ANCHOR_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

    private final VoxelShape floorShape, eastWallShape, westWallShape, northWallShape, southWallShape, ceilingShape;

    private final LightVariant<?> variant;

    public LightBlock(final AbstractBlock.Settings properties, final LightVariant<?> variant) {
        super(properties);
        this.variant = variant;
        final Box bb = this.variant.getBounds();
        final double w = Math.max(bb.getXsize(), bb.getZsize());
        final double w0 = 0.5D - w * 0.5D;
        final double w1 = 0.5D + w * 0.5D;
        if (variant.isOrientable()) {
            this.floorShape = clampBox(w0, 0.0D, w0, w1, -bb.minY, w1);
            this.eastWallShape = clampBox(0.0D, w0, w0, -bb.minY, w1, w1);
            this.westWallShape = clampBox(1.0D + bb.minY, w0, w0, 1.0D, w1, w1);
            this.southWallShape = clampBox(w0, w0, 0.0D, w1, w1, -bb.minY);
            this.northWallShape = clampBox(w0, w0, 1.0D + bb.minY, w1, w1, 1.0D);
            this.ceilingShape = clampBox(w0, 1.0D + bb.minY, w0, w1, 1.0D, w1);
        } else {
            final double t = 0.125D;
            final double u = 11.0D / 16.0D;
            this.floorShape = clampBox(w0, 0.0D, w0, w1, bb.getYsize() - this.variant.getFloorOffset(), w1);
            this.eastWallShape = clampBox(w0 - t, u + bb.minY, w0, w1 - t, u + bb.maxY, w1);
            this.westWallShape = clampBox(w0 + t, u + bb.minY, w0, w1 + t, u + bb.maxY, w1);
            this.southWallShape = clampBox(w0, u + bb.minY, w0 - t, w1, u + bb.maxY, w1 - t);
            this.northWallShape = clampBox(w0, u  + bb.minY, w0 + t, w1, u + bb.maxY, w1 + t);
            this.ceilingShape = clampBox(w0, 1.0D + bb.minY - 4.0D / 16.0D, w0, w1, 1.0D, w1);
        }
        this.setDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(LIT, true));
    }

    private static VoxelShape clampBox(double x0, double y0, double z0, double x1, double y1, double z1) {
        return Shapes.box(Mth.clamp(x0, 0.0D, 1.0D), Mth.clamp(y0, 0.0D, 1.0D), Mth.clamp(z0, 0.0D, 1.0D),
            Mth.clamp(x1, 0.0D, 1.0D), Mth.clamp(y1, 0.0D, 1.0D), Mth.clamp(z1, 0.0D, 1.0D));
    }

    public LightVariant<?> getVariant() {
        return this.variant;
    }

    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return new LightBlockEntity(pos, state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        final WallMountLocation value = state.get(FACE);
        if (value == WallMountLocation.WALL) {
            final Direction facing = state.get(FACING);
            final BlockPos anchorPos = pos.offset(facing.getOpposite());
            BlockState anchorState = world.getBlockState(anchorPos);
            if (anchorState.isIn(BlockTags.LEAVES)) {
                return true;
            }
            final VoxelShape shape = anchorState.getBlockSupportShape(world, anchorPos);
            return Block.isFaceFull(shape, facing);
        }
        final Direction facing = value == WallMountLocation.FLOOR ? Direction.DOWN : Direction.UP;
        final BlockPos anchorPos = pos.offset(facing);
        BlockState anchorState = world.getBlockState(anchorPos);
        if (anchorState.isIn(BlockTags.LEAVES)) {
            return true;
        }
        final VoxelShape shape = anchorState.getBlockSupportShape(world, anchorPos);
        return !Shapes.joinIsNotEmpty(shape.getFaceShape(facing.getOpposite()), MIN_ANCHOR_SHAPE, BooleanOp.ONLY_SECOND);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext context) {
        for (final Direction dir : context.getPlacementDirections()) {
            final BlockState state;
            if (dir.getAxis() == Direction.Axis.Y) {
                state = this.getDefaultState()
                    .with(FACE, dir == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
                    .with(FACING, context.getHorizontalDirection().getOpposite());
            } else {
                state = this.getDefaultState()
                    .setValue(FACE, WallMountLocation.WALL)
                    .setValue(FACING, dir.getOpposite());
            }
            if (state.canPlaceAt(context.getWorld(), context.getClickedPos())) {
                return state;
            }
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof LightBlockEntity) {
            final ItemStack lightItem = stack.copy();
            lightItem.setCount(1);
            ((LightBlockEntity) entity).setItemStack(lightItem);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> dropStack(final BlockState state, final LootContext.Builder builder) {
        final BlockEntity entity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (entity instanceof LightBlockEntity) {
            return Collections.singletonList(((LightBlockEntity) entity).getLight().getItem().copy());
        }
        return Collections.emptyList();
    }

    @Override
    public InteractionResult use(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).interact(world, pos, state, player, hand, hit);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rng) {
        super.animateTick(state, world, pos, rng);
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).animateTick();
        }
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter world, final BlockPos pos, final CollisionContext context) {
        switch (state.getValue(FACE)) {
            default:
            case FLOOR:
                return this.floorShape;
            case WALL:
                switch (state.getValue(FACING)) {
                    default:
                    case EAST:
                        return this.eastWallShape;
                    case WEST:
                        return this.westWallShape;
                    case SOUTH:
                        return this.southWallShape;
                    case NORTH:
                        return this.northWallShape;
                }
            case CEILING:
                return this.ceilingShape;
        }
    }

    @Override
    public ItemStack getCloneItemStack(final BlockState state, final HitResult target, final BlockGetter world, final BlockPos pos, final Player player) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof LightBlockEntity) {
            return ((LightBlockEntity) entity).getLight().getItem().copy();
        }
        final ItemStack stack = new ItemStack(this);
        DyeableItem.setColor(stack, DyeColor.YELLOW);
        return stack;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, LIT);
    }
}
