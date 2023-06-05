package com.isetrip.jbotify.lang.elements;

import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.root.annotations.RegisterLang;

@RegisterLang
public class English implements Lang {

    @Override
    public String name() {
        return "en_UK";
    }

    @Override
    public String button() {
        return "English\uD83C\uDDEC\uD83C\uDDE7";
    }
}
