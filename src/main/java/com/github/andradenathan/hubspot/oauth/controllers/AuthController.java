package com.github.andradenathan.hubspot.oauth.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.oauth.dtos.AuthTokenResponseDTO;
import com.github.andradenathan.hubspot.oauth.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


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

    @GetMapping("/callback")
    public ResponseEntity<?> handleAuthCallback(@RequestParam("code") String code) {
        try {
            AuthTokenResponseDTO tokenResponseDTO = authService.exchangeCodeForToken(code);
            return ResponseEntity.ok(
                    new BaseResponse(tokenResponseDTO, "Token generated successfully", "success")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Erro ao trocar código pelo token", "error"));
        }
    }
}
