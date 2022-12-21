package me.sshcrack.fairylights.server.block.entity;

import me.sshcrack.fairylights.server.block.LightBlock;
import me.sshcrack.fairylights.server.feature.light.Light;
import me.sshcrack.fairylights.server.item.LightVariant;
import me.sshcrack.fairylights.server.item.SimpleLightVariant;
import me.sshcrack.fairylights.server.sound.FLSounds;
import me.sshcrack.fairylights.util.FLMth;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import me.sshcrack.fairylights.util.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightBlockEntity extends BlockEntity {
    private Light<?> light;

    private boolean on = true;

    public LightBlockEntity(BlockPos pos, BlockState state) {
        super(FLBlockEntities.LIGHT.get(), pos, state);
        this.light = new Light<>(0, Vec3d.ZERO, 0.0F, 0.0F, ItemStack.EMPTY, SimpleLightVariant.FAIRY_LIGHT, 0.0F);
    }

    public Light<?> getLight() {
        return this.light;
    }

    public void setItemStack(final ItemStack stack) {
        this.light = new Light<>(0, Vec3d.ZERO, 0.0F, 0.0F, stack, LightVariant.get((CapabilityHelper<?>)(Object)stack).orElse(SimpleLightVariant.FAIRY_LIGHT), 0.0F);
        this.markDirty();
    }

    private void setOn(final boolean on) {
        this.on = on;
        this.light.power(on, true);
        this.markDirty();
    }

    public void interact(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        this.setOn(!this.on);
        world.setBlockState(pos, state.with(LightBlock.LIT, this.on));
        final SoundEvent lightSnd;
        final float pitch;
        if (this.on) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON;
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF;
            pitch = 0.5F;
        }
        this.world.playSound(null, pos, lightSnd, SoundCategory.BLOCKS, 1.0F, pitch);
    }

    public void animateTick() {
        final BlockState state = this.getCachedState();
        final WallMountLocation face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.FACING).asRotation();
        final MatrixStack matrix = new MatrixStack();
        matrix.translate(0.5F, 0.5F, 0.5F);
        matrix.rotate((float) Math.toRadians(180.0F - rotation), 0.0F, 1.0F, 0.0F);
        if (this.light.getVariant().isOrientable()) {
            if (face == WallMountLocation.WALL) {
                matrix.rotate(FLMth.HALF_PI, 1.0F, 0.0F, 0.0F);
            } else if (face == WallMountLocation.FLOOR) {
                matrix.rotate(-FLMth.PI, 1.0F, 0.0F, 0.0F);
            }
            matrix.translate(0.0F, 0.5F, 0.0F);
        } else {
            if (face == WallMountLocation.CEILING) {
                matrix.translate(0.0F, 0.25F, 0.0F);
            } else if (face == WallMountLocation.WALL) {
                matrix.translate(0.0F, 3.0F / 16.0F, 0.125F);
            } else {
                matrix.translate(0.0F, -(float) this.light.getVariant().getBounds().minY - 0.5F, 0.0F);
            }
        }
        this.light.getBehavior().animateTick(this.world, Vec3d.of(this.pos).add(matrix.transform(Vec3d.ZERO)), this.light);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.put("item", this.light.getItem().writeNbt(new NbtCompound()));
        compound.putBoolean("on", this.on);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.setItemStack(ItemStack.fromNbt(compound.getCompound("item")));
        this.setOn(compound.getBoolean("on"));
    }
}
