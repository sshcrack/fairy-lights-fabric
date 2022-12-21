/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.client.model;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container for data to be passed to {@link BakedModel} instances.
 * <p>
 * All objects stored in here <b>MUST BE IMMUTABLE OR THREAD-SAFE</b>.
 * Properties will be accessed from another thread.
 *
 * @see ModelProperty
 * @see BakedModel#getQuads(BlockState, Direction, Random)
 */
public final class ModelData
{
    public static final ModelData EMPTY = ModelData.builder().build();

    private final Map<ModelProperty<?>, Object> properties;

    private ModelData(Map<ModelProperty<?>, Object> properties)
    {
        this.properties = properties;
    }

    public Set<ModelProperty<?>> getProperties()
    {
        return properties.keySet();
    }

    public boolean has(ModelProperty<?> property)
    {
        return properties.containsKey(property);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(ModelProperty<T> property)
    {
        return (T) properties.get(property);
    }

    public Builder derive()
    {
        return new Builder(this);
    }

    public static Builder builder()
    {
        return new Builder(null);
    }

    public static final class Builder
    {
        private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap<>();

        private Builder(@Nullable ModelData parent)
        {

            if (parent != null)
            {
                properties.putAll(parent.properties);
            }
        }

        @Contract("_, _ -> this")
        public <T> Builder with(ModelProperty<T> property, T value)
        {
            Preconditions.checkState(property.test(value), "The provided value is invalid for this property.");
            properties.put(property, value);
            return this;
        }

        @Contract("-> new")
        public ModelData build()
        {
            return new ModelData(Collections.unmodifiableMap(properties));
        }
    }
}
