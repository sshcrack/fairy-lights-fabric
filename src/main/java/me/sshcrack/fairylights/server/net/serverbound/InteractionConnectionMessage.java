package me.sshcrack.fairylights.server.net.serverbound;

import me.sshcrack.fairylights.server.collision.Intersection;
import me.sshcrack.fairylights.server.connection.Connection;
import me.sshcrack.fairylights.server.connection.PlayerAction;
import me.sshcrack.fairylights.server.feature.FeatureType;
import me.sshcrack.fairylights.server.net.ConnectionMessage;
import me.sshcrack.fairylights.server.net_fabric.GeneralServerHandler;
import me.sshcrack.fairylights.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class InteractionConnectionMessage extends ConnectionMessage {
    private static final float RANGE = (Connection.MAX_LENGTH + 1) * (Connection.MAX_LENGTH + 1);

    private static final float REACH = 6 * 6;

    private PlayerAction type;

    private Vec3d hit;

    private FeatureType featureType;

    private int featureId;

    public InteractionConnectionMessage() {}

    public InteractionConnectionMessage(final Connection connection, final PlayerAction type, final Intersection intersection) {
        super(connection);
        this.type = type;
        this.hit = intersection.getResult();
        this.featureType = intersection.getFeatureType();
        this.featureId = intersection.getFeature().getId();
    }

    @Override
    public void encode(final PacketByteBuf buf) {
        super.encode(buf);
        buf.writeByte(this.type.ordinal());
        buf.writeDouble(this.hit.x);
        buf.writeDouble(this.hit.y);
        buf.writeDouble(this.hit.z);
        buf.writeVarInt(this.featureType.getId());
        buf.writeVarInt(this.featureId);
    }

    @Override
    public void decode(final PacketByteBuf buf) {
        super.decode(buf);
        this.type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
        this.hit = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.featureType = FeatureType.fromId(buf.readVarInt());
        this.featureId = buf.readVarInt();
    }

    public static final class Handler implements GeneralServerHandler<InteractionConnectionMessage> {
        @Override
        public void accept(final InteractionConnectionMessage message, final ServerPlayNetworkHandler context) {
            final ServerPlayerEntity player = context.getPlayer();
            getConnection(message, c -> true, player.world).ifPresent(connection -> {
                if (connection.isModifiable(player) &&
                    player.squaredDistanceTo(Vec3d.of(connection.getFastener().getPos())) < RANGE &&
                    player.squaredDistanceTo(message.hit.x, message.hit.y, message.hit.z) < REACH
                ) {
                    if (message.type == PlayerAction.ATTACK) {
                        connection.disconnect(player, message.hit);
                    } else {
                        this.interact(message, player, connection, message.hit);
                    }
                }
            });
        }

        private void interact(final InteractionConnectionMessage message, final PlayerEntity player, final Connection connection, final Vec3d hit) {
            for (final Hand hand : Hand.values()) {
                final ItemStack stack = player.getStackInHand(hand);
                final ItemStack oldStack = stack.copy();
                if (connection.interact(player, hit, message.featureType, message.featureId, stack, hand)) {
                    this.updateItem(player, oldStack, stack, hand);
                    break;
                }
            }
        }

        private void updateItem(final PlayerEntity player, final ItemStack oldStack, final ItemStack stack, final Hand hand) {
            if (stack.getCount() <= 0 && !player.getAbilities().creativeMode) {
                // TODO not needed as there are no listeners
                //ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
                player.setStackInHand(hand, ItemStack.EMPTY);
            } else if (stack.getCount() < oldStack.getCount() && player.getAbilities().creativeMode) {
                stack.setCount(oldStack.getCount());
            }
        }
    }
}
