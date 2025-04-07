package com.github.andradenathan.hubspot.webhook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import com.github.andradenathan.hubspot.webhook.handlers.WebhookEventHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WebhookService {
    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;
    private final Map<WebhookSubscriptionType, WebhookEventHandler> handlerMap;

    public WebhookService(
            ObjectMapper objectMapper,
            WebhookSignatureVerifier signatureVerifier,
            List<WebhookEventHandler> handlers) {
        this.signatureVerifier = signatureVerifier;
        this.objectMapper = objectMapper;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(WebhookEventHandler::getHandledType, handler -> handler));
    }

    public List<WebhookPayloadDTO> handleHubSpotWebhook(
            HttpServletRequest request,
            String signature,
            String timestamp,
            String rawBody) throws Exception {
        signatureVerifier.validate(request, rawBody, timestamp, signature);

        List<WebhookPayloadDTO> payloads = objectMapper.readValue(
                rawBody,
                new TypeReference<>() {}
        );

        for(WebhookPayloadDTO payload : payloads) {
            WebhookEventHandler handler = handlerMap.get(payload.subscriptionType());

            if(handler == null) {
                throw new IllegalArgumentException("No handler found for subscription type: " + payload.subscriptionType());
            }

            handler.handle(payload);
        }

        return payloads;
    }
}
