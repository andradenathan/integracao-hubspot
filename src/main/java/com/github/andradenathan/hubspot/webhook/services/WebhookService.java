package com.github.andradenathan.hubspot.webhook.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andradenathan.base.exceptions.InvalidSignatureException;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class WebhookService {
    @Value("${hubspot.webhook.secret}")
    private String webhookSecret;

    public WebhookPayloadDTO handleHubSpotWebhook(String signature, String rawBody) throws Exception {
        String expectedSignature = calculateSignature(rawBody);

        if(!expectedSignature.equals(signature)) {
            throw new InvalidSignatureException("Invalid HubSpot webhook signature");
        }

        ObjectMapper mapper = new ObjectMapper();
        WebhookPayloadDTO webhookPayloadDTO = mapper.readValue(rawBody, WebhookPayloadDTO.class);

        if(!webhookPayloadDTO.subscriptionType().equals(WebhookSubscriptionType.CONTACT_CREATION)) {
            throw new RuntimeException("Invalid subscription type: it must be contact.creation");
        }

        handleContactCreation(webhookPayloadDTO);

        return webhookPayloadDTO;
    }

    private void handleContactCreation(WebhookPayloadDTO webhookPayloadDTO) {}

    private String calculateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
