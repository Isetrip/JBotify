package com.isetrip.jbotify.commands;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.lang.Lang;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandBase {

    public String getName();

    public String getUsage();

    public String getApplying(Lang lang);

    public boolean canExecute(String id);

    public void process(UpdatesHandler updatesHandler, String[] args, Update update, Lang lang);

}
