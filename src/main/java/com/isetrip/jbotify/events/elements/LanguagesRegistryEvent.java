package com.isetrip.jbotify.events.elements;

import com.isetrip.jbotify.events.elements.core.Event;
import com.isetrip.jbotify.managers.LangManager;
import lombok.Getter;

public class LanguagesRegistryEvent extends Event {

    @Getter
    private LangManager langManager;

    public LanguagesRegistryEvent(LangManager langManager) {
        super(null);
        this.langManager = langManager;
    }

    public void register(String lang) {
        this.langManager.loadLanguageProperties(lang);
    }

}
