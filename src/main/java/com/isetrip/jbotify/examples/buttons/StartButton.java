package com.isetrip.jbotify.examples.buttons;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.buttons.ButtonType;
import com.isetrip.jbotify.buttons.IKeyboardButton;
import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.root.annotations.RegisterButton;
import com.isetrip.jbotify.utils.LangUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@RegisterButton
public class StartButton implements IKeyboardButton {

    @Override
    public String getButton(Lang lang) {
        return LangUtils.get(lang, "startBtn");
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.DEFAULT;
    }

    @Override
    public boolean canExecute(String id) {
        return true;
    }

    @Override
    public void process(UpdatesHandler updatesHandler, Lang lang, Update update) {

    }

}
