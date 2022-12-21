package me.sshcrack.fairylights.server.net.clientbound;

import me.sshcrack.fairylights.server.capability.CapabilityHandler;
import me.sshcrack.fairylights.server.net.Message;
import me.sshcrack.fairylights.server.net_fabric.GeneralClientHandler;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class UpdateEntityFastenerMessage implements Message {
    private int entityId;

    private NbtCompound compound;

    public UpdateEntityFastenerMessage() {}

    public UpdateEntityFastenerMessage(final Entity entity, final NbtCompound compound) {
        this.entityId = entity.getId();
        this.compound = compound;
    }

    @Override
    public void encode(final PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeNbt(this.compound);
    }

    @Override
    public void decode(final PacketByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.compound = buf.readNbt();
    }

    //TODO maybe fix warning
    @SuppressWarnings("unchecked")
    public static final class Handler implements GeneralClientHandler<UpdateEntityFastenerMessage> {
        @Override
        public void accept(final UpdateEntityFastenerMessage message, final ClientPlayNetworkHandler context) {
            final Entity entity = context.getWorld().getEntityById(message.entityId);
            if (entity != null) {
                ((CapabilityHelper<Entity>) entity).getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
            }
        }
    }
}
