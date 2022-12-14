package me.sshcrack.fairylights.client.renderer.block.entity;

import me.sshcrack.fairylights.client.ClientProxy;
import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.util.Catenary;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.FLMth;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import java.util.function.Function;

public abstract class ConnectionRenderer<C extends Connection> {
    private final WireModel model;
    private final float wireInflate;

    protected ConnectionRenderer(final Function<EntityModelLayer, ModelPart> baker, final EntityModelLayer wireModelLocation) {
        this(baker, wireModelLocation, 0.0F);
    }

    protected ConnectionRenderer(final Function<EntityModelLayer, ModelPart> baker, final EntityModelLayer wireModelLocation, final float wireInflate) {
        this.model = new WireModel(baker.apply(wireModelLocation));
        this.wireInflate = wireInflate;
    }

    public void render(final C conn, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        final Curve currCat = conn.getCatenary();
        final Curve prevCat = conn.getPrevCatenary();
        if (currCat != null && prevCat != null) {
            final Curve cat = prevCat.lerp(currCat, delta);
            final Curve.SegmentIterator it = cat.iterator();
            final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.getVertexConsumer(source, RenderLayer::getEntityCutout);
            final int color = this.getWireColor(conn);
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            while (it.next()) {
                matrix.push();
                matrix.translate(it.getX(0.0F), it.getY(0.0F),  it.getZ(0.0F));
                matrix.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(FLMth.PI / 2.0F - it.getYaw()));
                matrix.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(-it.getPitch()));
                matrix.scale(1.0F + this.wireInflate, 1.0F, it.getLength() * 16.0F);
                this.model.render(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
                matrix.pop();
                this.renderSegment(conn, it, delta, matrix, packedLight, source, packedOverlay);
            }
            this.render(conn, cat, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    protected int getWireColor(final C conn) {
        return 0xFFFFFF;
    }

    protected void render(final C conn, final Curve catenary, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {}

    protected void renderSegment(final C connection, final Catenary.SegmentView it, final float delta, final MatrixStack matrix, final int packedLight, final VertexConsumerProvider source, final int packedOverlay) {}

    public static class WireModel extends Model {
        final ModelPart root;

        WireModel(final ModelPart root) {
            super(RenderLayer::getEntityCutout);
            this.root = root;
        }

        public void render(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }

        public static TexturedModelData createLayer(final int u, final int v, final int size) {
            ModelData mesh = new ModelData();
            mesh.getRoot().addChild("root", ModelPartBuilder.create()
                .uv(u, v)
                .cuboid(-size * 0.5F, -size * 0.5F, 0.0F, size, size, 1.0F), ModelTransform.NONE);
            return TexturedModelData.of(mesh, 128, 128);
        }
    }
}
