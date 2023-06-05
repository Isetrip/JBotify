package com.isetrip.jbotify.buttons;

import com.isetrip.jbotify.JBotifyApplication;
import com.isetrip.jbotify.lang.Lang;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonsRegistry {

    private final HashMap<Integer, IKeyboardButton> BUTTONS_MAP;
    private final HashMap<String, Integer> INDEXES_MAP;
    private int currentIndex = 0;

    public ButtonsRegistry() {
        this.BUTTONS_MAP = new HashMap<>();
        this.INDEXES_MAP = new HashMap<>();
    }

    public void register(IKeyboardButton keyboardButton) {
        this.BUTTONS_MAP.put(this.currentIndex, keyboardButton);
        for (Lang lang : JBotifyApplication.getLangManager().getLangs()) {
            this.INDEXES_MAP.put(keyboardButton.getButton(lang), this.currentIndex);
        }
        this.currentIndex++;
    }

    public boolean use(String line, Update update, Lang lang) {
        if (this.INDEXES_MAP.containsKey(line)) {
            IKeyboardButton keyboardButton = this.BUTTONS_MAP.get(this.INDEXES_MAP.get(line));
            if (keyboardButton.canExecute(update.getMessage().getChatId().toString())) {
                keyboardButton.process(lang, update);
                return true;
            }
        }
        return false;
    }

    public List<IKeyboardButton> getIButtons() {
        List<IKeyboardButton> result = new ArrayList<>();
        for (Integer key : this.BUTTONS_MAP.keySet()) {
            result.add(this.BUTTONS_MAP.get(key));
        }
        return result;
    }

    public String[] getButtons(ButtonType type, Lang lang) {
        List<String> result = new ArrayList<>();
        for (Integer key : this.BUTTONS_MAP.keySet()) {
            if (this.BUTTONS_MAP.get(key).getButtonType() == type) result.add(this.BUTTONS_MAP.get(key).getButton(lang));
        }
        return result.toArray(new String[0]);
    }

    public String[] getButtons(ButtonType type, String key, Lang lang) {
        List<String> result = new ArrayList<>();
        for (Integer button : this.BUTTONS_MAP.keySet()) {
            IKeyboardButton button1 = this.BUTTONS_MAP.get(button);
            if (button1.getButtonType() == type && button1.getKey().equals(key)) result.add(button1.getButton(lang));
        }
        return result.toArray(new String[0]);
    }
}