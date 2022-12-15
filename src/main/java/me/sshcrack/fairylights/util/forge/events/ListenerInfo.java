package me.sshcrack.fairylights.util.forge.events;

import java.lang.reflect.Method;

public class ListenerInfo {
    private final Method method;
    private final Object context;

    public ListenerInfo(Object context, Method method) {
        this.context = context;
        this.method = method;
    }

    public Object getContext() {
        return context;
    }

    public Method getMethod() {
        return method;
    }
}
