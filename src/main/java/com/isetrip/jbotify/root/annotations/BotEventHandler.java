package com.isetrip.jbotify.root.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BotEventHandler {

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Listener {

    }

}
