package com.github.andradenathan.hubspot.webhook.handlers;

import com.github.andradenathan.contact.services.ContactService;
import com.github.andradenathan.hubspot.contact.services.HubSpotContactService;
import com.github.andradenathan.hubspot.webhook.WebhookSubscriptionType;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import org.springframework.stereotype.Service;

@Service
public class ContactCreationEventHandler implements WebhookEventHandler {
    private final HubSpotContactService hubSpotContactService;
    private final ContactService contactService;

    public ContactCreationEventHandler(
            HubSpotContactService hubSpotContactService,
            ContactService contactService) {
        this.hubSpotContactService = hubSpotContactService;
        this.contactService = contactService;
    }

    @Override
    public WebhookSubscriptionType getHandledType() {
        return WebhookSubscriptionType.CONTACT_CREATION;
    }

    @Override
    public void handle(WebhookPayloadDTO webhookPayloadDTO) {
        var contact = hubSpotContactService.findContactById(webhookPayloadDTO.objectId());
        contactService.save(contact.properties());
    }
}
