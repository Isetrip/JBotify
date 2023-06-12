package com.isetrip.jbotify.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;

public interface CommandBase {

    public String getName();

    public String getDescription(String lang);

    public boolean canExecute(String userId);

    public void process(Update update, TelegramBot bot);

}
