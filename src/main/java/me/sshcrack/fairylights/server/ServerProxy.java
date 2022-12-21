package me.sshcrack.fairylights.server;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.event.ServerEventHandler;
import me.sshcrack.fairylights.server.fastener.BlockView;
import me.sshcrack.fairylights.server.fastener.CreateBlockViewEvent;
import me.sshcrack.fairylights.server.fastener.RegularBlockView;
import me.sshcrack.fairylights.server.net.Message;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ServerProxy {
    public void init() {
        FairyLightsMod.EVENT_BUS.registerEventHandler(new ServerEventHandler());
    }

    public static void sendToPlayersWatchingChunk(final Identifier id, final Message message, final World world, BlockPos pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        message.encode(buf);

        Chunk chunk = world.getChunk(pos);
        ServerChunkManager manager = (ServerChunkManager)world.getChunkManager();
        for (ServerPlayerEntity player : manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(chunk.getPos())) {
            ServerPlayNetworking.send(player, id, buf);
        }
    }

    public static void sendToPlayersWatchingEntity(final Identifier id, final Message message, final Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        message.encode(buf);

        Packet<?> packet = ServerPlayNetworking.createS2CPacket(id, buf);

        ServerChunkManager manager = (ServerChunkManager)entity.getEntityWorld().getChunkManager();
        manager.sendToNearbyPlayers(entity, packet);
    }

    public static BlockView buildBlockView() {
        final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
        FairyLightsMod.EVENT_BUS.fireEvent(evt);
        return evt.getView();
    }
}
