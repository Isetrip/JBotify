package com.isetrip.jbotify.lang;

import com.isetrip.jbotify.JBotifyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LangManager {
    private static final String LANG_DIR = "lang";
    private static final String FILE_EXTENSION = ".lang";
    private final Map<String, Properties> langProperties;
    private final Map<String, Lang> langs;

    public LangManager() {
        this.langProperties = new HashMap<>();
        this.langs = new HashMap<>();
    }

    public String getMessage(Lang lang, String name) {
        Properties properties = langProperties.get(lang.name());
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }

    public void loadLanguageProperties(Lang lang) {
        InputStream inputStream = JBotifyApplication.class.getResourceAsStream(String.format("/%s/%s%s", LANG_DIR, lang.name(), FILE_EXTENSION));

        Properties properties = new Properties();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(reader);
            this.langProperties.put(lang.name(), properties);
            this.langs.put(lang.name(), lang);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Lang> getLangs() {
        return this.langs.values();
    }

    public Lang get(String lang) {
        return this.langs.get(lang);
    }

}
