package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.JBotifyHandler;
import com.isetrip.jbotify.events.elements.core.Event;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;

public class UnknownCommandEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private Message message;

    public UnknownCommandEvent(JBotifyHandler handler, Update update, Message message) {
        super(handler);
        this.update = update;
        this.message = message;
    }
}