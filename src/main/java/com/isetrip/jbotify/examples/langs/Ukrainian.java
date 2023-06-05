package com.isetrip.jbotify.examples.langs;

import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.root.annotations.RegisterLang;

@RegisterLang
public class Ukrainian implements Lang {


    @Override
    public String name() {
        return "ua_UK";
    }

    @Override
    public String button() {
        return "Українська";
    }
}
