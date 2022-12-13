/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package me.sshcrack.fairylights.util.forge.items.wrapper;

import me.sshcrack.fairylights.util.forge.items.RangedWrapper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper
{
    private final PlayerInventory inventoryPlayer;

    public PlayerMainInvWrapper(PlayerInventory inv)
    {
        super(new InvWrapper(inv), 0, inv.main.size());
        inventoryPlayer = inv;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest.getCount()!= stack.getCount())
        {
            // the stack in the slot changed, animate it
            ItemStack inSlot = getStackInSlot(slot);
            if(!inSlot.isEmpty())
            {
                if (getInventoryPlayer().player.world.isClient())
                {
                    inSlot.setBobbingAnimationTime(5);
                }
                else if(getInventoryPlayer().player instanceof ServerPlayerEntity) {
                    getInventoryPlayer().player.currentScreenHandler.sendContentUpdates();
                }
            }
        }
        return rest;
    }

    public PlayerInventory getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
