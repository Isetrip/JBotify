package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.JBotifyHandler;
import com.isetrip.jbotify.events.elements.core.Event;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;

public class CallbackQueryEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private Message message;
    @Getter
    private CallbackQuery callbackQuery;

    public CallbackQueryEvent(JBotifyHandler handler, Update update, CallbackQuery callbackQuery) {
        super(handler);
        this.update = update;
        this.callbackQuery = callbackQuery;
        this.message = callbackQuery.message();
    }
}