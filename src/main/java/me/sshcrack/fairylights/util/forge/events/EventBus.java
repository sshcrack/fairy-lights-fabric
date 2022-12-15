package me.sshcrack.fairylights.util.forge.events;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.events.Event;
import me.sshcrack.fairylights.util.forge.events.ListenerInfo;
import me.sshcrack.fairylights.util.forge.events.annotations.SubscribeEvent;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class EventBus {
    private final Map<Class<?>, ArrayList<ListenerInfo>> listeners = new HashMap<>();

    public void registerEventHandler(Object obj) {
        final HashSet<Class<?>> classes = new HashSet<>();
        typesFor(obj.getClass(), classes);

        Arrays.stream(obj.getClass().getMethods())
                .filter(e -> !Modifier.isStatic(e.getModifiers()))
                .forEach(m -> classes.stream()
                        .map(c -> getDeclMethod(c, m))
                        .filter(rm -> rm.isPresent() && rm.get().isAnnotationPresent(SubscribeEvent.class))
                        .findFirst()
                        .ifPresent(rm -> registerListener(obj, m, rm.get()))
                );
    }

    public void fireEvent(Event event) {
        Class<?> eventClass = event.getClass();
        if (!listeners.containsKey(eventClass))
            return;

        listeners.get(eventClass)
                .forEach(e -> {
                    try {
                        FairyLightsMod.LOGGER.info("Invoking method");
                        e.getMethod().invoke(e.getContext(), event);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        String exception = Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .collect(Collectors.joining("\n"));
                        FairyLightsMod.LOGGER.error(String.format("Could not fire Event %s: %s", eventClass.getName(), exception));
                    }
                });
    }

    public void registerListener(Object target, Method method, Method realMethod) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation. " +
                            "It has " + parameterTypes.length + " arguments, " +
                            "but event handler methods require a single argument only."
            );
        }

        Class<?> eventType = parameterTypes[0];
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation, " +
                            "but takes an argument that is not an Event subtype : " + eventType);
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Failed to create ASMEventHandler for " + target.getClass().getName() + "." + method.getName() + Type.getMethodDescriptor(method) + " it is not public and our transformer is disabled");
        }

        register(eventType, target, realMethod);
    }


    private void register(Class<?> eventType, Object target, Method method) {
        ArrayList<ListenerInfo> methods = new ArrayList<>();
        if (listeners.containsKey(eventType))
            methods = listeners.get(eventType);

        methods.add(new ListenerInfo(target, method));
        listeners.put(eventType, methods);
    }


    private void typesFor(final Class<?> clz, final Set<Class<?>> visited) {
        if (clz.getSuperclass() == null) return;
        typesFor(clz.getSuperclass(), visited);
        Arrays.stream(clz.getInterfaces()).forEach(i -> typesFor(i, visited));
        visited.add(clz);
    }

    private Optional<Method> getDeclMethod(final Class<?> clz, final Method in) {
        try {
            return Optional.of(clz.getDeclaredMethod(in.getName(), in.getParameterTypes()));
        } catch (NoSuchMethodException nse) {
            return Optional.empty();
        }

    }
}
