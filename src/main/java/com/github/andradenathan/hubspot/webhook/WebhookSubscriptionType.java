package com.github.andradenathan.hubspot.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookSubscriptionType {
    CONTACT_CREATION("contact.creation"),
    ;

    private final String value;

    WebhookSubscriptionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WebhookSubscriptionType fromValue(String value) {
        for (WebhookSubscriptionType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown subscription type: " + value);
    }
}
