/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.capabilities;


import net.minecraft.nbt.NbtCompound;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSerializable<T extends NbtCompound>
{
    T serializeNBT();
    void deserializeNBT(T nbt);
}
