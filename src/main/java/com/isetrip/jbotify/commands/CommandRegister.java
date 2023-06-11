package com.isetrip.jbotify.commands;

import com.isetrip.jbotify.JBotifyApplication;
import com.isetrip.jbotify.events.elements.RegisterSpecMenuEvent;
import com.isetrip.jbotify.managers.LangManager;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllPrivateChats;
import com.pengrad.telegrambot.request.SetMyCommands;

import java.util.*;

public class CommandRegister {

    private final HashMap<String, CommandBase> COMMANDS_MAP;

    public CommandRegister() {
        this.COMMANDS_MAP = new HashMap<>();
    }

    public void register(CommandBase commandBase) {
        this.COMMANDS_MAP.put(commandBase.getName(), commandBase);
    }

    public boolean use(String line, Update update) {
        if (this.COMMANDS_MAP.containsKey(line)) {
            CommandBase commandBase = this.COMMANDS_MAP.get(line);
            if (commandBase.canExecute(update.message().chat().id().toString())) {
                commandBase.process(update, JBotifyApplication.getBot());
                return true;
            }
        }
        return false;
    }

    public Collection<CommandBase> getCommands() {
        return this.COMMANDS_MAP.values();
    }

    public void registerCommandsMenu() {
        Set<String> langs = LangManager.getInstance().getLangs();
        for (String lang : langs) {
            List<BotCommand> commands = new ArrayList<>();
            getCommands().forEach(commandBase -> {
                commands.add(new BotCommand(commandBase.getName(), commandBase.getDescription(lang)));
            });
            JBotifyApplication.getBot().execute(new SetMyCommands(commands.toArray(new BotCommand[0]))
                    .languageCode(lang)
                    .scope(new BotCommandScopeAllPrivateChats()));
        }
        JBotifyApplication.getEventsRegister().publish(new RegisterSpecMenuEvent(this.COMMANDS_MAP));
    }
}
