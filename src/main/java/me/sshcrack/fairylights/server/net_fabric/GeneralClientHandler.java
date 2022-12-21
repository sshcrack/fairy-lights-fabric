package me.sshcrack.fairylights.server.net_fabric;

import me.sshcrack.fairylights.server.net.Message;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.function.BiConsumer;

public interface GeneralClientHandler<T> extends BiConsumer<T, ClientPlayNetworkHandler> { }
