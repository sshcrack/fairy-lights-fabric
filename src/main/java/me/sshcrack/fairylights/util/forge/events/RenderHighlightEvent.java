package me.sshcrack.fairylights.util.forge.events;/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */


import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before a selection highlight is rendered.
 * See the two subclasses to listen for blocks or entities.
 *
 * @see Block
 * @see Entity
 */
public abstract class RenderHighlightEvent extends Event
{
    private final WorldRenderer levelRenderer;
    private final Camera camera;
    private final HitResult target;
    private final float partialTick;
    private final MatrixStack poseStack;
    private final VertexConsumerProvider multiBufferSource;

    @ApiStatus.Internal
    protected RenderHighlightEvent(WorldRenderer levelRenderer, Camera camera, HitResult target, float partialTick, MatrixStack poseStack, VertexConsumerProvider multiBufferSource)
    {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.target = target;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
    }

    /**
     * {@return the level renderer}
     */
    public WorldRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    /**
     * {@return the camera information}
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * {@return the hit result which triggered the selection highlight}
     */
    public HitResult getTarget()
    {
        return target;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the matrix stack used for rendering}
     */
    public net.minecraft.client.util.math.MatrixStack getMatrixStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public VertexConsumerProvider getMultiBufferSource()
    {
        return multiBufferSource;
    }

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * If the event is cancelled, then the selection highlight will not be rendered.</p>
     *
     * <p>This event is fired on the {@linkplain me.sshcrack.fairylights.FairyLightsMod#EVENT_BUS main Forge event bus},
     * only on the Client side</p>
     */
    public static class Block extends RenderHighlightEvent
    {
        @ApiStatus.Internal
        public Block(WorldRenderer levelRenderer, Camera camera, BlockHitResult target, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the block hit result}
         */
        @Override
        public BlockHitResult getTarget()
        {
            return (BlockHitResult) super.target;
        }
    }

    /**
     * Fired before an entity's selection highlight is rendered.
     *
     *
     * <p>This event is fired on the {@linkplain me.sshcrack.fairylights.FairyLightsMod#EVENT_BUS main Forge event bus},
     * only on the Client side.</p>
     */
    public static class Entity extends RenderHighlightEvent
    {
        @ApiStatus.Internal
        public Entity(WorldRenderer levelRenderer, Camera camera, EntityHitResult target, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the entity hit result}
         */
        @Override
        public EntityHitResult getTarget()
        {
            return (EntityHitResult) super.target;
        }
    }
}
