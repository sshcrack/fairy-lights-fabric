package me.sshcrack.fairylights.server.net.clientbound;

import me.sshcrack.fairylights.server.net.ClientMessageContext;
import me.sshcrack.fairylights.server.net.Message;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.function.BiConsumer;

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

    public static final class Handler implements BiConsumer<UpdateEntityFastenerMessage, ClientMessageContext> {
        @Override
        public void accept(final UpdateEntityFastenerMessage message, final ClientMessageContext context) {
            final Entity entity = context.getWorld().getEntityById(message.entityId);
            if (entity != null) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
            }
        }
    }
}
