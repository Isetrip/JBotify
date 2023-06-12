package com.isetrip.jbotify.events.elements.core;

import com.isetrip.jbotify.JBotifyApplication;
import com.isetrip.jbotify.JBotifyHandler;
import com.pengrad.telegrambot.TelegramBot;
import lombok.Getter;

public abstract class Event {

    @Getter
    private JBotifyHandler handler;

    public Event(JBotifyHandler handler) {
        this.handler = handler;
    }

    public TelegramBot getBot() {
        return JBotifyApplication.getBot();
    }

}
