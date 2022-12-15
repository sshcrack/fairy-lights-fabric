package me.sshcrack.fairylights.server;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.config.FLConfig;
import me.sshcrack.fairylights.server.fastener.CreateBlockViewEvent;
import me.sshcrack.fairylights.server.fastener.RegularBlockView;
import me.sshcrack.fairylights.util.forge.events.AddReloadListenerEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerProxy {
    public void init(final IEventBus modBus) {
        FairyLightsMod.EVENT_BUS.<AddReloadListenerEvent>addListener(e -> {
            e.addListener(JingleManager.INSTANCE);
        });
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        modBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityHandler.register();
    }

    public static void sendToPlayersWatchingChunk(final Object message, final World world, final BlockPos pos) {
        FairyLightsMod.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(pos)), message);
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
