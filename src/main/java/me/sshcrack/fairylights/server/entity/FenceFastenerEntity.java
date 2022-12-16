package me.sshcrack.fairylights.server.entity;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.sshcrack.fairylights.server.ServerProxy;
import me.sshcrack.fairylights.server.block.FLBlocks;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.item.ConnectionItem;
import me.sshcrack.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public final class FenceFastenerEntity extends AbstractDecorationEntity/* implements IEntityAdditionalSpawnData*/ {
    private int surfaceCheckTime;

    public FenceFastenerEntity(final EntityType<? extends FenceFastenerEntity> type, final World world) {
        super(type, world);
    }

    public FenceFastenerEntity(final World world) {
        this(FLEntities.FASTENER.get(), world);
    }

    public FenceFastenerEntity(final World world, final BlockPos pos) {
        this(world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());

    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    public float getEyeHeight(final EntityPose pose, final EntityDimensions size) {
        /*
         * Because this entity is inside of a block when
         * EntityLivingBase#canEntityBeSeen performs its
         * raytracing it will always return false during
         * NetHandlerPlayServer#processUseEntity, making
         * the player reach distance be limited at three
         * blocks as opposed to the standard six blocks.
         * EntityLivingBase#canEntityBeSeen will add the
         * value given by getEyeHeight to the y position
         * of the entity to calculate the end point from
         * which to raytrace to. Returning one lets most
         * interactions with a player succeed, typically
         * for breaking the connection or creating a new
         * connection. I hope you enjoy my line lengths.
         */
        return 1;
    }

    @Override
    public boolean shouldRender(final double distance) {
        return distance < 4096;
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean canStayAttached() {
        return !this.world.isChunkLoaded(this.getBlockPos()) || ConnectionItem.isFence(this.world.getBlockState(this.getBlockPos()));
    }

    @Override
    public void remove(final RemovalReason reason) {
        this.getFastener().ifPresent(Fastener::remove);
        super.remove(reason);
    }

    // Copy from super but remove() moved to after onBroken()
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isClient() && this.isAlive()) {
            this.scheduleVelocityUpdate();
            this.onBreak(source.getSource());
            this.remove(RemovalReason.KILLED);
        }
        return true;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public void onBreak(@Nullable final Entity breaker) {
        this.getFastener().ifPresent(fastener -> fastener.dropItems(this.world, this.getBlockPos()));
        if (breaker != null) {
            this.world.syncWorldEvent(2001, this.getBlockPos(), Block.getRawIdFromState(FLBlocks.FASTENER.get().getDefaultState()));
        }
    }

    @Override
    public void onPlace() {
        final BlockSoundGroup sound = FLBlocks.FASTENER.get().getSoundGroup(FLBlocks.FASTENER.get().getDefaultState());
        this.playSound(sound.getPlaceSound(), (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }

    @Override
    public void setPosition(final double x, final double y, final double z) {
        super.setPosition(MathHelper.floor(x) + 0.5, MathHelper.floor(y) + 0.5, MathHelper.floor(z) + 0.5);
    }

    @Override
    public void setFacing(final Direction facing) {}

    @Override
    protected void updateAttachmentPosition() {
        BlockPos pos = getBlockPos();
        final double posX = pos.getX() + 0.5;
        final double posY = pos.getY() + 0.5;
        final double posZ = pos.getZ() + 0.5;
        this.setPos(posX, posY, posZ);
        final float w = 3 / 16F;
        final float h = 3 / 16F;
        this.setBoundingBox(new Box(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().expand(1)).orElseGet(super::getVisibilityBoundingBox);
    }

    @Override
    public void tick() {
        this.getFastener().ifPresent(fastener -> {
            if (!this.world.isClient() && (fastener.hasNoConnections() || this.checkSurface())) {
                this.dropItem(null);
                this.remove(RemovalReason.DISCARDED);
            } else if (fastener.update() && !this.world.isClient()) {
                final UpdateEntityFastenerMessage msg = new UpdateEntityFastenerMessage(this, fastener.serializeNBT());
                ServerProxy.sendToPlayersWatchingEntity(msg, this);
            }
        });
    }

    private boolean checkSurface() {
        if (this.surfaceCheckTime++ == 100) {
            this.surfaceCheckTime = 0;
            return !this.canStayAttached();
        }
        return false;
    }

    @Override
    public ActionResult interact(final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof ConnectionItem) {
            if (this.world.isClient()) {
                player.swingHand(hand);
            } else {
                this.getFastener().ifPresent(fastener -> ((ConnectionItem) stack.getItem()).connect(stack, player, this.world, fastener));
            }
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void writeCustomDataToNbt(final NbtCompound compound) {
        compound.put("pos", NbtHelper.fromBlockPos(this.getBlockPos()));
    }

    @Override
    public void readCustomDataFromNbt(final NbtCompound compound) {
        BlockPos pos = NbtHelper.toBlockPos(compound.getCompound("pos"));
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void writeSpawnPacket(final PacketByteBuf buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                NbtIo.write(fastener.serializeNBT(), new ByteBufOutputStream(buf));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void readSpawnData(final PacketByteBuf buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                fastener.deserializeNBT(NbtIo.read(new ByteBufInputStream(buf), new NbtTagSizeTracker(0x200000)));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    private Optional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }

    public static FenceFastenerEntity create(final World world, final BlockPos fence) {
        final FenceFastenerEntity fastener = new FenceFastenerEntity(world, fence);
        //fastener.forceSpawn = true;
        world.spawnEntity(fastener);
        fastener.onPlace();
        return fastener;
    }

    @Nullable
    public static FenceFastenerEntity find(final World world, final BlockPos pos) {
        final AbstractDecorationEntity entity = findHanging(world, pos);
        if (entity instanceof FenceFastenerEntity) {
            return (FenceFastenerEntity) entity;
        }
        return null;
    }

    @Nullable
    public static AbstractDecorationEntity findHanging(final World world, final BlockPos pos) {
        for (final AbstractDecorationEntity e : world.getEntitiesByClass(AbstractDecorationEntity.class, new Box(pos).expand(2), EntityPredicates.EXCEPT_SPECTATOR)) {
            if (e.getPos().equals(pos)) {
                return e;
            }
        }
        return null;
    }
}
