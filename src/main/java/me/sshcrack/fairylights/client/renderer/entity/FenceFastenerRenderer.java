package me.sshcrack.fairylights.client.renderer.entity;


import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.renderer.block.entity.FastenerRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final Identifier MODEL = new Identifier(FairyLightsMod.ModID, "block/fence_fastener");

    private final FastenerRenderer renderer;

    public FenceFastenerRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.renderer = new FastenerRenderer(context::bakeLayer);
    }

    @Override
    protected int getBlockLightLevel(final FenceFastenerEntity entity, final BlockPos delta) {
        return entity.world.getBrightness(LightLayer.BLOCK, entity.blockPosition());
    }

    @Override
    public void render(final FenceFastenerEntity entity, final float yaw, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight) {
        final VertexConsumer buf = source.getBuffer(Sheets.cutoutBlockSheet());
        matrix.pushPose();
        FastenerRenderer.renderBakedModel(MODEL, matrix, buf, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        matrix.popPose();
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> this.renderer.render(f, delta, matrix, source, packedLight, OverlayTexture.NO_OVERLAY));
        super.render(entity, yaw, delta, matrix, source, packedLight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Identifier getTextureLocation(final FenceFastenerEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
