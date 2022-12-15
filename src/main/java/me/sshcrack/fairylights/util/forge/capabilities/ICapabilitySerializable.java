/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.capabilities;

import net.minecraft.nbt.NbtCompound;

//Just a mix of the two, useful in patches to lower the size.
public interface ICapabilitySerializable<T extends NbtCompound> extends ICapabilityProvider, INBTSerializable<T>{}
