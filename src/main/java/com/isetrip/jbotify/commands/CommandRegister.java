package com.isetrip.jbotify.commands;

import com.isetrip.jbotify.UpdatesHandler;
import com.isetrip.jbotify.lang.Lang;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandRegister {

    private final HashMap<String, CommandBase> COMMANDS_MAP;

    public CommandRegister() {
        this.COMMANDS_MAP = new HashMap<>();
    }

    public void register(CommandBase commandBase) {
        this.COMMANDS_MAP.put(commandBase.getName(), commandBase);
    }

    public boolean use(UpdatesHandler updatesHandler, String line, Update update, Lang lang) {
        String command;
        String[] commandArgs;

        Pattern pattern = Pattern.compile("/(\\w+)(?:[\\s_]+(.*))?");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            command = matcher.group(1);
            String argsString = matcher.group(2);
            commandArgs = (argsString != null) ? argsString.split("[\\s_]+") : new String[0];
        } else return false;

        if (this.COMMANDS_MAP.containsKey(command)) {
            CommandBase commandBase = this.COMMANDS_MAP.get(command);
            if (commandBase.canExecute(update.getMessage().getChatId().toString())) {
                commandBase.process(updatesHandler, commandArgs, update, lang);
                return true;
            }
        }
        return false;
    }

    public List<CommandBase> getCommands() {
        List<CommandBase> result = new ArrayList<>();
        for (String key : this.COMMANDS_MAP.keySet()) {
            result.add(this.COMMANDS_MAP.get(key));
        }
        return result;
    }
}
