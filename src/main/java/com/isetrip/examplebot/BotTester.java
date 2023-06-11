package com.isetrip.examplebot;

import com.isetrip.jbotify.JBotifyApplication;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BotTester {

    public static void main(String... args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JBotifyApplication.run(BotTester.class, args);
    }

}
