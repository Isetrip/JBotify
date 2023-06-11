package com.isetrip.jbotify.managers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.isetrip.jbotify.annotations.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class ConfigurationManager {

    private final ObjectMapper objectMapper;

    public ConfigurationManager(List<Class<?>> classes) throws IllegalAccessException {
        this.objectMapper = new ObjectMapper();
        for (Class<?> clazz : classes) {
            loadConfiguration(clazz);
        }
    }

    public void loadConfiguration(Class<?> clazz) throws IllegalAccessException {
        Configuration configurationAnnotation = clazz.getAnnotation(Configuration.class);
        if (configurationAnnotation != null) {
            String file = configurationAnnotation.configFile();
            File config = new File(file);
            if (!config.exists()) return;
            try {
                Object obj = objectMapper.readValue(config, clazz);
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = objectMapper.convertValue(obj, field.getType());
                    field.set(clazz, value);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
