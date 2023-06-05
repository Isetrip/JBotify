package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.events.elements.core.Event;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallbackQueryEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private Message message;
    @Getter
    private CallbackQuery callbackQuery;
    @Getter
    private String chatId;

    public CallbackQueryEvent(UpdatesHandler updatesHandler, Update update, CallbackQuery callbackQuery, String chatId) {
        super(updatesHandler);
        this.update = update;
        this.callbackQuery = callbackQuery;
        this.message = callbackQuery.getMessage();
        this.chatId = chatId;
    }
}