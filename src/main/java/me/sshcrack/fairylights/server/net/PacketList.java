package me.sshcrack.fairylights.server.net;

import com.google.common.collect.Lists;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.net.clientbound.JingleMessage;
import me.sshcrack.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.sshcrack.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.sshcrack.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.sshcrack.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.sshcrack.fairylights.server.net_fabric.ClientTriplet;
import me.sshcrack.fairylights.server.net_fabric.ServerTriplet;
import net.minecraft.util.Identifier;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;

public class PacketList {
    public static final String CHANNEL_PREFIX = "main";

    public static final ServerTriplet<? extends Message, ?> C2S_EDIT_LETTERED = new ServerTriplet<>("edit_lettered", EditLetteredConnectionMessage::new, EditLetteredConnectionMessage.Handler::new);
    public static final ServerTriplet<? extends Message, ?> C2S_INTERACTION = new ServerTriplet<>("interaction", InteractionConnectionMessage::new, InteractionConnectionMessage.Handler::new);

    public static final ArrayList<ServerTriplet<? extends Message, ?>> SERVER_PACKETS = Lists.newArrayList(
            C2S_EDIT_LETTERED,
            C2S_INTERACTION
    );

    public static final ClientTriplet<? extends Message, ?> S2C_JINGLE = new ClientTriplet<>("jingle", JingleMessage::new, JingleMessage.Handler::new);
    public static final ClientTriplet<? extends Message, ?> S2C_OPEN_EDIT_LETTERED = new ClientTriplet<>("open_edit_lettered", OpenEditLetteredConnectionScreenMessage::new, OpenEditLetteredConnectionScreenMessage.Handler::new);
    public static final ClientTriplet<? extends Message, ?> S2C_UPDATE_ENTITY = new ClientTriplet<>("update_entity_fastener", UpdateEntityFastenerMessage::new, UpdateEntityFastenerMessage.Handler::new);

    public static final ArrayList<ClientTriplet<? extends Message, ?>> CLIENT_PACKETS = Lists.newArrayList(
            S2C_JINGLE,
            S2C_OPEN_EDIT_LETTERED,
            S2C_UPDATE_ENTITY
    );

    public static Identifier getId(Triplet<String, ?, ?> triplet) {
        return new Identifier(FairyLightsMod.ModID, String.format("%s:%s", PacketList.CHANNEL_PREFIX, triplet.getA()));
    }
}
