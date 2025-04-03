package com.github.andradenathan.hubspot.oauth.controllers;

import com.github.andradenathan.base.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${hubspot.scopes}")
    private String scopes;

    private static final String AUTH_URL = "https://app.hubspot.com/oauth/authorize";


    @GetMapping("/authorization-url")
    public ResponseEntity<BaseResponse> getAuthorizationUrl() {
        String url = UriComponentsBuilder.fromUriString(AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scopes)
                .queryParam("response_type", "code")
                .toUriString();

        return ResponseEntity.ok(
                new BaseResponse(url, "Authorization URL generated successfully", "success")
        );
    }
}
