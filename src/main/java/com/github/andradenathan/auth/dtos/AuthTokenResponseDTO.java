package com.github.andradenathan.auth.dtos;

public record AuthTokenResponseDTO(
        String access_token,
        String refresh_token,
        Integer expires_in,
        String token_type,
        String scope
) {}
