package com.isetrip.jbotify.examples.commands;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.root.annotations.RegisterCommand;
import com.isetrip.jbotify.utils.LangUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@RegisterCommand
public class StartCommand implements CommandBase {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getUsage() {
        return "/start";
    }

    @Override
    public String getApplying() {
        return "Starts the bot";
    }

    @Override
    public boolean canExecute(String id) {
        return true;
    }

    @Override
    public void process(UpdatesHandler updatesHandler, String[] args, Update update) {
        updatesHandler.sendMessageWithLangs("Choose lang!", update.getMessage().getChatId().toString(),
                false);

        //updatesHandler.sendMessage("Hi", update.getMessage().getChatId().toString(), false, LangUtils.of("ua_UK"));
    }

}
