package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.events.elements.core.Event;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;

public class TryExecuteCommandEvent extends Event {

    @Getter
    private CommandBase commandBase;
    @Getter
    private Update update;

    public TryExecuteCommandEvent(CommandBase commandBase, Update update) {
        super(null);
        this.commandBase = commandBase;
        this.update = update;
    }

}
