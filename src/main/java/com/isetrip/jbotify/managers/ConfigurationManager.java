package com.isetrip.jbotify.managers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.isetrip.jbotify.annotations.Configuration;
import com.isetrip.jbotify.annotations.Instance;
import com.isetrip.jbotify.annotations.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
public class ConfigurationManager {

    private final ObjectMapper objectMapper;

    public ConfigurationManager(List<Class<?>> classes) throws IllegalAccessException {
        this.objectMapper = new ObjectMapper();
        for (Class<?> clazz : classes) {
            try {
                loadConfiguration(clazz);
            } catch (IOException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadConfiguration(Class<?> clazz) throws IllegalAccessException, IOException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Configuration configurationAnnotation = clazz.getAnnotation(Configuration.class);
        if (configurationAnnotation != null) {
            String file = configurationAnnotation.configFile();
            File config = new File(file);
            if (!config.exists()) return;
            JsonNode jsonNode = objectMapper.readTree(config);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Value.class)) continue;
                field.setAccessible(true);
                String valueName = field.getAnnotation(Value.class).value();
                if (!jsonNode.has(valueName)) continue;
                JsonNode obj = jsonNode.get(valueName);
                Object value = objectMapper.convertValue(obj, field.getType());
                field.set(getInstance(clazz), value);
            }
        }
    }

    public static Object getInstance(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Instance.class) && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    return field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance();
    }

}
