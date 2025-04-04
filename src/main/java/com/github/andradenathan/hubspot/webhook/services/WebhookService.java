package com.github.andradenathan.hubspot.webhook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andradenathan.base.exceptions.InvalidSignatureException;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotContactDTO;
import com.github.andradenathan.hubspot.oauth.services.AuthService;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class WebhookService {
    @Value("${hubspot.client-secret}")
    private String clientSecret;
    private String method = "POST";
    private String url = "https://f1c3-179-55-99-49.ngrok-free.app/webhook";

    private RestTemplate restTemplate;
    private AuthService authService;

    public WebhookService(AuthService authService, RestTemplateBuilder restTemplate) {
        this.authService = authService;
        this.restTemplate = restTemplate.build();
    }

    public List<WebhookPayloadDTO> handleHubSpotWebhook(String signature, String timestamp, String rawBody) throws Exception {
        validateHubSpotSignature(rawBody, timestamp, signature);

        ObjectMapper mapper = new ObjectMapper();
        List<WebhookPayloadDTO> payloads = mapper.readValue(
                rawBody,
                new TypeReference<>() {}
        );

        for(WebhookPayloadDTO payload : payloads) {
            if(!payload.subscriptionType().equals(WebhookSubscriptionType.CONTACT_CREATION)) {
                throw new RuntimeException("Invalid subscription type: it must be contact.creation");
            }

            handleContactCreation(payload);
        }

        return payloads;
    }

    private void handleContactCreation(WebhookPayloadDTO webhookPayloadDTO) {
        Long contactId = webhookPayloadDTO.objectId();

        String url = "https://api.hubapi.com/crm/v3/objects/contacts/" + contactId;

        String uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("properties", "firstname,lastname,email")
                .toString();

        HttpHeaders headers = authService.createHeaders();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        HttpEntity<HubSpotContactDTO> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(authService.createHeaders()),
                HubSpotContactDTO.class
        );

        Map<String, String> properties = response.getBody().properties();

    }

    private void validateHubSpotSignature(
            String body,
            String timestamp,
            String receivedSignature
    ) throws InvalidKeyException, NoSuchAlgorithmException {
        long currentMillis = System.currentTimeMillis();
        long requestMillis = Long.parseLong(timestamp);
        long maxAllowedMillis = 5 * 60 * 1000;

        if ((currentMillis - requestMillis) > maxAllowedMillis) {
            throw new InvalidSignatureException("Timestamp is too old");
        }

        String rawString = method + url + body + timestamp;

        String expectedSignature = calculateSignature(rawString, clientSecret);

        if (!MessageDigest.isEqual(expectedSignature.getBytes(), receivedSignature.getBytes())) {
            throw new InvalidSignatureException("Invalid HubSpot webhook signature");
        }
    }

    private String calculateSignature(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
