/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package me.sshcrack.fairylights.util.forge.items;

import me.sshcrack.fairylights.util.forge.items.wrapper.PlayerMainInvWrapper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerHelper
{
    @NotNull
    public static ItemStack insertItem(IItemHandler dest, @NotNull ItemStack stack, boolean simulate)
    {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++)
        {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasNbt() != b.hasNbt())
            return false;

        //TODO Caps
        return (!a.hasNbt() || a.getNbt().equals(b.getNbt())) /*&& a.areCapsCompatible(b)*/;
    }

    /**
     * A relaxed version of canItemStacksStack that stacks itemstacks with different metadata if they don't have subtypes.
     * This usually only applies when players pick up items.
     */
    public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;

        if (!a.isStackable())
            return false;

        // Metadata value only matters when the item has subtypes
        // Vanilla stacks non-subtype items with different metadata together
        // TODO Item subtypes, is this still necessary?
        /* e.g. a stick with metadata 0 and a stick with metadata 1 stack
        if (a.getHasSubtypes() && a.getMetadata() != b.getMetadata())
            return false;
*/
        if (a.hasNbt() != b.hasNbt())
            return false;

        //TODO Caps
        return (!a.hasNbt() || a.getNbt().equals(b.getNbt())) /*&& a.areCapsCompatible(b)*/;
    }

    @NotNull
    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size)
    {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    /**
     * Inserts the ItemStack into the inventory, filling up already present stacks first.
     * This is equivalent to the behaviour of a player picking up an item.
     * Note: This function stacks items without subtypes with different metadata together.
     */
    @NotNull
    public static ItemStack insertItemStacked(IItemHandler inventory, @NotNull ItemStack stack, boolean simulate)
    {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable())
        {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++)
        {
            ItemStack slot = inventory.getStackInSlot(i);
            if (canItemStacksStackRelaxed(slot, stack))
            {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty())
                {
                    break;
                }
            }
        }

        // insert remainder into empty slots
        if (!stack.isEmpty())
        {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++)
            {
                if (inventory.getStackInSlot(i).isEmpty())
                {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty())
                    {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    /** giveItemToPlayer without preferred slot */
    public static void giveItemToPlayer(PlayerEntity player, @NotNull ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    /**
     * Inserts the given itemstack into the players inventory.
     * If the inventory can't hold it, the item will be dropped in the world at the players position.
     *
     * @param player The player to give the item to
     * @param stack  The itemstack to insert
     */
    public static void giveItemToPlayer(PlayerEntity player, @NotNull ItemStack stack, int preferredSlot)
    {
        if (stack.isEmpty()) return;

        PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(player.getInventory());
        World level = player.getWorld();

        // try adding it into the inventory
        ItemStack remainder = stack;
        // insert into preferred slot first
        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots())
        {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }
        // then into the inventory in general
        if (!remainder.isEmpty())
        {
            remainder = insertItemStacked(inventory, remainder, false);
        }

        // play sound if something got picked up
        if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
        {
            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        // drop remaining itemstack into the level
        if (!remainder.isEmpty() && !level.isClient())
        {
            ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityitem.setPickupDelay(40);
            entityitem.setVelocity(entityitem.getVelocity().multiply(0, 1, 0));

            level.spawnEntity(entityitem);
        }
    }

    /**
     * This method uses the standard vanilla algorithm to calculate a comparator output for how "full" the inventory is.
     * This method is an adaptation of Container#calcRedstoneFromInventory(IInventory).
     * @param inv The inventory handler to test.
     * @return A redstone value in the range [0,15] representing how "full" this inventory is.
     */
    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv)
    {
        if (inv == null)
        {
            return 0;
        }
        else
        {
            int itemsFound = 0;
            float proportion = 0.0F;

            for (int j = 0; j < inv.getSlots(); ++j)
            {
                ItemStack itemstack = inv.getStackInSlot(j);

                if (!itemstack.isEmpty())
                {
                    proportion += (float)itemstack.getCount() / (float)Math.min(inv.getSlotLimit(j), itemstack.getMaxCount());
                    ++itemsFound;
                }
            }

            proportion = proportion / (float)inv.getSlots();
            return MathHelper.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
        }
    }
}
