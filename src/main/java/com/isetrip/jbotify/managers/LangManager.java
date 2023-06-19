package com.isetrip.jbotify.managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LangManager {

    private static LangManager instance;
    private static final String LANG_DIR = "lang";
    private static final String FILE_EXTENSION = ".lang";
    private final Map<String, Properties> langProperties;

    public LangManager() {
        this.langProperties = new HashMap<>();
        instance = this;
    }

    public String getMessage(String lang, String name) {
        Properties properties = this.langProperties.get(lang);
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }

    public void loadLanguageProperties(String file) {
        InputStream inputStream = LangManager.class.getResourceAsStream(String.format("/%s/%s%s", LANG_DIR, file, FILE_EXTENSION));

        Properties properties = new Properties();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(reader);
            this.langProperties.put(file, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getLangs() {
        return this.langProperties.keySet();
    }

    public String getLang(String lang) {
        return this.langProperties.containsKey(lang) ? lang : "en";
    }

    public static LangManager getInstance() {
        return instance;
    }
}
