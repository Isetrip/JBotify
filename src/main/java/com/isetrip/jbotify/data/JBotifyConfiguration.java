package com.isetrip.jbotify.data;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JBotifyConfiguration {
    private String botToken;

    private String analyticsAddress;
    private int analyticsPort;
}
