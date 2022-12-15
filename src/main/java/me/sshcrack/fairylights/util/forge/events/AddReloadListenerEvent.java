/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.events;

import com.google.common.collect.ImmutableList;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * The main ResourceManager is recreated on each reload, just after {@link DataPackContents}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link me.sshcrack.fairylights.FairyLightsMod#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    private final List<ResourceReloader> listeners = new ArrayList<>();
    private final DataPackContents serverResources;

    public AddReloadListenerEvent(DataPackContents serverResources)
    {
        this.serverResources = serverResources;
    }

   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
    public void addListener(ResourceReloader listener)
    {
       listeners.add(new WrappedStateAwareListener(listener));
    }

    public List<ResourceReloader> getListeners()
    {
       return ImmutableList.copyOf(listeners);
    }

    /**
     * @return The ReloableServerResources being reloaded.
     */
    public DataPackContents getServerResources()
    {
        return serverResources;
    }

    /**
     * This context object holds data relevant to the current reload, such as staged tags.
     * @return The condition context for the currently active reload.
     */
    /*public ICondition.IContext getConditionContext()
    {
        return serverResources.getConditionContext();
    }*/

    private static class WrappedStateAwareListener implements ResourceReloader {
        private final ResourceReloader wrapped;

        private WrappedStateAwareListener(final ResourceReloader wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public CompletableFuture<Void> reload(final Synchronizer stage, final ResourceManager resourceManager, final Profiler preparationsProfiler, final Profiler reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor) {
            //if (ModLoader.isLoadingStateValid())
                return wrapped.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            //else
            //    return CompletableFuture.completedFuture(null);
        }
    }
}
