package me.sshcrack.fairylights.client.renderer.block.entity;

import me.sshcrack.fairylights.client.model.light.LightModel;
import me.sshcrack.fairylights.server.block.LightBlock;
import me.sshcrack.fairylights.server.block.entity.LightBlockEntity;
import me.sshcrack.fairylights.server.feature.light.Light;
import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3f;

public class LightBlockEntityRenderer implements BlockEntityRenderer<LightBlockEntity> {
    private final LightRenderer lights;

    public LightBlockEntityRenderer(final BlockEntityRendererFactory.Context context) {
        this.lights = new LightRenderer(context::getLayerModelPart);
    }

    @Override
    public void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        this.render(entity, delta, matrix, source, packedLight, packedOverlay, entity.getLight());
    }

    private <T extends LightBehavior> void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay, final Light<T> light) {
        final LightModel<T> model = this.lights.getModel(light, -1);
        final Box box = model.getBounds();
        final BlockState state = entity.getCachedState();
        final WallMountLocation face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.FACING).asRotation();
        matrix.push();
        matrix.translate(0.5D, 0.5D, 0.5D);
        matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotation));
        if (light.getVariant().isOrientable()) {
            if (face == WallMountLocation.WALL) {
                matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            } else if (face == WallMountLocation.FLOOR) {
                matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-180.0F));
            }
            matrix.translate(0.0D, 0.5D, 0.0D);
        } else {
            if (face == WallMountLocation.CEILING) {
                matrix.translate(0.0D, 0.25D, 0.0D);
            } else if (face == WallMountLocation.WALL) {
                matrix.translate(0.0D, 3.0D / 16.0D, 0.125D);
            } else {
                matrix.translate(0.0D, -box.minY - model.getFloorOffset() - 0.5D, 0.0D);
            }
        }
        this.lights.render(matrix, this.lights.start(source), light, model, delta, packedLight, packedOverlay);
        matrix.pop();
    }
}
