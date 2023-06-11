package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.JBotifyHandler;
import com.isetrip.jbotify.events.elements.core.Event;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.WebAppData;
import lombok.Getter;

public class WebAppDataReceiveEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private WebAppData webAppData;

    public WebAppDataReceiveEvent(JBotifyHandler handler, Update update, WebAppData webAppData) {
        super(handler);
        this.update = update;
        this.webAppData = webAppData;
    }
}