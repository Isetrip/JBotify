package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.events.elements.core.Event;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiveEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private Message message;
    @Getter
    private String chatId;

    public MessageReceiveEvent(UpdatesHandler updatesHandler, Update update, Message message, String chatId) {
        super(updatesHandler);
        this.update = update;
        this.message = message;
        this.chatId = chatId;
    }
}
