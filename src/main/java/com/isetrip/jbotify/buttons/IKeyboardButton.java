package com.isetrip.jbotify.buttons;

import com.isetrip.jbotify.lang.Lang;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface IKeyboardButton {

    public String getButton(Lang lang);

    public String getKey();

    public ButtonType getButtonType();

    public boolean canExecute(String id);

    public void process(Lang lang, Update update);

}
