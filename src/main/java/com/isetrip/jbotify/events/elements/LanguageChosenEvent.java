package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.data.JBUser;
import com.isetrip.jbotify.events.elements.core.Event;
import com.isetrip.jbotify.lang.Lang;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

public class LanguageChosenEvent extends Event {

    @Getter
    private Lang lang;
    @Getter
    private String chatId;
    @Getter
    private Update update;
    @Getter
    private JBUser jbUser;

    public LanguageChosenEvent(UpdatesHandler updatesHandler, Lang lang, String chatId, Update update, JBUser jbUser) {
        super(updatesHandler);
        this.lang = lang;
        this.chatId = chatId;
        this.update = update;
        this.jbUser = jbUser;
    }
}
