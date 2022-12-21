/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.events;

import net.minecraft.world.WorldAccess;


/**
 * This event is fired whenever an event involving a {@link WorldAccess} occurs.
 * <p>
 */
public class WorldEvent extends Event
{
    private final WorldAccess level;

    public WorldEvent(WorldAccess level)
    {
        this.level = level;
    }

    /**
     * {@return the level this event is affecting}
     */
    public WorldAccess getLevel()
    {
        return level;
    }

    /**
     * This event is fired whenever a level loads.
     * This event is fired whenever a level loads in ClientLevel's constructor and
     * {@literal MinecraftServer#createLevels(ChunkProgressListener)}.
     * <p>
     * on both logical sides.
     **/
    public static class Load extends WorldEvent
    {
        public Load(WorldAccess level) { super(level); }
    }
}
