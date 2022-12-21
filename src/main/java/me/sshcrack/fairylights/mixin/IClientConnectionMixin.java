package me.sshcrack.fairylights.mixin;

import io.netty.channel.Channel;

public interface IClientConnectionMixin {
    Channel getChannel();
}
