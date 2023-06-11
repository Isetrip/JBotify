package com.isetrip.jbotify.events.elements.core;

import com.isetrip.jbotify.JBotifyHandler;
import lombok.Getter;

public abstract class Event {

    @Getter
    private JBotifyHandler handler;

    public Event(JBotifyHandler handler) {
        this.handler = handler;
    }
}
