package com.isetrip.jbotify.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isetrip.jbotify.root.annotations.Configuration;
import com.isetrip.jbotify.root.annotations.Value;
import com.isetrip.jbotify.utils.ClassScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigurationManager {

    private final Gson gson;

    public ConfigurationManager(List<Class<?>> classes) throws FileNotFoundException, IllegalAccessException {
        this.gson = new Gson();
        for (Class<?> clazz : classes) {
            loadConfiguration(clazz);
        }
    }

    public void loadConfiguration(Class<?> clazz) throws FileNotFoundException, IllegalAccessException {
        Configuration configurationAnnotation = clazz.getAnnotation(Configuration.class);
        if (configurationAnnotation != null) {
            String file = configurationAnnotation.configFile();
            File config = new File(file);
            if (!config.exists()) return;
            Object obj = JsonParser.parseReader(new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8));
            JsonObject items = (JsonObject) obj;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Value.class)) {
                    Value confValue = field.getAnnotation(Value.class);
                    field.setAccessible(true);
                    Object value = gson.fromJson(items.get(confValue.name()), field.getType());
                    field.set(clazz, value);
                }
            }
        }
    }

}
