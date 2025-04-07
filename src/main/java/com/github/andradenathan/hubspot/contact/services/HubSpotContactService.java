package com.github.andradenathan.hubspot.contact.services;

import com.github.andradenathan.base.exceptions.RateLimitExceededException;
import com.github.andradenathan.base.exceptions.UnauthorizedException;
import com.github.andradenathan.hubspot.contact.dtos.*;
import com.github.andradenathan.auth.services.AuthService;
import com.github.andradenathan.hubspot.webhook.services.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class HubSpotContactService {
    private static final String HUBSPOT_CONTACTS_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final RestTemplate restTemplate;

    private final AuthService authService;

    public HubSpotContactService(RestTemplateBuilder restTemplateBuilder, AuthService authService) {
        this.authService = authService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Retryable(
            retryFor = { RateLimitExceededException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 1.0)
    )
    public HubSpotCreateContactResponseDTO create(HubSpotCreateContactRequestDTO hubSpotCreateContactRequestDTO)
            throws UnauthorizedException, RateLimitExceededException {

        HttpHeaders headers = authService.createHeaders();

        HubSpotContactWrapperDTO hubSpotContactWrapperDTO = new HubSpotContactWrapperDTO(hubSpotCreateContactRequestDTO);

        HttpEntity<HubSpotContactWrapperDTO> requestEntity = new HttpEntity<>(
                hubSpotContactWrapperDTO,
                headers
        );

        try {
            ResponseEntity<HubSpotCreateContactResponseDTO> response = restTemplate.exchange(
                    HUBSPOT_CONTACTS_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    HubSpotCreateContactResponseDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value())
                throw new UnauthorizedException("Unauthorized: " + e.getMessage());

            if(e.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new RateLimitExceededException("Rate limit exceeded, please try again.");
            }

            throw e;
        }
    }

    public HubSpotGetContactResponseDTO findContactById(Long contactId) {
        HttpHeaders headers = authService.createHeaders();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<HubSpotGetContactResponseDTO> response = restTemplate.exchange(
                HUBSPOT_CONTACTS_API_URL + "/" + contactId,
                HttpMethod.GET,
                requestEntity,
                HubSpotGetContactResponseDTO.class
        );

        return response.getBody();
    }
}