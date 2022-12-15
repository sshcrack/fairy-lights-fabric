package me.sshcrack.fairylights.client;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.collision.Collidable;
import me.sshcrack.fairylights.server.collision.Intersection;
import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.HangingLightsConnection;
import me.sshcrack.fairylights.server.connection.PlayerAction;
import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import me.sshcrack.fairylights.server.fastener.CollectFastenersEvent;
import me.sshcrack.fairylights.server.fastener.Fastener;
import me.sshcrack.fairylights.server.fastener.FastenerType;
import me.sshcrack.fairylights.server.jingle.Jingle;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.forge.events.RenderHighlightEvent;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class ClientEventHandler {
    private static final float HIGHLIGHT_ALPHA = 0.4F;

    @Nullable
    public static Connection getHitConnection() {
        final net.minecraft.util.hit.HitResult result = MinecraftClient.getInstance().crosshairTarget;
        if (result instanceof EntityHitResult) {
            final Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof HitConnection) {
                return ((HitConnection) entity).result.connection;
            }
        }
        return null;
    }

    public void renderOverlay(final ForgeGui gui, final MatrixStack poseStack, final float partialTick, final int screenWidth, final int screenHeight) {
        final Connection conn = getHitConnection();
        if (!(conn instanceof HangingLightsConnection)) {
            return;
        }
        final Jingle jingle = ((HangingLightsConnection) conn).getPlayingJingle();
        if (jingle == null) {
            return;
        }
        final List<String> lines = List.of(
                "Song: " + jingle.getTitle(),
                "Artist: " + jingle.getArtist());
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (!Strings.isNullOrEmpty(line)) {
                final int lineHeight = gui.getFont().lineHeight;
                final int textWidth = gui.getFont().width(line);
                final int y = 2 + lineHeight * i;
                GuiComponent.fill(poseStack, 1, y - 1, 2 + textWidth + 1, y + lineHeight - 1, 0x90505050);
                gui.getFont().draw(poseStack, line, 2, y, 0xe0e0e0);
            }
        }
    }

    public static void updateHitConnection() {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final Entity viewer = mc.getCameraEntity();
        if (mc.crosshairTarget != null && mc.world != null && viewer != null) {
            final HitResult result = getHitConnection(mc.world, viewer);
            if (result != null) {
                final Vec3d eyes = viewer.getCameraPosVec(1.0F);
                if (result.intersection.getResult().distanceTo(eyes) < mc.crosshairTarget.getPos().distanceTo(eyes)) {
                    mc.crosshairTarget = new EntityHitResult(new HitConnection(mc.world, result));
                    mc.targetedEntity = null;
                }
            }
        }
    }

    @Nullable
    private static HitResult getHitConnection(final World world, final Entity viewer) {
        final Box bounds = new Box(viewer.getBlockPos()).expand(Connection.MAX_LENGTH + 1.0D);
        final Set<Fastener<?>> fasteners = collectFasteners(world, bounds);
        return getHitConnection(viewer, bounds, fasteners);
    }

    private static Set<Fastener<?>> collectFasteners(final World world, final Box bounds) {
        final Set<Fastener<?>> fasteners = Sets.newLinkedHashSet();
        final CollectFastenersEvent event = new CollectFastenersEvent(world, bounds, fasteners);
        world.getEntitiesByClass(FenceFastenerEntity.class, bounds, EntityPredicates.EXCEPT_SPECTATOR)
                //TODO fix capability things here
                .forEach(e -> event.accept(e));
        final int minX = MathHelper.floor(bounds.minX / 16.0D);
        final int maxX = MathHelper.ceil(bounds.maxX / 16.0D);
        final int minZ = MathHelper.floor(bounds.minZ / 16.0D);
        final int maxZ = MathHelper.ceil(bounds.maxZ / 16.0D);
        final ChunkManager provider = world.getChunkManager();
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                final Chunk chunk = provider.getChunk(x, z, ChunkStatus.FULL, false);
                if (chunk != null) {
                    event.accept(chunk);
                }
            }
        }
        FairyLightsMod.EVENT_BUS.fireEvent(event);
        return fasteners;
    }

    @Nullable
    private static HitResult getHitConnection(final Entity viewer, final Box bounds, final Set<Fastener<?>> fasteners) {
        if (fasteners.isEmpty()) {
            return null;
        }
        final Vec3d origin = viewer.getCameraPosVec(1);
        final Vec3d look = viewer.getEyePos();
        final double reach = MinecraftClient.getInstance().interactionManager.getReachDistance();
        final Vec3d end = origin.add(look.x * reach, look.y * reach, look.z * reach);
        Connection found = null;
        Intersection rayTrace = null;
        double distance = Double.MAX_VALUE;
        for (final Fastener<?> fastener : fasteners) {
            for (final Connection connection : fastener.getOwnConnections()) {
                if (connection.getDestination().getType() == FastenerType.PLAYER) {
                    continue;
                }
                final Collidable collision = connection.getCollision();
                final Intersection result = collision.intersect(origin, end);
                if (result != null) {
                    final double dist = result.getResult().distanceTo(origin);
                    if (dist < distance) {
                        distance = dist;
                        found = connection;
                        rayTrace = result;
                    }
                }
            }
        }
        if (found == null) {
            return null;
        }
        return new HitResult(found, rayTrace);
    }

    @SubscribeEvent
    public void drawBlockHighlight(final RenderHighlightEvent.Entity event) {
        final Entity entity = event.getTarget().getEntity();
        final Vec3d pos = event.getCamera().getPos();
        final VertexConsumerProvider buf = event.getMultiBufferSource();
        if (entity instanceof FenceFastenerEntity) {
            this.drawFenceFastenerHighlight((FenceFastenerEntity) entity, event.getMatrixStack(), buf.getBuffer(RenderLayer.LINES), event.getPartialTick(), pos.x, pos.y, pos.z);
        } else if (entity instanceof final HitConnection hit) {
            if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
                final MatrixStack matrix = event.getMatrixStack();
                matrix.push();
                final Vec3d p = hit.result.connection.getFastener().getConnectionPoint();
                matrix.translate(p.x - pos.x, p.y - pos.y, p.z - pos.z);
                this.renderHighlight(hit.result.connection, matrix, buf.getBuffer(RenderLayer.LINES));
                matrix.pop();
            } else {
                final Box bb = hit.result.intersection.getHitBox().offset(-pos.x, -pos.y, -pos.z).expand(0.002D);
                WorldRenderer.drawBox(event.getMatrixStack(), buf.getBuffer(RenderLayer.LINES), bb, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
            }
        }
    }

    private void drawFenceFastenerHighlight(final FenceFastenerEntity fence, final MatrixStack matrix, final VertexConsumer buf, final float delta, final double dx, final double dy, final double dz) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        // Check if the server will allow interaction
        if (player != null && (player.canSee(fence) || player.squaredDistanceTo(fence) <= 9.0D)) {
            final Box selection = fence.getBoundingBox().offset(-dx, -dy, -dz).expand(0.002D);
            WorldRenderer.drawBox(matrix, buf, selection, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
        }
    }

    private void renderHighlight(final Connection connection, final MatrixStack matrix, final VertexConsumer buf) {
        final Curve cat = connection.getCatenary();
        if (cat == null) {
            return;
        }
        final Vec3f p = new Vec3f();
        final Vec3f v1 = new Vec3f();
        final Vec3f v2 = new Vec3f();
        final LineBuilder builder = new LineBuilder(matrix, buf);
        final float r = connection.getRadius() + 0.01F;
        for (int edge = 0; edge < 4; edge++) {
            p.set(cat.getX(0), cat.getY(0), cat.getZ(0));
            v1.set(cat.getDx(0), cat.getDy(0), cat.getDz(0));
            v1.normalize();
            v2.set(-v1.getX(), -v1.getY(), -v1.getZ());
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(builder, (n + 1) / 2 % 4, p, v1, v2, r);
            }
            this.addVertex(builder, edge, p, v1, v2, r);
            for (int i = 1; i < cat.getCount() - 1; i++) {
                p.set(cat.getX(i), cat.getY(i), cat.getZ(i));
                v2.set(-cat.getDx(i), -cat.getDy(i), -cat.getDz(i));
                v2.normalize();
                this.addVertex(builder, edge, p, v1, v2, r);
                this.addVertex(builder, edge, p, v1, v2, r);
                v1.set(-v2.getX(), -v2.getY(), -v2.getZ());
            }
            p.set(cat.getX(), cat.getY(), cat.getZ());
            v2.set(-v1.getX(), -v1.getY(), -v1.getZ());
            this.addVertex(builder, edge, p, v1, v2, r);
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(builder, (n + 1) / 2 % 4, p, v1, v2, r);
            }
        }
    }

    static class LineBuilder {
        final MatrixStack matrix;
        final VertexConsumer buf;
        Vec3f last;

        LineBuilder(MatrixStack matrix, VertexConsumer buf) {
            this.matrix = matrix;
            this.buf = buf;
        }

        void accept(Vec3f pos) {
            if (this.last == null) {
                this.last = pos;
            } else {
                Vec3f n = pos.copy();
                n.subtract(this.last);
                n.normalize();
                n.transform(this.matrix.peek().getNormalMatrix());
                this.buf.vertex(this.matrix.peek().getPositionMatrix(), this.last.getX(), this.last.getY(), this.last.getZ())
                        .color(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA)
                        .normal(n.getX(), n.getY(), n.getZ())
                        .next();
                this.buf.vertex(this.matrix.peek().getPositionMatrix(), pos.getX(), pos.getY(), pos.getZ())
                        .color(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA)
                        .normal(n.getX(), n.getY(), n.getZ())
                        .next();
                this.last = null;
            }
        }
    }

    private void addVertex(final LineBuilder builder, final int edge, final Vec3f p, final Vec3f v1, final Vec3f v2, final float r) {
        builder.accept(this.get(edge, p, v1, v2, r));
    }

    private Vec3f get(final int edge, final Vec3f p, final Vec3f v1, final Vec3f v2, final float r) {
        final Vec3f up = new Vec3f();
        final Vec3f side = new Vec3f();
        // if collinear
        if (v1.dot(v2) < -(1.0F - 1.0e-2F)) {
            final float h = MathHelper.sqrt(v1.getX() * v1.getX() + v1.getZ() * v1.getZ());
            // if vertical
            if (h < 1.0e-2F) {
                up.set(-1.0F, 0.0F, 0.0F);
            } else {
                up.set(-v1.getX() / h * -v1.getY(), -h, -v1.getZ() / h * -v1.getY());
            }
        } else {
            up.set(v2.getX(), v2.getY(), v2.getZ());
            up.lerp(v1, 0.5F);
        }
        up.normalize();
        side.set(v1.getX(), v1.getY(), v1.getZ());
        side.cross(up);
        side.normalize();
        float vv1 = edge == 0 || edge == 3 ? -r : r;
        float vv2 = edge < 2 ? -r : r;
        side.multiplyComponentwise(vv1,vv1,vv1);
        up.multiplyComponentwise(vv2,vv2,vv2);
        up.add(side);
        up.add(p);
        return up;
    }

    static class HitConnection extends Entity {
        final ClientEventHandler.HitResult result;

        HitConnection(final World world, final ClientEventHandler.HitResult result) {
            super(EntityType.ITEM, world);
            this.setId(-1);
            this.result = result;
            Vec3d pos = result.intersection.getResult();
            this.setPos(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public boolean damage(final DamageSource source, final float amount) {
            if (source.getSource() == MinecraftClient.getInstance().player) {
                this.processAction(PlayerAction.ATTACK);
                return true;
            }
            return false;
        }

        @Override
        public ActionResult interact(final PlayerEntity player, final Hand hand) {
            if (player == MinecraftClient.getInstance().player) {
                this.processAction(PlayerAction.INTERACT);
                return ActionResult.SUCCESS;
            }
            return super.interact(player, hand);
        }

        private void processAction(final PlayerAction action) {
            this.result.connection.processClientAction(MinecraftClient.getInstance().player, action, this.result.intersection);
        }

        @Override
        public ItemStack getPickBlockStack() {
            return this.result.connection.getItemStack();
        }

        @Override
        protected void initDataTracker() {
        }

        @Override
        protected void writeCustomDataToNbt(final NbtCompound compound) {
        }

        @Override
        protected void readCustomDataFromNbt(final NbtCompound compound) {
        }

        @Override
        public Packet<?> createSpawnPacket() {
            return new Packet<>() {
                @Override
                public void write(final PacketByteBuf buf) {

                }

                @Override
                public void apply(final PacketListener listener) {

                }
            };
        }
    }

    private static final class HitResult {
        private final Connection connection;

        private final Intersection intersection;

        public HitResult(final Connection connection, final Intersection intersection) {
            this.connection = connection;
            this.intersection = intersection;
        }
    }
}
