package com.github.andradenathan.hubspot.webhook.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.webhook.dtos.WebhookPayloadDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/contact-created")
    public ResponseEntity<BaseResponse> handleContactCreated(@RequestBody WebhookPayloadDTO webhookPayloadDTO) {}
}
