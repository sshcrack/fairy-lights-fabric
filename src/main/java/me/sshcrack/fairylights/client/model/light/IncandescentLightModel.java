package me.sshcrack.fairylights.client.model.light;


import me.sshcrack.fairylights.server.feature.light.BrightnessLightBehavior;
import me.sshcrack.fairylights.server.feature.light.Light;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class IncandescentLightModel extends LightModel<BrightnessLightBehavior> {
    final ModelPart bulb;

    final ModelPart filament;

    public IncandescentLightModel(final ModelPart root) {
        super(root);
        this.bulb = root.getChild("bulb");
        this.filament = root.getChild("filament");
    }

    @Override
    public void animate(final Light<?> light, final BrightnessLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
    }

    @Override
    protected int getLight(final int packedLight) {
        return (int) Math.max((this.brightness * 15.0F * 16.0F), packedLight & 255) | packedLight & (255 << 16);
    }

    @Override
    public void render(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        super.render(matrix, builder, light, overlay, r, g, b, a);
        final int emissiveLight = this.getLight(light);
        final float cr = 0.23F, cg = 0.18F, cb = 0.14F;
        final float br = this.brightness;
        this.filament.render(matrix, builder, emissiveLight, overlay, r * (cr * (1.0F - br) + br), g * (cg * (1.0F - br) + br), b * (cb * (1.0F - br) + br), a);
    }

    @Override
    public void renderTranslucent(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        final float bi = this.brightness;
        final int emissiveLight = this.getLight(light);
        final float br = 1.0F, bg = 0.73F, bb = 0.3F;
        this.bulb.render(matrix, builder, emissiveLight, overlay, r * (br * bi + (1.0F - bi)), g * (bg * bi + (1.0F - bi)), b * (bb * bi + (1.0F - bi)), bi * 0.4F + 0.25F);
        super.renderTranslucent(matrix, builder, light, overlay, r, g, b, a);
    }

    public static TexturedModelData createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().setTextureOffset(90, 10);
        helper.unlit().addBox(-1.0F, -0.01F, -1.0F, 2.0F, 1.0F, 2.0F);
        EasyMeshBuilder bulb = new EasyMeshBuilder("bulb", 98, 10);
        bulb.addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F);
        helper.extra().add(bulb);
        EasyMeshBuilder filament = new EasyMeshBuilder("filament", 90, 13);
        filament.addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F);
        helper.extra().add(filament);
        return helper.build();
    }
}
