package me.sshcrack.fairylights.client.model.light;

import me.sshcrack.fairylights.server.feature.light.Light;
import me.sshcrack.fairylights.server.feature.light.MeteorLightBehavior;
import me.sshcrack.fairylights.util.FLMth;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class MeteorLightModel extends LightModel<MeteorLightBehavior> {
    private static final int LIGHT_COUNT = 12;

    private final PartPair[] lights;

    private final ModelPart connector;

    private final ModelPart cap;

    private float stage;

    public MeteorLightModel(final ModelPart root) {
        super(root);
        this.connector = this.unlit.getChild("connector");
        this.cap = this.unlit.getChild("cap");
        this.lights = new PartPair[LIGHT_COUNT];
        for (int i = 0; i < LIGHT_COUNT; i++) {
            String key = "light_" + i;
            this.lights[i] = new PartPair(this.litTint.getChild(key), this.litTintGlow.getChild(key));
        }
        /*
        (m, u, v) -> new ModelPart(m, u, v) {
                @Override
                public void translateRotate(final MatrixStack stack) {
                    super.translateRotate(stack);
                    stack.scale(rodScale, 1.0F, rodScale);
                }
            }
         */
    }

    @Override
    public void animate(final Light<?> light, final MeteorLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.red = behavior.getRed(delta);
        this.green = behavior.getGreen(delta);
        this.blue = behavior.getBlue(delta);
        this.stage = behavior.getProgress(delta) * 3.0F - 1.0F;
    }

    private float computeBrightness(final float t) {
        return MathHelper.clamp(t - this.stage > 0.0F ? 1.0F - Math.abs(t - this.stage) * 4.0F : 1.0F - Math.abs(t - this.stage), 0.0F, 1.0F);
    }

    @Override
    public void render(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        for (int i = 0; i < this.lights.length; i++) {
            this.brightness = this.computeBrightness((float) i / this.lights.length);
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].setVisible(i == n);
            }
            this.connector.visible = i == 0;
            this.cap.visible = i == this.lights.length - 1;
            super.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }

    @Override
    public void renderTranslucent(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        for (int i = 0; i < this.lights.length; i++) {
            this.brightness = this.computeBrightness((float) i / this.lights.length);
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].setVisible(i == n);
            }
            super.renderTranslucent(matrix, builder, light, overlay, r, g, b, a);
        }
    }

    public static TexturedModelData createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder connector = new EasyMeshBuilder("connector", 77, 0);
        connector.addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        helper.unlit().addChild(connector);
        final EasyMeshBuilder cap = new EasyMeshBuilder("cap", 77, 0);
        cap.addBox(-1, -25.45F + 0.05F, -1, 2, 1, 2, 0);
        helper.unlit().addChild(cap);
        final float rodScale = 0.8F;
        final BulbBuilder bulb = helper.createBulb();
        for (int i = 0; i < LIGHT_COUNT; i++) {
            final BulbBuilder light = bulb.createChild("light_" + i, 37, 72);
            light.addBox(-1, -i * 2 - 2.5F + 0.05F, -1, 2, 2, 2, MathHelper.sin(i * FLMth.PI / LIGHT_COUNT) * 0.1F);
        }
        return helper.build();
    }

    private record PartPair(ModelPart first, ModelPart second) {
        void setVisible(final boolean visible) {
            this.first().visible = visible;
            this.second().visible = visible;
        }
    }
}
