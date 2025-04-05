package com.github.andradenathan.hubspot.oauth.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.oauth.dtos.AuthTokenResponseDTO;
import com.github.andradenathan.hubspot.oauth.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/authorization-url")
    public ResponseEntity<BaseResponse> getAuthorizationUrl() {
        String url = authService.mountAuthorizationUrl();

        return ResponseEntity.ok(
                new BaseResponse(url, "Authorization URL generated successfully", "success")
        );
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleAuthCallback(@RequestParam("code") String code) {
        AuthTokenResponseDTO tokenResponseDTO = authService.exchangeCodeForToken(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                new BaseResponse(tokenResponseDTO, "Token generated successfully", "success")
        );
    }
}
