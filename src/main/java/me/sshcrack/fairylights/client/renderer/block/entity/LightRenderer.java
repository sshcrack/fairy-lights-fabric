package me.sshcrack.fairylights.client.renderer.block.entity;

import com.google.common.collect.ImmutableMap;
import me.sshcrack.fairylights.client.ClientProxy;
import me.sshcrack.fairylights.client.FLModelLayers;
import me.sshcrack.fairylights.client.model.light.*;
import me.sshcrack.fairylights.server.feature.light.Light;
import me.sshcrack.fairylights.server.feature.light.LightBehavior;
import me.sshcrack.fairylights.server.item.LightVariant;
import me.sshcrack.fairylights.server.item.SimpleLightVariant;
import me.sshcrack.fairylights.util.FLMth;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LightRenderer {
    static class DefaultModel extends LightModel<LightBehavior> {
        private static final ModelPart EMPTY = new ModelPart(List.of(), Map.of());

        public DefaultModel() {
            super(new ModelPart(List.of(), Map.of(
                "lit", EMPTY,
                "lit_tint", EMPTY,
                "lit_tint_glow", EMPTY,
                "unlit", EMPTY
            )));
        }

        @Override
        public void render(final MatrixStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        }
    }

    private final LightModelProvider<LightBehavior> defaultLight = LightModelProvider.of(new DefaultModel());

    private final Map<LightVariant<?>, LightModelProvider<?>> lights;

    public LightRenderer(final Function<EntityModelLayer, ModelPart> baker) {
        lights = new ImmutableMap.Builder<LightVariant<?>, LightModelProvider<?>>()
            .put(SimpleLightVariant.FAIRY_LIGHT, LightModelProvider.of(new FairyLightModel(baker.apply(FLModelLayers.FAIRY_LIGHT))))
            .put(SimpleLightVariant.PAPER_LANTERN, LightModelProvider.of(new PaperLanternModel(baker.apply(FLModelLayers.PAPER_LANTERN))))
            .put(SimpleLightVariant.ORB_LANTERN, LightModelProvider.of(new OrbLanternModel(baker.apply(FLModelLayers.ORB_LANTERN))))
            .put(SimpleLightVariant.FLOWER_LIGHT, LightModelProvider.of(new FlowerLightModel(baker.apply(FLModelLayers.FLOWER_LIGHT))))
            .put(SimpleLightVariant.CANDLE_LANTERN_LIGHT, LightModelProvider.of(new ColorCandleLanternModel(baker.apply(FLModelLayers.CANDLE_LANTERN_LIGHT))))
            .put(SimpleLightVariant.OIL_LANTERN_LIGHT, LightModelProvider.of(new ColorOilLanternModel(baker.apply(FLModelLayers.OIL_LANTERN_LIGHT))))
            .put(SimpleLightVariant.JACK_O_LANTERN, LightModelProvider.of(new JackOLanternLightModel(baker.apply(FLModelLayers.JACK_O_LANTERN))))
            .put(SimpleLightVariant.SKULL_LIGHT, LightModelProvider.of(new SkullLightModel(baker.apply(FLModelLayers.SKULL_LIGHT))))
            .put(SimpleLightVariant.GHOST_LIGHT, LightModelProvider.of(new GhostLightModel(baker.apply(FLModelLayers.GHOST_LIGHT))))
            .put(SimpleLightVariant.SPIDER_LIGHT, LightModelProvider.of(new SpiderLightModel(baker.apply(FLModelLayers.SPIDER_LIGHT))))
            .put(SimpleLightVariant.WITCH_LIGHT, LightModelProvider.of(new WitchLightModel(baker.apply(FLModelLayers.WITCH_LIGHT))))
            .put(SimpleLightVariant.SNOWFLAKE_LIGHT, LightModelProvider.of(new SnowflakeLightModel(baker.apply(FLModelLayers.SNOWFLAKE_LIGHT))))
            .put(SimpleLightVariant.HEART_LIGHT, LightModelProvider.of(new HeartLightModel(baker.apply(FLModelLayers.HEART_LIGHT))))
            .put(SimpleLightVariant.MOON_LIGHT, LightModelProvider.of(new MoonLightModel(baker.apply(FLModelLayers.MOON_LIGHT))))
            .put(SimpleLightVariant.STAR_LIGHT, LightModelProvider.of(new StarLightModel(baker.apply(FLModelLayers.STAR_LIGHT))))
            .put(SimpleLightVariant.ICICLE_LIGHTS, LightModelProvider.of(
                new IcicleLightsModel[] {
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_1), 1),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_2), 2),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_3), 3),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_4), 4)
                },
                (models, i) -> models[i < 0 ? 3 : FLMth.mod(FLMth.hash(i), 4)]
            ))
            .put(SimpleLightVariant.METEOR_LIGHT, LightModelProvider.of(new MeteorLightModel(baker.apply(FLModelLayers.METEOR_LIGHT))))
            .put(SimpleLightVariant.OIL_LANTERN, LightModelProvider.of(new OilLanternModel(baker.apply(FLModelLayers.OIL_LANTERN))))
            .put(SimpleLightVariant.CANDLE_LANTERN, LightModelProvider.of(new CandleLanternModel(baker.apply(FLModelLayers.CANDLE_LANTERN))))
            .put(SimpleLightVariant.INCANDESCENT_LIGHT, LightModelProvider.of(new IncandescentLightModel(baker.apply(FLModelLayers.INCANDESCENT_LIGHT))))
            .build();
    }

    public Data start(final VertexConsumerProvider source) {
        // TODO dunno just guessed the RenderLayer thing
        final VertexConsumer buf = ClientProxy.TRANSLUCENT_TEXTURE.getVertexConsumer(source, RenderLayer::getEntityTranslucent);
        ForwardingVertexConsumer translucent = new ForwardingVertexConsumer() {
            @Override
            protected VertexConsumer delegate() {
                return buf;
            }

            @Override
            public VertexConsumer normal(float x, float y, float z) {
                return super.normal(0.0F, 1.0F, 0.0F);
            }
        };
        return new Data(buf, translucent);
    }

    public <T extends LightBehavior> LightModel<T> getModel(final Light<?> light, final int index) {
        return this.getModel(light.getVariant(), index);
    }

    @SuppressWarnings("unchecked")
    public <T extends LightBehavior> LightModel<T> getModel(final LightVariant<?> variant, final int index) {
        return (LightModel<T>) this.lights.getOrDefault(variant, this.defaultLight).get(index);
    }

    public void render(final MatrixStack matrix, final Data data, final Light<?> light, final int index, final float delta, final int packedLight, final int packedOverlay) {
        this.render(matrix, data, light, this.getModel(light, index), delta, packedLight, packedOverlay);
    }

    public <T extends LightBehavior> void render(final MatrixStack matrix, final Data data, final Light<T> light, final LightModel<T> model, final float delta, final int packedLight, final int packedOverlay) {
        model.animate(light, light.getBehavior(), delta);
        model.render(matrix, data.solid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        model.renderTranslucent(matrix, data.translucent, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    interface LightModelProvider<T extends LightBehavior> {
        LightModel<T> get(final int index);

        static <T extends LightBehavior> LightModelProvider<T> of(final LightModel<T> model) {
            return i -> model;
        }

        static <T extends LightBehavior> LightModelProvider<T> of(final Supplier<LightModel<T>> model) {
            return i -> model.get();
        }

        static <T extends LightBehavior, D> LightModelProvider<T> of(final D data, final BiFunction<? super D, Integer, LightModel<T>> function) {
            return i -> function.apply(data, i);
        }
    }

    static class Data {
        final VertexConsumer solid;
        final VertexConsumer translucent;

        Data(final VertexConsumer solid, final VertexConsumer translucent) {
            this.solid = solid;
            this.translucent = translucent;
        }
    }

}
