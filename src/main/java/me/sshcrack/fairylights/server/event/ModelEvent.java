/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.server.event;

import me.sshcrack.fairylights.util.forge.events.Event;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Houses events related to models.
 */
public abstract class ModelEvent extends Event
{
    @ApiStatus.Internal
    protected ModelEvent()
    {
    }

    /**
     * Fired when the {@link BakedModelManager} is notified of the resource manager reloading.
     */
    public static class BakingCompleted extends ModelEvent
    {
        private final BakedModelManager modelManager;
        private final Map<Identifier, BakedModel> models;
        private final ModelLoader modelBakery;

        @ApiStatus.Internal
        public BakingCompleted(BakedModelManager modelManager, Map<Identifier, BakedModel> models, ModelLoader modelBakery)
        {
            this.modelManager = modelManager;
            this.models = models;
            this.modelBakery = modelBakery;
        }

        /**
         * @return the model manager
         */
        public BakedModelManager getModelManager()
        {
            return modelManager;
        }

        /**
         * @return the modifiable registry map of models and their model names
         */
        public Map<Identifier, BakedModel> getModels()
        {
            return models;
        }

        /**
         * @return the model loader
         */
        public ModelLoader getModelBakery()
        {
            return modelBakery;
        }
    }
}
