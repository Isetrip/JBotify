package com.isetrip.jbotify.events;

import com.isetrip.jbotify.events.elements.core.Event;
import com.isetrip.jbotify.root.annotations.BotEventHandler;
import com.isetrip.jbotify.utils.MethodFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventsRegister {

    private final List<Object> handlers;

    public EventsRegister() {
        this.handlers = new ArrayList<>();
    }

    public void register(Class<?> handlerClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (handlerClass.isAnnotationPresent(BotEventHandler.class)) {
            Constructor<?> constructor = handlerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            this.handlers.add(instance);
        } else {
            throw new IllegalArgumentException("Class must be annotated with @BotEventHandler");
        }
    }


    public void publish(Event event) {
        for (Object eventHandler : this.handlers) {
            Class<?> clazz = eventHandler.getClass();
            Class<?> argumentType = event.getClass();
            Method method = MethodFinder.findMethodWithArgument(clazz, argumentType);
            if (method != null) {
                Object[] methodArgs = {event};
                try {
                    method.invoke(eventHandler, methodArgs);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
