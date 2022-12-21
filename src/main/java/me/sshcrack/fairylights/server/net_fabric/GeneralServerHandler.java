package me.sshcrack.fairylights.server.net_fabric;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.function.BiConsumer;

public interface GeneralServerHandler<T> extends BiConsumer<T, ServerPlayNetworkHandler> { }
