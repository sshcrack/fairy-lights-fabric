/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ICustomPacket<T extends Packet<?>> {
    /**
     * Returns a unsafe reference to this packet's internal data.
     * Any modifications to this buffer will be reflected in the main buffer.
     */
    @Nullable
    PacketByteBuf getInternalData();

    Identifier getName();

    int getIndex();

    default NetworkDirection getDirection() {
        return NetworkDirection.directionFor(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default T getThis() {
        return (T)this;
    }
}
