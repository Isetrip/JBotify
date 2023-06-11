package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.events.elements.core.Event;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class RegisterSpecMenuEvent extends Event {

    @Getter
    private Map<String, CommandBase> commandsMap;

    public RegisterSpecMenuEvent(HashMap<String, CommandBase> commandsMap) {
        super(null);
        this.commandsMap = commandsMap;
    }
}
