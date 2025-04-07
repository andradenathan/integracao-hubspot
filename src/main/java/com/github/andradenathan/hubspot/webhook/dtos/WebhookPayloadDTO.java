package com.github.andradenathan.hubspot.webhook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayloadDTO(
        Long eventId,
        Long subscriptionId,
        Long portalId,
        Long appId,
        Long occurredAt,
        WebhookSubscriptionType subscriptionType,
        Long objectId,
        String propertyName,
        String propertyValue,
        String changeFlag,
        String changeSource,
        Long attemptNumber,
        Long messageId,
        String messageType
) {}
