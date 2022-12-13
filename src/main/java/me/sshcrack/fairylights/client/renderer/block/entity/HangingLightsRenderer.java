package me.sshcrack.fairylights.client.renderer.block.entity;

import me.sshcrack.fairylights.client.FLModelLayers;
import me.sshcrack.fairylights.server.connection.HangingLightsConnection;
import me.sshcrack.fairylights.server.feature.light.Light;
import me.sshcrack.fairylights.util.matrix.MatrixStack;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import java.util.function.Function;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final LightRenderer lights;

    public HangingLightsRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        super(baker, FLModelLayers.LIGHTS_WIRE);
        this.lights = new LightRenderer(baker);
    }

    @Override
    protected int getWireColor(final HangingLightsConnection conn) {
        return conn.getString().getColor();
    }

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light<?>[] lights = conn.getFeatures();
        if (lights == null) {
            return;
        }
        final LightRenderer.Data data = this.lights.start(source);
        for (int i = 0; i < lights.length; i++) {
            final Light<?> light = lights[i];
            final Vec3d pos = light.getPoint(delta);
            matrix.pushPose();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.mulPose(Vector3f.YP.rotation(-light.getYaw(delta)));
            if (light.parallelsCord()) {
                matrix.mulPose(Vector3f.ZP.rotation(light.getPitch(delta)));
            }
            matrix.mulPose(Vector3f.XP.rotation(light.getRoll(delta)));
            if (light.getVariant() != SimpleLightVariant.FAIRY_LIGHT) { // FIXME
                matrix.mulPose(Vector3f.YP.rotation(FLMth.mod(FLMth.hash(i) * FLMth.DEG_TO_RAD, FLMth.TAU) + FLMth.PI / 4.0F));
            }
            matrix.translate(0.0D, -light.getDescent(), 0.0D);
            this.lights.render(matrix, data, light, i, delta, packedLight, packedOverlay);
            matrix.popPose();
        }
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(0, 0, 2);
    }
}
