package me.sshcrack.fairylights.client.renderer.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.FLModelLayers;
import me.sshcrack.fairylights.server.connection.LetterBuntingConnection;
import me.sshcrack.fairylights.server.feature.Letter;
import me.sshcrack.fairylights.util.Curve;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Locale;
import java.util.function.Function;

public class LetterBuntingRenderer extends ConnectionRenderer<LetterBuntingConnection> {
    public static final Int2ObjectMap<ModelIdentifier> MODELS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ&!?".chars()
        .collect(
            Int2ObjectOpenHashMap::new,
            (map, cp) -> map.put(cp, new ModelIdentifier(FairyLightsMod.ModID, "entity/letter/" + Character.getName(cp).toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "_"))),
            Int2ObjectOpenHashMap::putAll
        );

    public LetterBuntingRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        super(baker, FLModelLayers.LETTER_WIRE);
    }

    @Override
    protected void render(final LetterBuntingConnection conn, final Curve catenary, final float delta, final MatrixStack matrix, final VertexConsumerProvider source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Letter[] letters = conn.getLetters();
        if (letters == null) {
            return;
        }
        final int count = letters.length;
        if (count == 0) {
            return;
        }
        final VertexConsumer buf = source.getBuffer(TexturedRenderLayers.getEntityCutout());
        for (final Letter letter : letters) {
            final ModelIdentifier path = MODELS.get(letter.getLetter());
            if (path == null) {
                continue;
            }
            final int color = StyledString.getColor(letter.getStyle().getColor());
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            final Vec3d pos = letter.getPoint(delta);
            matrix.push();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(-letter.getYaw(delta)));
            matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(letter.getPitch(delta)));
            matrix.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(letter.getRoll(delta)));
            matrix.translate(-0.5F, -1.0F - 0.5F / 16.0F, -0.5F);
            FastenerRenderer.renderBakedModel(path, matrix, buf, r, g, b, packedLight, packedOverlay);
            matrix.pop();
        }
    }

    public static TexturedModelData wireLayer() {
        return WireModel.createLayer(0, 17, 1);
    }
}
