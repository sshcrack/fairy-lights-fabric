/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.capabilities;

import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ICapabilityProvider
{
    /**
     * Retrieves the Optional handler for the capability requested on the specific side.
     * The return value <strong>CAN</strong> be the same for multiple faces.
     * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
     * be notified if the requested capability get lost.
     *
     * @param cap The capability to check
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return The requested an optional holding the requested capability.
     */
    @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap);
}
