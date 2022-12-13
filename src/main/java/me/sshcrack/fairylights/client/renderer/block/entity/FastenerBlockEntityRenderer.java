package me.sshcrack.fairylights.client.renderer.block.entity;

import me.sshcrack.fairylights.server.block.entity.FastenerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public final class FastenerBlockEntityRenderer implements BlockEntityRenderer<FastenerBlockEntityRenderer> {

    private final BlockView view;
    private final FastenerRenderer renderer;

    public FastenerBlockEntityRenderer(final BlockEntityRendererProvider.Context context, final BlockView view) {
        this.view = view;
        this.renderer = new FastenerRenderer(context::bakeLayer);
    }

    @Override
    public boolean shouldRenderOffScreen(final FastenerBlockEntity fastener) {
        return true;
    }

    @Override
    public void render(final FastenerBlockEntity fastener, final float delta, final MatrixStack matrix, final VertexConsumerProvider bufferSource, final int packedLight, final int packedOverlay) {
        fastener.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> {
            //this.bindTexture(FastenerRenderer.TEXTURE);
            matrix.push();
            final Vec3d offset = fastener.getOffset();
            matrix.translate(offset.x, offset.y, offset.z);
            //this.view.unrotate(this.getWorld(), f.getPos(), FastenerBlockEntityRenderer.GlMatrix.INSTANCE, delta);
            this.renderer.render(f, delta, matrix, bufferSource, packedLight, packedOverlay);
            matrix.pop();
        });
    }
}
