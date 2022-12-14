package me.sshcrack.fairylights.client.renderer.block.entity;

import me.paulf.fairylights.util.FLMth;
import me.sshcrack.fairylights.client.ClientProxy;
import me.sshcrack.fairylights.client.FLModelLayers;
import me.sshcrack.fairylights.server.connection.GarlandVineConnection;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.FLMth;
import me.sshcrack.fairylights.util.RandomArray;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import java.util.function.Function;

public class GarlandVineRenderer extends ConnectionRenderer<GarlandVineConnection> {
    private static final int RING_COUNT = 7;

    private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

    private final RingsModel rings;

    protected GarlandVineRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        super(baker, FLModelLayers.VINE_WIRE);
        this.rings = new RingsModel(baker.apply(FLModelLayers.GARLAND_RINGS));
    }

    @Override
    protected void render(final GarlandVineConnection conn, final Curve catenary, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final int hash = conn.getUUID().hashCode();
        final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.getVertexConsumer(source, RenderLayer::getEntityCutout);
        catenary.visitPoints(0.25F, false, (index, x, y, z, yaw, pitch) -> {
            matrix.push();
            matrix.translate(x, y, z);
            matrix.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(-yaw));
            matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(pitch));
            matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(RAND.get(index + hash) * 45.0F));
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(RAND.get(index + 8 + hash) * 60.F + 90.0F));
            this.rings.setWhich(index % RING_COUNT);
            this.rings.renderToBuffer(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrix.pop();
        });
    }

    public static TexturedModelData wireLayer() {
        return WireModel.createLayer(39, 0, 1);
    }

    public static class RingsModel extends Model {
        final ModelPart[] roots;
        int which;

        RingsModel(final ModelPart root) {
            super(RenderLayer::getEntityCutout);
            ModelPart[] roots = new ModelPart[RING_COUNT];
            for (int i = 0; i < RING_COUNT; i++) {
                roots[i] = root.getChild(Integer.toString(i));
            }
            this.roots = roots;
        }

        public static TexturedModelData createLayer() {
            final float size = 4.0F;
            ModelPartBuilder root = ModelPartBuilder.create()
                .uv(14, 91)
                .cuboid(-size / 2.0F, -size / 2.0F, -size / 2.0F, size, size, size);
            PartPose crossPose = PartPose.rotation(0.0F, 0.0F, FLMth.HALF_PI);
            ModelData mesh = new ModelData();
            for (int i = 0; i < RING_COUNT; i++) {
                mesh.getRoot().addChild(Integer.toString(i), root, PartPose.ZERO)
                    .addOrReplaceChild("cross_" + i, ModelPartBuilder.create()
                        .uv(i * 8, 64)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F)
                        .cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F), crossPose);
            }
            return TexturedModelData.of(mesh, 128, 128);
        }

        public void setWhich(int which) {
            this.which = which;
        }

        @Override
        public void renderToBuffer(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.roots[this.which].render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
