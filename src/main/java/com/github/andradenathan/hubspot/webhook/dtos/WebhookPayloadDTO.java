package com.github.andradenathan.hubspot.webhook.dtos;

import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;

public record WebhookPayloadDTO(
        Long eventId,
        Long subscriptionId,
        Long portalId,
        Long appId,
        Long occurredAt,
        WebhookSubscriptionType subscriptionType,
        Long objectId
) {}
