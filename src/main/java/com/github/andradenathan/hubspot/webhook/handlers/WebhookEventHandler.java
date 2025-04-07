package com.github.andradenathan.hubspot.webhook.handlers;

import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;

public interface WebhookEventHandler {
    WebhookSubscriptionType getHandledType();
    void handle(WebhookPayloadDTO webhookPayloadDTO);
}
