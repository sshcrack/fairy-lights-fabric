package me.sshcrack.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.server.fastener.CreateBlockViewEvent;
import me.paulf.fairylights.server.fastener.RegularBlockView;
import me.paulf.fairylights.server.jingle.JingleManager;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.fastener.CreateBlockViewEvent;
import me.sshcrack.fairylights.server.fastener.RegularBlockView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;

public class ServerProxy {
    public void init(final IEventBus modBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FLConfig.GENERAL_SPEC);
        MinecraftForge.EVENT_BUS.<AddReloadListenerEvent>addListener(e -> {
            e.addListener(JingleManager.INSTANCE);
        });
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        modBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityHandler.register();
    }

    public static void sendToPlayersWatchingChunk(final Object message, final Level world, final BlockPos pos) {
        FairyLightsMod.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static void sendToPlayersWatchingEntity(final Object message, final Entity entity) {
        FairyLightsMod.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public static BlockView buildBlockView() {
        final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.getView();
    }

    public void initIntegration() {
		/*if (Loader.isModLoaded(ValkyrienWarfareMod.MODID)) {
			final Class<?> vw;
			try {
				vw = Class.forName("ValkyrienWarfare");
			} catch (final ClassNotFoundException e) {
				throw new AssertionError(e);
			}
			MinecraftForge.EVENT_BUS.register(vw);
		}*/
    }
}
