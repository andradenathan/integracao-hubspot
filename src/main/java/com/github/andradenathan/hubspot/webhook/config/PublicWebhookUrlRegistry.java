package com.github.andradenathan.hubspot.webhook.config;

import org.springframework.stereotype.Component;

@Component
public class PublicWebhookUrlRegistry {
    private volatile String publicWebhookUrl;

    public void set(String url) {
        this.publicWebhookUrl = url.trim();
    }

    public String get() {
        if(publicWebhookUrl == null || publicWebhookUrl.isEmpty()) {
            throw new IllegalStateException("Public webhook URL is not set");
        }

        return publicWebhookUrl;
    }
}
