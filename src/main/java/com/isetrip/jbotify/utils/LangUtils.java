package com.isetrip.jbotify.utils;

import com.isetrip.jbotify.JBotifyApplication;
import com.isetrip.jbotify.lang.Lang;

import java.util.HashSet;
import java.util.Set;

public class LangUtils {

    public static Lang getLangFromButton(String text) {
        for (Lang lang : JBotifyApplication.getLangManager().getLangs())
            if (lang.button().equals(text)) return lang;
        return null;
    }

    public static String[] getLangsButtons() {
        Set<String> result = new HashSet<>();
        for (Lang lang : JBotifyApplication.getLangManager().getLangs())
            result.add(lang.button());
        return result.toArray(new String[0]);
    }

    public static String get(Lang lang, String key) {
        return JBotifyApplication.getLangManager().getMessage(lang, key);
    }

    public static Lang of(String key) {
        return JBotifyApplication.getLangManager().get(key);
    }

}
