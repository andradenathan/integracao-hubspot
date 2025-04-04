package com.github.andradenathan.hubspot.webhook.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import com.github.andradenathan.hubspot.webhook.services.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> handleHubSpotWebhook(
            @RequestHeader("X-HubSpot-Signature-v3") String signature,
            @RequestHeader("X-HubSpot-Request-Timestamp") String timestamp,
            @RequestBody String rawBody) throws Exception {

        logger.info("Received HubSpot webhook with signature: {}", signature);
        logger.info("Received HubSpot webhook with timestamp: {}", timestamp);
        logger.info("Received HubSpot webhook with body: {}", rawBody);

        List<WebhookPayloadDTO> webhookPayloadDTO = webhookService.handleHubSpotWebhook(signature, timestamp, rawBody);

        logger.info("Webhook payload: {}", webhookPayloadDTO);
        logger.info("Webhook handled successfully");

        return ResponseEntity.status(HttpStatus.OK).body(
                new BaseResponse(
                        webhookPayloadDTO,
                        "Contact created successfully",
                        "success"
                )
        );
    }
}
