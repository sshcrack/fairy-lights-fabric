package me.sshcrack.fairylights.client;

import com.google.common.collect.ImmutableList;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.model.light.*;
import me.sshcrack.fairylights.client.renderer.block.entity.*;
import me.sshcrack.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.sshcrack.fairylights.server.ServerProxy;
import me.sshcrack.fairylights.server.block.entity.FLBlockEntities;
import me.sshcrack.fairylights.server.item.FLItems;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public final class ClientProxy extends ServerProxy {
    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier SOLID_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(FairyLightsMod.ModID, "entity/connections"));

    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier TRANSLUCENT_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(FairyLightsMod.ModID, "entity/connections"));

    private final ImmutableList<Identifier> entityModels = new ImmutableList.Builder<Identifier>()
        .addAll(PennantBuntingRenderer.MODELS)
        .addAll(LetterBuntingRenderer.MODELS.values())
        .build();

    @Override
    public void init(final IEventBus modBus) {
        super.init(modBus);
        new ClippyController().init(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FLClientConfig.SPEC);
        ClientEventHandler clientEventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(clientEventHandler);
        modBus.<RegisterGuiOverlaysEvent>addListener(e -> {
            e.registerBelowAll("overlay", clientEventHandler::renderOverlay);
        });
        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent e) -> JinglerCommand.register(e.getDispatcher()));
        JinglerCommand.register(MinecraftForge.EVENT_BUS);
        modBus.<TextureStitchEvent.Pre>addListener(e -> {
            if (SOLID_TEXTURE.atlasLocation().equals(e.getAtlas().location())) {
                e.addSprite(SOLID_TEXTURE.texture());
            }
        });
        // Undo sprite uv shrink
        modBus.<ModelEvent.BakingCompleted>addListener(e -> {
            final VertexFormat vertexFormat = DefaultVertexFormat.BLOCK;
            final int size = vertexFormat.getIntegerSize();
            final int index = this.getUvIndex(vertexFormat);
            if (index != -1) {
                this.entityModels.forEach(path -> {
                    final BakedModel model = Minecraft.getInstance().getModelManager().getModel(path);
                    if (model != Minecraft.getInstance().getModelManager().getMissingModel()) {
                        this.recomputeUv(size, index, model);
                    }
                });
            }
        });
        modBus.addListener(this::setup);
        modBus.addListener(this::setupModelLayers);
        modBus.addListener(this::setupColors);
        modBus.addListener(this::setupModels);
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
        final Sprite sprite = model.getParticleIcon(ModelData.EMPTY);
        final int w = (int) (sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
        final int h = (int) (sprite.getHeight() / (sprite.getV1() - sprite.getV0()));
        for (final BakedQuad quad : model.getQuads(null, null, Random.create(42L), ModelTransform.NONE, RenderLayer.getCutoutMipped())) {
            final int[] data = quad.getVertices();
            for (int n = 0; n < 4; n++) {
                int iu = n * stride + finalUvOffset;
                int iv = n * stride + finalUvOffset + 1;
                data[iu] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iu]) * w) / w);
                data[iv] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iv]) * h) / h);
            }
        }
    }

    private void setup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(FLBlockEntities.FASTENER, context -> new FastenerBlockEntityRenderer(context, ServerProxy.buildBlockView()));
        BlockEntityRenderers.register(FLBlockEntities.LIGHT, LightBlockEntityRenderer::new);
        EntityRenderers.register(FLEntities.FASTENER.get(), FenceFastenerRenderer::new);
        /*final LightRenderer r = new LightRenderer();
        final StringBuilder bob = new StringBuilder();
        FLItems.lights().forEach(l -> {
            final LightModel<?> model = r.getModel(l.getBlock().getVariant(), -1);
            final AxisAlignedBB bb = model.getBounds();
            bob.append(String.format("%n%s new AxisAlignedBB(%.3fD, %.3fD, %.3fD, %.3fD, %.3fD, %.3fD), %.3fD", l.getRegistryName(), bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, model.getFloorOffset()));
        });
        LogManager.getLogger().debug("waldo {}", bob);*/
    }

    private void setupModelLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FLModelLayers.BOW, BowModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.GARLAND_RINGS, GarlandVineRenderer.RingsModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.TINSEL_STRIP, GarlandTinselRenderer.StripModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.FAIRY_LIGHT, FairyLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.PAPER_LANTERN, PaperLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.ORB_LANTERN, OrbLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.FLOWER_LIGHT, FlowerLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.CANDLE_LANTERN_LIGHT, ColorCandleLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.OIL_LANTERN_LIGHT, ColorOilLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.JACK_O_LANTERN, JackOLanternLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SKULL_LIGHT, SkullLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.GHOST_LIGHT, GhostLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SPIDER_LIGHT, SpiderLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.WITCH_LIGHT, WitchLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SNOWFLAKE_LIGHT, SnowflakeLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.HEART_LIGHT, HeartLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.MOON_LIGHT, MoonLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.STAR_LIGHT, StarLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_1, () -> IcicleLightsModel.createLayer(1));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_2, () -> IcicleLightsModel.createLayer(2));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_3, () -> IcicleLightsModel.createLayer(3));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_4, () -> IcicleLightsModel.createLayer(4));
        event.registerLayerDefinition(FLModelLayers.METEOR_LIGHT, MeteorLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.OIL_LANTERN, OilLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.CANDLE_LANTERN, CandleLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.INCANDESCENT_LIGHT, IncandescentLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.LETTER_WIRE, LetterBuntingRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.PENNANT_WIRE, PennantBuntingRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.TINSEL_WIRE, GarlandTinselRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.VINE_WIRE, GarlandVineRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.LIGHTS_WIRE, HangingLightsRenderer::wireLayer);
    }

    private void setupModels(final ModelEvent.RegisterAdditional event) {
        event.register(FenceFastenerRenderer.MODEL);
        this.entityModels.forEach(event::register);
    }

    private void setupColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> {
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
        event.register((stack, index) -> {
            final NbtCompound tag = stack.getNbt();
            if (index == 0) {
                if (tag != null) {
                    return HangingLightsConnectionItem.getString(tag).getColor();
                }
                return StringTypes.BLACK_STRING.getColor();
            }
            if (tag != null) {
                final NbtList tagList = tag.getList("pattern", Tag.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    final ItemStack item = ItemStack.of(tagList.getCompound((index - 1) % tagList.size()));
                    if (ColorChangingBehavior.exists(item)) {
                        return ColorChangingBehavior.animate(item);
                    }
                    return DyeableItem.getColor(item);
                }
            }
            if (FairyLights.CHRISTMAS.isOccurringNow()) {
                return (index + Util.getMillis() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
            }
            if (FairyLights.HALLOWEEN.isOccurringNow()) {
                return index % 2 == 0 ? 0xf9801d : 0x8932b8;
            }
            return 0xFFD584;
        }, FLItems.HANGING_LIGHTS.get());
        event.register((stack, index) -> index == 0 ? DyeableItem.getColor(stack) : 0xFFFFFFFF, FLItems.TINSEL.get());
        event.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFFFF;
            }
            final NbtCompound tag = stack.getNbt();
            if (tag != null) {
                final NbtList tagList = tag.getList("pattern", Tag.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    final ItemStack light = ItemStack.of(tagList.getCompound((index - 1) % tagList.size()));
                    return DyeableItem.getColor(light);
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.PENNANT_BUNTING.get());
        event.register(ClientProxy::secondLayerColor, FLItems.TRIANGLE_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SPEARHEAD_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SWALLOWTAIL_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SQUARE_PENNANT.get());
        event.register((stack, index) -> {
            final NbtCompound tag = stack.getNbt();
            if (index > 0 && tag != null) {
                final StyledString str = StyledString.deserialize(tag.getCompound("text"));
                if (str.length() > 0) {
                    ChatFormatting lastColor = null, color = null;
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
