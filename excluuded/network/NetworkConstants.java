
/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import io.netty.util.AttributeKey;
import me.sshcrack.fairylights.util.forge.network.event.EventNetworkChannel;
import me.sshcrack.fairylights.util.forge.network.simple.SimpleChannel;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
/**
 * Constants related to networking
 */
public class NetworkConstants
{
    public static final String FMLNETMARKER = "FML";
    /**
     */
    public static final int FMLNETVERSION = 3;
    public static final String NETVERSION = FMLNETMARKER + FMLNETVERSION;
    public static final String NOVERSION = "NONE";

    static final Marker NETWORK = MarkerManager.getMarker("FMLNETWORK");
    static final AttributeKey<String> FML_NETVERSION = AttributeKey.valueOf("fml:netversion");
    static final AttributeKey<HandshakeHandler> FML_HANDSHAKE_HANDLER = AttributeKey.valueOf("fml:handshake");
    static final AttributeKey<MCRegisterPacketHandler.ChannelList> FML_MC_REGISTRY = AttributeKey.valueOf("minecraft:netregistry");
    static final AttributeKey<ConnectionData> FML_CONNECTION_DATA = AttributeKey.valueOf("fml:conndata");
    static final AttributeKey<ConnectionData.ModMismatchData> FML_MOD_MISMATCH_DATA = AttributeKey.valueOf("fml:mismatchdata");
    static final Identifier MC_REGISTER_RESOURCE = new Identifier("minecraft:register");
    static final Identifier FML_PLAY_RESOURCE = new Identifier("fml:play");
    static final Identifier FML_HANDSHAKE_RESOURCE = new Identifier("fml:handshake");
    static final SimpleChannel playChannel = NetworkInitialization.getPlayChannel();

    public static String init() {
        return NetworkConstants.NETVERSION;
    }
}
