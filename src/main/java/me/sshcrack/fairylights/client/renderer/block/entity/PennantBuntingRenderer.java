package me.sshcrack.fairylights.client.renderer.block.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.FLModelLayers;
import me.sshcrack.fairylights.server.connection.PennantBuntingConnection;
import me.sshcrack.fairylights.server.feature.Pennant;
import me.sshcrack.fairylights.server.item.FLItems;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.styledstring.Style;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.function.Function;

public class PennantBuntingRenderer extends ConnectionRenderer<PennantBuntingConnection> {
    private static final ModelIdentifier TRIANGLE_MODEL = new ModelIdentifier(FairyLightsMod.ModID, "entity/triangle_pennant");

    private static final ModelIdentifier SPEARHEAD_MODEL = new ModelIdentifier(FairyLightsMod.ModID, "entity/spearhead_pennant");

    private static final ModelIdentifier SWALLOWTAIL_MODEl = new ModelIdentifier(FairyLightsMod.ModID, "entity/swallowtail_pennant");

    private static final ModelIdentifier SQUARE_MODEL = new ModelIdentifier(FairyLightsMod.ModID, "entity/square_pennant");

    public static final ImmutableSet<ModelIdentifier> MODELS = ImmutableSet.of(TRIANGLE_MODEL, SPEARHEAD_MODEL, SWALLOWTAIL_MODEl, SQUARE_MODEL);

    private final ImmutableMap<Item, ModelIdentifier> models = ImmutableMap.of(
        FLItems.TRIANGLE_PENNANT.get(), TRIANGLE_MODEL,
        FLItems.SPEARHEAD_PENNANT.get(), SPEARHEAD_MODEL,
        FLItems.SWALLOWTAIL_PENNANT.get(), SWALLOWTAIL_MODEl,
        FLItems.SQUARE_PENNANT.get(), SQUARE_MODEL
    );

    public PennantBuntingRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        super(baker, FLModelLayers.PENNANT_WIRE, 0.25F);
    }

    @Override
    protected void render(final PennantBuntingConnection conn, final Curve catenary, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Pennant[] currLights = conn.getFeatures();
        if (currLights != null) {
            final TextRenderer font = MinecraftClient.getInstance().textRenderer;
            final VertexConsumer buf = source.getBuffer(TexturedRenderLayers.getEntityCutout());
            final int count = currLights.length;
            if (count == 0) {
                return;
            }
            StyledString text = conn.getText();
            if (text.length() > count) {
                text = text.substring(0, count);
            }
            final int offset = (count - text.length()) / 2;
            for (int i = 0; i < count; i++) {
                final Pennant currPennant = currLights[i];
                final int color = currPennant.getColor();
                final float r = ((color >> 16) & 0xFF) / 255.0F;
                final float g = ((color >> 8) & 0xFF) / 255.0F;
                final float b = (color & 0xFF) / 255.0F;
                final BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(this.models.getOrDefault(currPennant.getItem(), TRIANGLE_MODEL));
                final Vec3d pos = currPennant.getPoint(delta);
                matrix.push();
                matrix.translate(pos.x, pos.y, pos.z);
                matrix.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(-currPennant.getYaw(delta)));
                matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(currPennant.getPitch(delta)));
                matrix.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(currPennant.getRoll(delta)));
                matrix.push();
                FastenerRenderer.renderBakedModel(model, matrix, buf, r, g, b, packedLight, packedOverlay);
                matrix.pop();
                if (i >= offset && i < offset + text.length()) {
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, i - offset, 1, delta);
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, text.length() - 1 - (i - offset), -1, delta);
                }
                matrix.pop();
            }
        }
    }

    private void drawLetter(final MatrixStack matrix, final VertexConsumerProvider source, final Pennant pennant, final int packedLight, final TextRenderer font, final StyledString text, final int index, final int side, final float delta) {
        final Style style = text.styleAt(index);
        final StringBuilder bob = new StringBuilder();
        if (style.isObfuscated()) bob.append(Formatting.OBFUSCATED);
        if (style.isBold()) bob.append(Formatting.BOLD);
        if (style.isStrikethrough()) bob.append(Formatting.STRIKETHROUGH);
        if (style.isUnderline()) bob.append(Formatting.UNDERLINE);
        if (style.isItalic()) bob.append(Formatting.ITALIC);
        bob.append(text.charAt(index));
        final String chr = bob.toString();
        final Matrix3f m = new Matrix3f();
        m.loadIdentity();
        m.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(pennant.getYaw(delta)));
        m.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(pennant.getPitch(delta)));
        m.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(pennant.getRoll(delta)));
        final Vec3f v = new Vec3f(0.0F, 0.0F, side);
        v.transform(m);
        // TODO: correct entity diffuse
        //Method is diffuseLight in Forge
        final float brightness = MathHelper.method_34955(v.getX(), v.getY(), v.getZ());
        final int styleColor = MoreObjects.firstNonNull(style.getColor().getColorValue(), 0xFFFFFF);
        final int r = (int) ((styleColor >> 16 & 0xFF) * brightness);
        final int g = (int) ((styleColor >> 8 & 0xFF) * brightness);
        final int b = (int) ((styleColor & 0xFF) * brightness);
        final int argb = 0xFF000000 | r << 16 | g << 8 | b;
        matrix.push();
        matrix.translate(0.0F, -0.25F, 0.04F * side);
        final float s = 0.03075F;
        matrix.scale(s * side, -s, s);
        final float w = font.getWidth(chr);
        font.draw(chr, -(w - 1.0F) / 2.0F, -4.0F, argb, false, matrix.peek().getPositionMatrix(), source, false, 0, packedLight);
        matrix.pop();
    }

    public static TexturedModelData wireLayer() {
        return WireModel.createLayer(0, 17, 1);
    }
}
