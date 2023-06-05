package com.isetrip.jbotify.events.elements.core;

import com.isetrip.jbotify.UpdatesHandler;
import lombok.Getter;

public abstract class Event {

    @Getter
    private UpdatesHandler updatesHandler;

    public Event(UpdatesHandler updatesHandler) {
        this.updatesHandler = updatesHandler;
    }
}
