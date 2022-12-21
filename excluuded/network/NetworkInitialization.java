/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import me.sshcrack.fairylights.util.forge.network.event.EventNetworkChannel;
import me.sshcrack.fairylights.util.forge.network.simple.SimpleChannel;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.registries.RegistryManager;

import java.util.Arrays;
import java.util.List;

class NetworkInitialization {

    public static SimpleChannel getPlayChannel() {
         SimpleChannel playChannel = NetworkRegistry.ChannelBuilder.
                named(NetworkConstants.FML_PLAY_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkConstants.NETVERSION).
                simpleChannel();

        playChannel.messageBuilder(PlayMessages.SpawnEntity.class, 0).
                decoder(PlayMessages.SpawnEntity::decode).
                encoder(PlayMessages.SpawnEntity::encode).
                consumerNetworkThread(PlayMessages.SpawnEntity::handle).
                add();

        playChannel.messageBuilder(PlayMessages.OpenContainer.class,1).
                decoder(PlayMessages.OpenContainer::decode).
                encoder(PlayMessages.OpenContainer::encode).
                consumerNetworkThread(PlayMessages.OpenContainer::handle).
                add();

        return playChannel;
    }
}
