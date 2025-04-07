package com.github.andradenathan.hubspot.webhook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andradenathan.contact.services.ContactService;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotGetContactResponseDTO;
import com.github.andradenathan.hubspot.contact.services.HubSpotContactService;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WebhookService {
    private HubSpotContactService hubSpotContactService;
    private ContactService contactService;

    private WebhookSignatureVerifier webhookSignatureVerifier;

    public WebhookService(
            ContactService contactService,
            HubSpotContactService hubSpotContactService,
            WebhookSignatureVerifier webhookSignatureVerifier) {
        this.contactService = contactService;
        this.webhookSignatureVerifier = webhookSignatureVerifier;
        this.hubSpotContactService = hubSpotContactService;
    }

    public List<WebhookPayloadDTO> handleHubSpotWebhook(
            HttpServletRequest request,
            String signature,
            String timestamp,
            String rawBody) throws Exception {
        webhookSignatureVerifier.validate(request, rawBody, timestamp, signature);

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
        HubSpotGetContactResponseDTO contact = hubSpotContactService.findContactById(webhookPayloadDTO.objectId());

        contactService.save(contact.properties());
    }
}
