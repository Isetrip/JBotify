package com.isetrip.jbotify;

import com.isetrip.jbotify.data.JBotifyConfiguration;
import com.isetrip.jbotify.events.elements.CallbackQueryEvent;
import com.isetrip.jbotify.events.elements.MessageReceiveEvent;
import com.isetrip.jbotify.events.elements.UnknownCommandEvent;
import com.isetrip.jbotify.events.elements.WebAppDataReceiveEvent;
import com.isetrip.jbotify.managers.CustomUpdateActionManager;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class JBotifyHandler implements UpdatesListener {

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::process);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void process(Update update) {
        if (CustomUpdateActionManager.getInstance().act(update))
            return;
        if (update.message() != null) {
            Message message = update.message();
            if (message.webAppData() != null) {
                JBotifyApplication.getEventsRegister().publish(new WebAppDataReceiveEvent(this, update, message.webAppData()));
            } else if (message.entities() != null && message.entities().length > 0) {
                MessageEntity[] entities = message.entities();
                for (MessageEntity entity : entities) {
                    if (entity.type() != MessageEntity.Type.bot_command) continue;
                    String result = message.text().substring(entity.offset() + 1, entity.offset() + entity.length());
                    if (!JBotifyApplication.getCommandRegister().use(result, update))
                        JBotifyApplication.getEventsRegister().publish(new UnknownCommandEvent(this, update, update.message()));
                }
            } else {
                JBotifyApplication.getEventsRegister().publish(new MessageReceiveEvent(this, update, update.message()));
            }
        } else if (update.callbackQuery() != null) {
            JBotifyApplication.getEventsRegister().publish(new CallbackQueryEvent(this, update, update.callbackQuery()));
        }
    }

}
