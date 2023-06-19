package com.isetrip.jbotify.utils;

import com.isetrip.jbotify.managers.LangManager;
import com.pengrad.telegrambot.model.User;

public class LangUtils {

    public static String get(String lang, String key) {
        return LangManager.getInstance().getMessage(LangManager.getInstance().getLang(lang), key);
    }

    public static String get(User user, String key) {
        return get(user.languageCode(), key);
    }
}
