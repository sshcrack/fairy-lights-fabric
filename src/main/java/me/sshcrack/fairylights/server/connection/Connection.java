package me.sshcrack.fairylights.server.connection;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.collision.Collidable;
import me.sshcrack.fairylights.server.collision.CollidableList;
import me.sshcrack.fairylights.server.collision.FeatureCollisionTree;
import me.sshcrack.fairylights.server.collision.Intersection;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.fastener.FastenerType;
import me.sshcrack.fairylights.server.fastener.FenceFastener;
import me.sshcrack.fairylights.server.fastener.accessor.FastenerAccessor;
import me.sshcrack.fairylights.server.feature.Feature;
import me.sshcrack.fairylights.server.feature.FeatureType;
import me.sshcrack.fairylights.server.item.ConnectionItem;
import me.sshcrack.fairylights.server.sound.FLSounds;
import me.sshcrack.fairylights.util.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Connection implements NBTSerializable {
    public static final int MAX_LENGTH = 32;

    public static final double PULL_RANGE = 5;

    public static final FeatureType CORD_FEATURE = FeatureType.register("cord");

    private static final CubicBezier SLACK_CURVE = new CubicBezier(0.495F, 0.505F, 0.495F, 0.505F);

    private static final float MAX_SLACK = 3;

    private final ConnectionType<?> type;

    protected final Fastener<?> fastener;

    private final UUID uuid;

    private FastenerAccessor destination;

    @Nullable
    private FastenerAccessor prevDestination;

    protected World world;

    @Nullable
    private Curve catenary;

    @Nullable
    protected Curve prevCatenary;

    protected float slack = 1;

    private Collidable collision = Collidable.empty();

    private boolean updateCatenary;

    private int prevStretchStage;

    private boolean removed;

    private boolean drop;

    public Connection(final ConnectionType<?> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        this.type = type;
        this.world = world;
        this.fastener = fastener;
        this.uuid = uuid;
        this.computeCatenary();
    }

    public ConnectionType<?> getType() {
        return this.type;
    }

    @Nullable
    public final Curve getCatenary() {
        return this.catenary;
    }

    @Nullable
    public final Curve getPrevCatenary() {
        return this.prevCatenary == null ? this.catenary : this.prevCatenary;
    }

    public void setWorld(final World world) {
        this.world = world;
    }

    public final World getWorld() {
        return this.world;
    }

    public final Collidable getCollision() {
        return this.collision;
    }

    public final Fastener<?> getFastener() {
        return this.fastener;
    }

    public final UUID getUUID() {
        return this.uuid;
    }

    public final void setDestination(final Fastener<?> destination) {
        this.prevDestination = this.destination;
        this.destination = destination.createAccessor();
        this.computeCatenary();
    }

    public final FastenerAccessor getDestination() {
        return this.destination;
    }

    public boolean isDestination(final FastenerAccessor location) {
        return this.destination.equals(location);
    }

    public void setDrop() {
        this.drop = true;
    }

    public void noDrop() {
        this.drop = false;
    }

    public boolean shouldDrop() {
        return this.drop;
    }

    public ItemStack getItemStack() {
        final ItemStack stack = new ItemStack(this.getType().getItem());
        final NbtCompound tagCompound = this.serializeLogic();
        if (!tagCompound.isEmpty()) {
            stack.setNbt(tagCompound);
        }
        return stack;
    }

    public float getRadius() {
        return 0.0625F;
    }

    public final boolean isDynamic() {
        return this.fastener.isMoving() || this.destination.get(this.world, false).filter(Fastener::isMoving).isPresent();
    }

    public final boolean isModifiable(final PlayerEntity player) {
        return this.world.canPlayerModifyAt(player, this.fastener.getPos());
    }

    public final void remove() {
        if (!this.removed) {
            this.removed = true;
            this.onRemove();
        }
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    public void computeCatenary() {
        this.updateCatenary = true;
    }

    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        FairyLightsMod.NETWORK.sendToServer(new InteractionConnectionMessage(this, action, intersection));
    }

    public void disconnect(final PlayerEntity player, final Vec3d hit) {
        this.destination.get(this.world).ifPresent(f -> this.disconnect(f, hit));
    }

    private void disconnect(final Fastener<?> destinationFastener, final Vec3d hit) {
        this.fastener.removeConnection(this);
        destinationFastener.removeConnection(this.uuid);
        if (this.shouldDrop()) {
            final ItemStack stack = this.getItemStack();
            final ItemEntity item = new ItemEntity(this.world, hit.x, hit.y, hit.z, stack);
            final float scale = 0.05F;
            item.setVelocity(
                    this.world.random.nextGaussian() * scale,
                    this.world.random.nextGaussian() * scale + 0.2F,
                    this.world.random.nextGaussian() * scale
            );
            this.world.spawnEntity(item);
        }
        this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_DISCONNECT, SoundCategory.BLOCKS, 1, 1);
    }

    public boolean reconnect(final Fastener<?> destination) {
        return this.fastener.reconnect(this.world, this, destination);
    }

    public boolean interact(final PlayerEntity player, final Vec3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        final Item item = heldStack.getItem();
        if (item instanceof ConnectionItem && !this.matches(heldStack)) {
            return this.replace(player, hit, heldStack);
        } else if (heldStack.isOf(Items.STRING)) {
            return this.slacken(hit, heldStack, 0.2F);
        } else if (heldStack.isOf(Items.STICK)) {
            return this.slacken(hit, heldStack, -0.2F);
        }
        return false;
    }

    public boolean matches(final ItemStack stack) {
        if (this.getType().getItem().equals(stack.getItem())) {
            final NbtCompound tag = stack.getNbt();
            return tag == null || Utils.impliesNbt(this.serializeLogic(), tag);
        }
        return false;
    }

    private boolean replace(final PlayerEntity player, final Vec3d hit, final ItemStack heldStack) {
        return this.destination.get(this.world).map(dest -> {
            this.fastener.removeConnection(this);
            dest.removeConnection(this.uuid);
            if (this.shouldDrop()) {
                //player.getInventory().insertStack(this.getItemStack());
            }
            final NbtCompound data = heldStack.getNbt();
            final ConnectionType<? extends Connection> type = ((ConnectionItem) heldStack.getItem()).getConnectionType();
            final Connection conn = this.fastener.connect(this.world, dest, type, data == null ? new NbtCompound() : data, true);
            conn.slack = this.slack;
            conn.onConnect(player.getWorld(), player, heldStack);
            heldStack.decrement(1);
            this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_CONNECT, SoundCategory.BLOCKS, 1, 1);
            return true;
        }).orElse(false);
    }

    private boolean slacken(final Vec3d hit, final ItemStack heldStack, final float amount) {
        if (this.slack <= 0 && amount < 0 || this.slack >= MAX_SLACK && amount > 0) {
            return true;
        }
        this.slack = MathHelper.clamp(this.slack + amount, 0, MAX_SLACK);
        if (this.slack < 1e-2F) {
            this.slack = 0;
        }
        this.computeCatenary();
        this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_STRETCH, SoundCategory.BLOCKS, 1, 0.8F + (MAX_SLACK - this.slack) * 0.4F);
        return true;
    }

    public void onConnect(final World world, final PlayerEntity user, final ItemStack heldStack) {
    }

    protected void onRemove() {
    }

    protected void onUpdate() {
    }

    protected void onCalculateCatenary(final boolean relocated) {
    }

    public final boolean update(final Vec3d from) {
        this.prevCatenary = this.catenary;
        final boolean changed = this.destination.get(this.world, false).map(dest -> {
            final Vec3d point = dest.getConnectionPoint();
            final boolean c = this.updateCatenary(from, dest, point);
            this.onUpdate();
            final double dist = point.distanceTo(from);
            final double pull = dist - MAX_LENGTH + PULL_RANGE;
            if (pull > 0) {
                final int stage = (int) (pull + 0.1F);
                if (stage > this.prevStretchStage) {
                    this.world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_STRETCH, SoundCategory.BLOCKS, 0.25F, 0.5F + stage / 8F);
                }
                this.prevStretchStage = stage;
            }
            if (dist > MAX_LENGTH + PULL_RANGE) {
                this.world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_SNAP, SoundCategory.BLOCKS, 0.75F, 0.8F + this.world.random.nextFloat() * 0.3F);
                this.remove();
            } else if (dest.isMoving()) {
                dest.resistSnap(from);
            }
            return c;
        }).orElse(false);
        if (this.destination.isGone(this.world)) {
            this.remove();
        }
        return changed;
    }

    private boolean updateCatenary(final Vec3d from, final Fastener<?> dest, final Vec3d point) {
        if (this.updateCatenary || this.isDynamic()) {
            final Vec3d vec = point.subtract(from);
            if (vec.length() > 1e-6) {
                final Direction facing = this.fastener.getFacing();
                if (this.fastener instanceof FenceFastener && dest instanceof FenceFastener && vec.horizontalLength() < 1e-2) {
                    this.catenary = this.verticalHelix(vec);
                } else {
                    this.catenary = Catenary.from(vec, facing.getAxis() == Direction.Axis.Y ? 0.0F : (float) Math.toRadians(90.0F + facing.asRotation()), SLACK_CURVE, this.slack);
                }
                this.onCalculateCatenary(!this.destination.equals(this.prevDestination));
                final CollidableList.Builder bob = new CollidableList.Builder();
                this.addCollision(bob, from);
                this.collision = bob.build();
            }
            this.updateCatenary = false;
            this.prevDestination = this.destination;
            return true;
        }
        return false;
    }

    private Curve verticalHelix(final Vec3d vec) {
        final float length = (float) vec.length();
        final float height = (float) vec.y;
        final float stepSize = 0.25F;
        final float loopsPerBlock = 1.0F;
        final float radius = 0.33F;
        final int steps = (int) (FLMth.TWO_PI * radius * loopsPerBlock * length / stepSize);
        final float rad = -FLMth.TWO_PI * (loopsPerBlock * length);
        final float[] x = new float[steps];
        final float[] y = new float[steps];
        final float[] z = new float[steps];
        float helixLength = 0.0F;
        for (int i = 0; i < steps; i++) {
            float t = (float) i / (steps - 1);
            x[i] = radius * MathHelper.cos(t * rad);
            y[i] = t * height;
            z[i] = radius * MathHelper.sin(t * rad);
            if (i > 0) {
                helixLength += MathHelper.sqrt(
                        MathHelper.square(x[i] - x[i - 1]) +
                                MathHelper.square(y[i] - y[i - 1]) +
                                MathHelper.square(z[i] - z[i - 1]));
            }
        }
        return new Curve3d(steps, x, y, z, helixLength);
    }

    public void addCollision(final CollidableList.Builder collision, final Vec3d origin) {
        if (this.catenary == null) {
            return;
        }
        final int count = this.catenary.getCount();
        if (count <= 2) {
            return;
        }
        final float r = this.getRadius();
        final Catenary.SegmentIterator it = this.catenary.iterator();
        final Box[] bounds = new Box[count - 1];
        int index = 0;
        while (it.next()) {
            final float x0 = it.getX(0.0F);
            final float y0 = it.getY(0.0F);
            final float z0 = it.getZ(0.0F);
            final float x1 = it.getX(1.0F);
            final float y1 = it.getY(1.0F);
            final float z1 = it.getZ(1.0F);
            bounds[index++] = new Box(
                    origin.x + x0, origin.y + y0, origin.z + z0,
                    origin.x + x1, origin.y + y1, origin.z + z1
            ).expand(r);
        }
        collision.add(FeatureCollisionTree.build(CORD_FEATURE, i -> Segment.INSTANCE, i -> bounds[i], 1, bounds.length - 2));
    }

    public void deserialize(final Fastener<?> destination, final NbtCompound compound, final boolean drop) {
        this.destination = destination.createAccessor();
        this.drop = drop;
        this.deserializeLogic(compound);
    }

    @Override
    public NbtCompound serialize() {
        final NbtCompound compound = new NbtCompound();
        compound.put("destination", FastenerType.serialize(this.destination));
        compound.put("logic", this.serializeLogic());
        compound.putFloat("slack", this.slack);
        if (!this.drop) compound.putBoolean("drop", false);
        return compound;
    }

    @Override
    public void deserialize(final NbtCompound compound) {
        this.destination = FastenerType.deserialize(compound.getCompound("destination"));
        this.deserializeLogic(compound.getCompound("logic"));
        this.slack = compound.contains("slack", NbtElement.NUMBER_TYPE) ? compound.getFloat("slack") : 1;
        this.drop = !compound.contains("drop", NbtElement.NUMBER_TYPE) || compound.getBoolean("drop");
        this.updateCatenary = true;
    }

    public NbtCompound serializeLogic() {
        return new NbtCompound();
    }

    public void deserializeLogic(final NbtCompound compound) {
    }

    static class Segment implements Feature {
        static final Segment INSTANCE = new Segment();

        @Override
        public int getId() {
            return 0;
        }
    }
}
