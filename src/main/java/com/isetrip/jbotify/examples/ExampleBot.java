package com.isetrip.jbotify.examples;

import com.isetrip.jbotify.JBotifyApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ExampleBot {

    public static void main(String... args) throws TelegramApiException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JBotifyApplication.run(ExampleBot.class, args);
    }

}
