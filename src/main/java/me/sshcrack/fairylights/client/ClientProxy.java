package me.sshcrack.fairylights.client;

import com.google.common.collect.ImmutableList;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.model.light.*;
import me.sshcrack.fairylights.client.renderer.block.entity.*;
import me.sshcrack.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.sshcrack.fairylights.server.ServerProxy;
import me.sshcrack.fairylights.server.block.entity.FLBlockEntities;
import me.sshcrack.fairylights.server.entity.FLEntities;
import me.sshcrack.fairylights.server.event.ModelEvent;
import me.sshcrack.fairylights.server.feature.light.ColorChangingBehavior;
import me.sshcrack.fairylights.server.item.DyeableItem;
import me.sshcrack.fairylights.server.item.FLItems;
import me.sshcrack.fairylights.server.item.HangingLightsConnectionItem;
import me.sshcrack.fairylights.server.string.StringTypes;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public final class ClientProxy extends ServerProxy {
    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier SOLID_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(FairyLightsMod.ModID, "entity/connections"));

    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier TRANSLUCENT_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(FairyLightsMod.ModID, "entity/connections"));

    private final ImmutableList<ModelIdentifier> entityModels = new ImmutableList.Builder<ModelIdentifier>()
        .addAll(PennantBuntingRenderer.MODELS)
        .addAll(LetterBuntingRenderer.MODELS.values())
        .build();

    @Override
    public void init() {
        super.init();
        FairyLightsMod.EVENT_BUS.registerEventHandler(this);
        ClientEventHandler clientEventHandler = new ClientEventHandler();
        FairyLightsMod.EVENT_BUS.registerEventHandler(clientEventHandler);

        this.setupModelLayers();
        this.setup();
        this.onTextureStitch();
    }

    private void onTextureStitch() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
                .register((atlasTexture, registry) -> {
                    registry.register(SOLID_TEXTURE.getAtlasId());
                });
    }

    @SubscribeEvent
    private void onBakingComplete(final ModelEvent.BakingCompleted event) {
        final VertexFormat vertexFormat = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        final int size = vertexFormat.getVertexSizeInteger();
        final int index = this.getUvIndex(vertexFormat);
        if (index != -1) {
            this.entityModels.forEach(path -> {
                final BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(path);
                if (model != MinecraftClient.getInstance().getBakedModelManager().getMissingModel()) {
                    this.recomputeUv(size, index, model);
                }
            });
        }
    }

    private int getUvIndex(VertexFormat vertexFormat) {
        int position = 0;
        for (final VertexFormatElement ee : vertexFormat.getElements()) {
            if (ee.getType() == VertexFormatElement.Type.UV) {
                if (position % 4 == 0) {
                    return position / 4;
                }
                break;
            }
            position += ee.getByteLength();
        }
        return -1;
    }

    private void recomputeUv(final int stride, final int finalUvOffset, final BakedModel model) {
        final Sprite sprite = model.getParticleSprite();
        final int w = (int) (sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU()));
        final int h = (int) (sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV()));
        //TODO i dunno
        for (final BakedQuad quad : model.getQuads(null, null, Random.create(42L))) {
            final int[] data = quad.getVertexData();
            for (int n = 0; n < 4; n++) {
                int iu = n * stride + finalUvOffset;
                int iv = n * stride + finalUvOffset + 1;
                data[iu] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iu]) * w) / w);
                data[iv] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iv]) * h) / h);
            }
        }
    }

    private void setup() {
        BlockEntityRendererRegistry.register(FLBlockEntities.FASTENER.get(), context -> new FastenerBlockEntityRenderer(context, ServerProxy.buildBlockView()));
        BlockEntityRendererRegistry.register(FLBlockEntities.LIGHT.get(), LightBlockEntityRenderer::new);
        EntityRendererRegistry.register(FLEntities.FASTENER.get(), FenceFastenerRenderer::new);
        /*final LightRenderer r = new LightRenderer();
        final StringBuilder bob = new StringBuilder();
        FLItems.lights().forEach(l -> {
            final LightModel<?> model = r.getModel(l.getBlock().getVariant(), -1);
            final AxisAlignedBB bb = model.getBounds();
            bob.append(String.format("%n%s new AxisAlignedBB(%.3fD, %.3fD, %.3fD, %.3fD, %.3fD, %.3fD), %.3fD", l.getRegistryName(), bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, model.getFloorOffset()));
        });
        LogManager.getLogger().debug("waldo {}", bob);*/

        setupColors();
        setupModels();
    }

    private void setupModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.BOW, BowModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.GARLAND_RINGS, GarlandVineRenderer.RingsModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.TINSEL_STRIP, GarlandTinselRenderer.StripModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.FAIRY_LIGHT, FairyLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.PAPER_LANTERN, PaperLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ORB_LANTERN, OrbLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.FLOWER_LIGHT, FlowerLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.CANDLE_LANTERN_LIGHT, ColorCandleLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.OIL_LANTERN_LIGHT, ColorOilLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.JACK_O_LANTERN, JackOLanternLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SKULL_LIGHT, SkullLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.GHOST_LIGHT, GhostLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SPIDER_LIGHT, SpiderLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.WITCH_LIGHT, WitchLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SNOWFLAKE_LIGHT, SnowflakeLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.HEART_LIGHT, HeartLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.MOON_LIGHT, MoonLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.STAR_LIGHT, StarLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_1, () -> IcicleLightsModel.createLayer(1));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_2, () -> IcicleLightsModel.createLayer(2));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_3, () -> IcicleLightsModel.createLayer(3));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_4, () -> IcicleLightsModel.createLayer(4));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.METEOR_LIGHT, MeteorLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.OIL_LANTERN, OilLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.CANDLE_LANTERN, CandleLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.INCANDESCENT_LIGHT, IncandescentLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.LETTER_WIRE, LetterBuntingRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.PENNANT_WIRE, PennantBuntingRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.TINSEL_WIRE, GarlandTinselRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.VINE_WIRE, GarlandVineRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.LIGHTS_WIRE, HangingLightsRenderer::wireLayer);
    }

    private void setupModels() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(FenceFastenerRenderer.MODEL);
            this.entityModels.forEach(out);
        });
    }

    private void setupColors() {
        ColorProviderRegistry.ITEM.register((stack, index) -> {
            if (index == 1) {
                if (ColorChangingBehavior.exists(stack)) {
                    return ColorChangingBehavior.animate(stack);
                }
                return DyeableItem.getColor(stack);
            }
            return 0xFFFFFF;
        },
                FLItems.FAIRY_LIGHT.get(),
                FLItems.PAPER_LANTERN.get(),
                FLItems.ORB_LANTERN.get(),
                FLItems.FLOWER_LIGHT.get(),
                FLItems.CANDLE_LANTERN_LIGHT.get(),
                FLItems.OIL_LANTERN_LIGHT.get(),
                FLItems.JACK_O_LANTERN.get(),
                FLItems.SKULL_LIGHT.get(),
                FLItems.GHOST_LIGHT.get(),
                FLItems.SPIDER_LIGHT.get(),
                FLItems.WITCH_LIGHT.get(),
                FLItems.SNOWFLAKE_LIGHT.get(),
                FLItems.HEART_LIGHT.get(),
                FLItems.MOON_LIGHT.get(),
                FLItems.STAR_LIGHT.get(),
                FLItems.ICICLE_LIGHTS.get(),
                FLItems.METEOR_LIGHT.get()
        );
        ColorProviderRegistry.ITEM.register((stack, index) -> {
            final NbtCompound tag = stack.getNbt();
            if (index == 0) {
                if (tag != null) {
                    return HangingLightsConnectionItem.getString(tag).getColor();
                }
                return StringTypes.BLACK_STRING.getColor();
            }
            if (tag != null) {
                final NbtList tagList = tag.getList("pattern", NbtCompound.COMPOUND_TYPE);
                if (tagList.size() > 0) {
                    final ItemStack item = ItemStack.fromNbt(tagList.getCompound((index - 1) % tagList.size()));
                    if (ColorChangingBehavior.exists(item)) {
                        return ColorChangingBehavior.animate(item);
                    }
                    return DyeableItem.getColor(item);
                }
            }
            if (FairyLightsMod.CHRISTMAS.isOccurringNow()) {
                return (index + Util.getMeasuringTimeMs() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
            }
            if (FairyLightsMod.HALLOWEEN.isOccurringNow()) {
                return index % 2 == 0 ? 0xf9801d : 0x8932b8;
            }
            return 0xFFD584;
        }, FLItems.HANGING_LIGHTS.get());
        ColorProviderRegistry.ITEM.register((stack, index) -> index == 0 ? DyeableItem.getColor(stack) : 0xFFFFFFFF, FLItems.TINSEL.get());
        ColorProviderRegistry.ITEM.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFFFF;
            }
            final NbtCompound tag = stack.getNbt();
            if (tag != null) {
                final NbtList tagList = tag.getList("pattern", NbtCompound.COMPOUND_TYPE);
                if (tagList.size() > 0) {
                    final ItemStack light = ItemStack.fromNbt(tagList.getCompound((index - 1) % tagList.size()));
                    return DyeableItem.getColor(light);
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.PENNANT_BUNTING.get());
        ColorProviderRegistry.ITEM.register(ClientProxy::secondLayerColor, FLItems.TRIANGLE_PENNANT.get());
        ColorProviderRegistry.ITEM.register(ClientProxy::secondLayerColor, FLItems.SPEARHEAD_PENNANT.get());
        ColorProviderRegistry.ITEM.register(ClientProxy::secondLayerColor, FLItems.SWALLOWTAIL_PENNANT.get());
        ColorProviderRegistry.ITEM.register(ClientProxy::secondLayerColor, FLItems.SQUARE_PENNANT.get());
        ColorProviderRegistry.ITEM.register((stack, index) -> {
            final NbtCompound tag = stack.getNbt();
            if (index > 0 && tag != null) {
                final StyledString str = StyledString.deserialize(tag.getCompound("text"));
                if (str.length() > 0) {
                    Formatting lastColor = null, color = null;
                    int n = (index - 1) % str.length();
                    for (int i = 0; i < str.length(); lastColor = color, i++) {
                        color = str.styleAt(i).getColor();
                        if (lastColor != color && (n-- == 0)) {
                            break;
                        }
                    }
                    return StyledString.getColor(color) | 0xFF000000;
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.LETTER_BUNTING.get());
    }

    private static int secondLayerColor(final ItemStack stack, final int index) {
        return index == 0 ? 0xFFFFFF : DyeableItem.getColor(stack);
    }
}
