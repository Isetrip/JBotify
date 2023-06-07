package com.isetrip.jbotify;

import com.isetrip.jbotify.buttons.ButtonType;
import com.isetrip.jbotify.buttons.ButtonsRegistry;
import com.isetrip.jbotify.data.BotData;
import com.isetrip.jbotify.data.JBUser;
import com.isetrip.jbotify.events.elements.CallbackQueryEvent;
import com.isetrip.jbotify.events.elements.LanguageChosenEvent;
import com.isetrip.jbotify.events.elements.MessageReceiveEvent;
import com.isetrip.jbotify.events.elements.UnknownCommandEvent;
import com.isetrip.jbotify.logs.Log;
import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.utils.DefaultUtils;
import com.isetrip.jbotify.utils.LangUtils;
import com.isetrip.jbotify.utils.ReplyKeyboardUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

public class UpdatesHandler extends TelegramLongPollingBot {

    private final BotData botData;

    public UpdatesHandler(BotData botData) {
        this.botData = botData;
    }

    @Override
    public String getBotUsername() {
        return this.botData.getName();
    }

    @Override
    public String getBotToken() {
        return this.botData.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            SendChatAction action = new SendChatAction();
            action.setAction(ActionType.TYPING);
            action.setChatId(update.getMessage().getChatId().toString());
            execute(action, true);
            Lang langTemp = null;
            JBUser user = JBotifyApplication.getJbUsersSet().findByField("userId", update.getMessage().getFrom().getId().toString()).stream()
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                String username = update.getMessage().getFrom().getUserName();
                if (username == null)
                    username = update.getMessage().getFrom().getFirstName();
                user = JBUser.builder()
                        .userId(update.getMessage().getFrom().getId().toString())
                        .userName(username)
                        .userLang("en_UK")
                        .chatId(update.getMessage().getChatId().toString())
                        .build();
                JBotifyApplication.getJbUsersSet().add(user);
            }
            String text = update.getMessage().getText();
            Log.info(String.format("Message{%s}:%s", update.getMessage().getChatId(), text));
            if (text.startsWith("/") && !JBotifyApplication.getCommandRegister().use(this, text, update, user.getUserLang())) {
                JBotifyApplication.getEventsRegister().publish(new UnknownCommandEvent(this, update, update.getMessage(), update.getMessage().getChatId().toString(), user));
            } else if ((langTemp = LangUtils.getLangFromButton(text)) != null) {
                user.setUserLang(langTemp.name());
                JBotifyApplication.getJbUsersSet().update(user);
                JBotifyApplication.getEventsRegister().publish(new LanguageChosenEvent(this, langTemp, update.getMessage().getChatId().toString(), update, user));
            } else if (!JBotifyApplication.getButtonsRegistry().use(this, text, update, user.getUserLang())) {
                JBotifyApplication.getEventsRegister().publish(new MessageReceiveEvent(this, update, update.getMessage(), update.getMessage().getChatId().toString(), user));
            }
        } else if (update.hasCallbackQuery()) {
            SendChatAction action = new SendChatAction();
            action.setAction(ActionType.TYPING);
            action.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            execute(action, true);
            JBUser user = JBotifyApplication.getJbUsersSet().findByField("userId", update.getCallbackQuery().getFrom().getId().toString()).stream()
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                String username = update.getCallbackQuery().getFrom().getUserName();
                if (username == null)
                    username = update.getCallbackQuery().getFrom().getFirstName();
                user = JBUser.builder()
                        .userId(update.getCallbackQuery().getFrom().getId().toString())
                        .userName(username)
                        .userLang("en_UK")
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .build();
                JBotifyApplication.getJbUsersSet().add(user);
            }
            Log.info(String.format("CallbackQuery{%s}:%s", update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().toString()));
            JBotifyApplication.getEventsRegister().publish(new CallbackQueryEvent(this, update, update.getCallbackQuery(), update.getCallbackQuery().getMessage().getChatId().toString(), user));
        }
    }

    public void disclosure(String message) {
        JBotifyApplication.getJbUsersSet().forEach(user -> sendMessage(message, user.getUserId(), true, user.getUserLang()));
    }

    public void sendMessage(String message, String chatId, boolean ignore, Lang lang, String... btns) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (lang != null) {
            String[] buttons = JBotifyApplication.getButtonsRegistry().getButtons(ButtonType.DEFAULT, lang);
            if (btns.length > 0)
                buttons = DefaultUtils.concat(btns, buttons);
            sendMessage.setReplyMarkup(ReplyKeyboardUtils.getWithButtons(buttons));
        }
        sendMessage.setParseMode("Markdown");
        execute(sendMessage, ignore);
    }

    public void sendMessageWithLangs(String message, String chatId, boolean ignore) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(ReplyKeyboardUtils.getWithButtons(LangUtils.getLangsButtons()));
        sendMessage.setParseMode("Markdown");
        execute(sendMessage, ignore);
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method, boolean ignore) {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            if (!ignore) Log.error(JBotifyApplication.getStackTrace(e));
        }
        return null;
    }

}
