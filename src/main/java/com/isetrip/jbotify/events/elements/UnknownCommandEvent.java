package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.data.JBUser;
import com.isetrip.jbotify.events.elements.core.Event;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownCommandEvent extends Event {

    @Getter
    private Update update;
    @Getter
    private Message message;
    @Getter
    private String chatId;
    @Getter
    private JBUser jbUser;

    public UnknownCommandEvent(UpdatesHandler updatesHandler, Update update, Message message, String chatId, JBUser jbUser) {
        super(updatesHandler);
        this.update = update;
        this.message = message;
        this.chatId = chatId;
        this.jbUser = jbUser;
    }
}