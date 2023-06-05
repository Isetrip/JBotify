package com.isetrip.jbotify.commands;

import com.isetrip.jbotify.UpdatesHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandBase {

    public String getName();

    public String getUsage();

    public String getApplying();

    public boolean canExecute(String id);

    public void process(UpdatesHandler updatesHandler, String[] args, Update update);

}
