package me.sshcrack.fairylights.client.renderer.block.entity;

import me.sshcrack.fairylights.server.block.entity.FastenerBlockEntity;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.fastener.BlockView;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public final class FastenerBlockEntityRenderer implements BlockEntityRenderer<FastenerBlockEntity> {

    private final BlockView view;
    private final FastenerRenderer renderer;

    public FastenerBlockEntityRenderer(final BlockEntityRendererFactory.Context context, final BlockView view) {
        this.view = view;
        this.renderer = new FastenerRenderer(context::getLayerModelPart);
    }



    @Override
    public boolean rendersOutsideBoundingBox(final FastenerBlockEntity fastener) {
        return true;
    }

    @Override
    public void render(final FastenerBlockEntity fastener, final float delta, final MatrixStack matrix, final VertexConsumerProvider bufferSource, final int packedLight, final int packedOverlay) {
        ((CapabilityHelper<BlockEntity>) fastener).getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> {
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
