/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package me.sshcrack.fairylights.util.forge.events.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to subscribe a method to an {@link me.sshcrack.fairylights.util.forge.events.Event}
 *
 * This annotation can only be applied to single parameter methods, where the single parameter is a subclass of
 * {@link me.sshcrack.fairylights.util.forge.events.Event}.
 *
 * Use {@link me.sshcrack.fairylights.util.forge.events.EventBus#registerEventHandler(Object)} to submit either an Object instance or a {@link Class} to the event bus
 *
 * The Event Bus system generates an ASM wrapper that dispatches to the marked method.
 */
@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeEvent
{
    boolean receiveCanceled() default false;
}
