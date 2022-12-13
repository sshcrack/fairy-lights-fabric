package me.sshcrack.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.client.model.light.BowModel;
import me.paulf.fairylights.server.connection.*;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FenceFastener;
import me.sshcrack.fairylights.client.model.light.BowModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.VertexConsumerProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.function.Function;

public class FastenerRenderer {
    private final HangingLightsRenderer hangingLights;
    private final GarlandVineRenderer garland;
    private final GarlandTinselRenderer tinsel;
    private final PennantBuntingRenderer pennants;
    private final LetterBuntingRenderer letters;
    private final BowModel bow;

    public FastenerRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        this.hangingLights = new HangingLightsRenderer(baker);
        this.garland = new GarlandVineRenderer(baker);
        this.tinsel = new GarlandTinselRenderer(baker);
        this.pennants = new PennantBuntingRenderer(baker);
        this.letters = new LetterBuntingRenderer(baker);
        this.bow = new BowModel(baker.apply(FLModelLayers.BOW));
    }

    public void render(final Fastener<?> fastener, final float delta, final PoseStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        boolean renderBow = true;
        for (final Connection conn : fastener.getAllConnections()) {
            if (conn.getFastener() == fastener) {
                this.renderConnection(delta, matrix, source, packedLight, packedOverlay, conn);
            }
            if (renderBow && conn instanceof GarlandVineConnection &&
                    this.renderBow(fastener, matrix, source, packedLight, packedOverlay)) {
                renderBow = false;
            }
        }
    }

    private boolean renderBow(Fastener<?> fastener, PoseStack matrix, VertexConsumerProvider source, int packedLight, int packedOverlay) {
        if (fastener instanceof FenceFastener) {
            final Level world = fastener.getWorld();
            if (world == null) {
                return false;
            }
            final BlockState state = world.getBlockState(fastener.getPos());
            if (!state.is(Tags.Blocks.FENCES)) {
                return false;
            }
            final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
            final float offset = -1.5F / 16.0F;
            final boolean north = state.getValue(FenceBlock.NORTH);
            final boolean east = state.getValue(FenceBlock.EAST);
            final boolean south = state.getValue(FenceBlock.SOUTH);
            final boolean west = state.getValue(FenceBlock.WEST);
            boolean tryDirX = true;
            boolean bow = false;
            if (!north && (east || west)) {
                this.bow(matrix, Direction.NORTH, offset, buf, packedLight, packedOverlay);
                tryDirX = false;
                bow = true;
            }
            if (!south && (east || west)) {
                this.bow(matrix, Direction.SOUTH, offset, buf, packedLight, packedOverlay);
                tryDirX = false;
                bow = true;
            }
            if (tryDirX) {
                if (!east && (north || south)) {
                    this.bow(matrix, Direction.EAST, offset, buf, packedLight, packedOverlay);
                    bow = true;
                }
                if (!west && (north || south)) {
                    this.bow(matrix, Direction.WEST, offset, buf, packedLight, packedOverlay);
                    bow = true;
                }
            }
            return bow;
        } else if (fastener.getFacing().getAxis() != Direction.Axis.Y) {
            final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
            this.bow(matrix, fastener.getFacing(), 0.0F, buf, packedLight, packedOverlay);
            return true;
        }
        return false;
    }

    private void bow(PoseStack matrix, Direction dir, float offset, VertexConsumer buf, int packedLight, int packedOverlay) {
        matrix.pushPose();
        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F - dir.toYRot()));
        if (offset != 0.0F) {
            matrix.translate(0.0D, 0.0D, offset);
        }
        this.bow.renderToBuffer(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrix.popPose();
    }

    private void renderConnection(final float delta, final PoseStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay, final Connection conn) {
        if (conn instanceof HangingLightsConnection) {
            this.hangingLights.render((HangingLightsConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof GarlandVineConnection) {
            this.garland.render((GarlandVineConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof GarlandTinselConnection) {
            this.tinsel.render((GarlandTinselConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof PennantBuntingConnection) {
            this.pennants.render((PennantBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof LetterBuntingConnection) {
            this.letters.render((LetterBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    public static void renderBakedModel(final Identifier path, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(Minecraft.getInstance().getModelManager().getModel(path), matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    public static void renderBakedModel(final BakedModel model, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(model, ItemTransforms.TransformType.FIXED, matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    // (refusing to use handlePerspective due to IForgeTransformationMatrix#push superfluous undocumented MatrixStack#push)
    public static void renderBakedModel(final BakedModel model, final ItemTransforms.TransformType type, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        model.getTransforms().getTransform(type).apply(false, matrix);
        for (final Direction side : Direction.values()) {
            for (final BakedQuad quad : model.getQuads(null, side, RandomSource.create(42L))) {
                buf.putBulkData(matrix.last(), quad, r, g, b, packedLight, packedOverlay);
            }
        }
        for (final BakedQuad quad : model.getQuads(null, null, RandomSource.create(42L))) {
            buf.putBulkData(matrix.last(), quad, r, g, b, packedLight, packedOverlay);
        }
    }
}
