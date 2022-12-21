package me.sshcrack.fairylights.client.renderer.entity;


import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.renderer.block.entity.FastenerRenderer;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.entity.FenceFastenerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final ModelIdentifier MODEL = new ModelIdentifier(FairyLightsMod.ModID, "block/fence_fastener");

    private final FastenerRenderer renderer;

    public FenceFastenerRenderer(final EntityRendererFactory.Context context) {
        super(context);
        this.renderer = new FastenerRenderer(context::getPart);
    }

    @Override
    protected int getBlockLight(final FenceFastenerEntity entity, final BlockPos delta) {
        return entity.world.getLightLevel(LightType.BLOCK, entity.getBlockPos());
    }

    @Override
    public void render(final FenceFastenerEntity entity, final float yaw, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight) {
        final VertexConsumer buf = source.getBuffer(TexturedRenderLayers.getItemEntityTranslucentCull());
        matrix.push();
        FastenerRenderer.renderBakedModel(MODEL, matrix, buf, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.DEFAULT_UV);
        matrix.pop();
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> this.renderer.render(f, delta, matrix, source, packedLight, OverlayTexture.DEFAULT_UV));
        super.render(entity, yaw, delta, matrix, source, packedLight);
    }

    @Override
    public Identifier getTexture(final FenceFastenerEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}
