package com.github.andradenathan.hubspot.oauth.services;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private volatile String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
