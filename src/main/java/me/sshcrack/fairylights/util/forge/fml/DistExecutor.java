/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.fml;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

/**
 * Use to execute code conditionally based on sidedness.
 * <ul>
 * <li>When you want to run something on one side {@link #unsafeRunForDist(Supplier, Supplier)}</li>
 * </ul>
 */
public final class DistExecutor
{
    /**
     * Unsafe version of {@link #unsafeRunForDist(Supplier, Supplier)}. Use only when you know what you're doing
     * and understand why the verifier can cause unexpected ClassNotFoundException crashes even when code is apparently
     * not sided. Ensure you test both sides fully to be confident in using this.
     *
     * @param clientTarget The supplier supplier to run when on the {@link EnvType#CLIENT}
     * @param serverTarget The supplier supplier to run when on the {@link EnvType#SERVER}
     * @param <T> The common type to return
     * @return The returned instance
     */
    public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        EnvType curr = MinecraftClient.getInstance() == null ? EnvType.SERVER : EnvType.CLIENT;
        switch (curr)
        {
            case CLIENT:
                return clientTarget.get().get();
            case SERVER:
                return serverTarget.get().get();
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }

}
