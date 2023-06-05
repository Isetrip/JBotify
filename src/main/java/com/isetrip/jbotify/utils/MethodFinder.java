package com.isetrip.jbotify.utils;

import java.lang.reflect.Method;
public class MethodFinder {

    public static Method findMethodWithArgument(Class<?> clazz, Class<?> argumentType) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.equals(argumentType)) {
                    return method;
                }
            }
        }
        return null;
    }
}