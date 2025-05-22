package me.sshcrack.fairylights.util;

import io.netty.channel.Channel;

public interface IClientConnectionMixin {
    Channel getChannel();
}
