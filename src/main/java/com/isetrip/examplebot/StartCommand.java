package com.isetrip.examplebot;

import com.isetrip.jbotify.annotations.RegisterCommand;
import com.isetrip.jbotify.commands.CommandBase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

@RegisterCommand
public class StartCommand implements CommandBase {
    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription(String lang) {
        return null;
    }

    @Override
    public boolean canExecute(String chatId) {
        return true;
    }

    @Override
    public void process(Update update, TelegramBot bot) {

    }
}
