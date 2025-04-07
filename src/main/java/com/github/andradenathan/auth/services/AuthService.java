package com.github.andradenathan.auth.services;

import com.github.andradenathan.auth.dtos.AuthTokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {
    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.scopes}")
    private String scopes;

    private final String authenticationUrl = "https://app.hubspot.com/oauth/authorize";
    private final String redirectUrl = "http://localhost:8090/auth/callback";
    private final String tokenUrl = "https://api.hubapi.com/oauth/v1/token";

    private final RestTemplate restTemplate;

    private final TokenService tokenService;

    public AuthService(RestTemplateBuilder restTemplateBuilder, TokenService tokenService) {
        this.restTemplate = restTemplateBuilder.build();
        this.tokenService = tokenService;
    }

    public AuthTokenResponseDTO exchangeCodeForToken(String authorizationCode) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUrl);
        requestBody.add("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AuthTokenResponseDTO> response = restTemplate.exchange(
                tokenUrl,
                org.springframework.http.HttpMethod.POST,
                requestEntity,
                AuthTokenResponseDTO.class
        );

        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to exchange authorization code for token");
        }

        AuthTokenResponseDTO tokenDTO = response.getBody();
        tokenService.setAccessToken(tokenDTO.access_token());

        return tokenDTO;
    }

    public String mountAuthorizationUrl() {
        return UriComponentsBuilder.fromUriString(authenticationUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("scope", scopes)
                .queryParam("response_type", "code")
                .toUriString();
    }

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        String accessToken = tokenService.getAccessToken();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        return headers;
    }
}
