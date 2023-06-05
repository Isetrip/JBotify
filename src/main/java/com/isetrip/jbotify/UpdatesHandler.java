package com.isetrip.jbotify;

import com.isetrip.jbotify.buttons.ButtonType;
import com.isetrip.jbotify.buttons.ButtonsRegistry;
import com.isetrip.jbotify.data.BotData;
import com.isetrip.jbotify.events.elements.CallbackQueryEvent;
import com.isetrip.jbotify.events.elements.MessageReceiveEvent;
import com.isetrip.jbotify.logs.Log;
import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.utils.DefaultUtils;
import com.isetrip.jbotify.utils.LangUtils;
import com.isetrip.jbotify.utils.ReplyKeyboardUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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
            Lang langTemp = null;
            String text = update.getMessage().getText();
            if (text.startsWith("/")) {
                JBotifyApplication.getCommandRegister().use(this, text, update);
            } else if ((langTemp = LangUtils.getLangFromButton(text)) != null) {
                // TODO: 05.06.2023 Set User lang
            } else if (!JBotifyApplication.getButtonsRegistry().use(text, update, LangUtils.of("ua_UK"))) {// TODO: 05.06.2023 Change to user lang or default
                JBotifyApplication.getEventsRegister().publish(new MessageReceiveEvent(this, update, update.getMessage(), update.getMessage().getChatId().toString()));
            }
        } else if (update.hasCallbackQuery()) {
            JBotifyApplication.getEventsRegister().publish(new CallbackQueryEvent(this, update, update.getCallbackQuery(), update.getCallbackQuery().getMessage().getChatId().toString()));
        }
    }

    public void disclosure(String message) {
        //DataManager.getUsers().forEach(chatid -> sendMessage(message, chatid, true, DataManager.getLang(chatid)));
    }

    // TODO: 05.06.2023 Make UsersStorage 

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
