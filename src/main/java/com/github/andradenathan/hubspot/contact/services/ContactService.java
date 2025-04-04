package com.github.andradenathan.hubspot.contact.services;

import com.github.andradenathan.base.exceptions.RateLimitExceededException;
import com.github.andradenathan.base.exceptions.UnauthorizedException;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactRequestDTO;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactResponseDTO;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotContactDTO;
import com.github.andradenathan.hubspot.oauth.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ContactService {
    @Value("${hubspot.api-url}")
    private String apiUrl;

    @Value("${hubspot.access-token}")
    private String accessToken;

    private static final String HUBSPOT_CONTACTS_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final RestTemplate restTemplate;

    private final AuthService authService;

    public ContactService(RestTemplateBuilder restTemplateBuilder, AuthService authService) {
        this.authService = authService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Retryable(
            retryFor = { RateLimitExceededException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 1.0)
    )
    public CreateContactResponseDTO create(CreateContactRequestDTO createContactRequestDTO)
            throws RateLimitExceededException {
        HubSpotContactDTO hubSpotContactDTO = HubSpotContactDTO.from(createContactRequestDTO);

        HttpHeaders headers = authService.createHeaders(accessToken);

        HttpEntity<HubSpotContactDTO> requestEntity = new HttpEntity<>(hubSpotContactDTO, headers);

        try {
            ResponseEntity<HubSpotContactDTO> response = restTemplate.exchange(
                    HUBSPOT_CONTACTS_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    HubSpotContactDTO.class
            );

            return new CreateContactResponseDTO(response.getBody());
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value())
                throw new UnauthorizedException("Unauthorized: " + e.getMessage());

            if(e.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new RateLimitExceededException("Rate limit exceeded, try again later.");
            }

            throw e;
        }
    }
}