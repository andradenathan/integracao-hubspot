package com.github.andradenathan.hubspot.webhook.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.webhook.config.PublicWebhookUrlRegistry;
import com.github.andradenathan.hubspot.webhook.dtos.PublicUrlDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/webhook-url")
public class PublicWebhookUrlController {
    private final PublicWebhookUrlRegistry publicWebhookUrlRegistry;

    public PublicWebhookUrlController(PublicWebhookUrlRegistry publicWebhookUrlRegistry) {
        this.publicWebhookUrlRegistry = publicWebhookUrlRegistry;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> setPublicWebhookUrl(@RequestBody PublicUrlDTO publicUrlDTO) {
        publicWebhookUrlRegistry.set(publicUrlDTO.url());
        return ResponseEntity.status(HttpStatus.OK).body(
                new BaseResponse("Public webhook URL set successfully", "success")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getPublicWebhookUrl() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new BaseResponse(
                        publicWebhookUrlRegistry.get(),
                        "Public webhook URL retrieved successfully",
                        "success")
        );
    }
}
