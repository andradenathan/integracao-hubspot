package com.github.andradenathan.hubspot.contact.services;

import com.github.andradenathan.base.exceptions.UnauthorizedException;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactRequestDTO;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactResponseDTO;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotContactDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class ContactService {
    @Value("${hubspot.api-url}")
    private String apiUrl;

    @Value("${hubspot.access-token}")
    private String accessToken;

    private static final String HUBSPOT_CONTACTS_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final RestTemplate restTemplate;

    public ContactService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public CreateContactResponseDTO createContact(CreateContactRequestDTO createContactRequestDTO) {
        HubSpotContactDTO hubSpotContactDTO = HubSpotContactDTO.from(createContactRequestDTO);

        HttpHeaders headers = createHeaders();

        HttpEntity<HubSpotContactDTO> requestEntity = new HttpEntity<>(hubSpotContactDTO, headers);

        int retries = 3;

        for(int retry = 0; retry < retries; retry++) {
            try {
                ResponseEntity<HubSpotContactDTO> response = restTemplate.exchange(
                        HUBSPOT_CONTACTS_API_URL,
                        HttpMethod.POST,
                        requestEntity,
                        HubSpotContactDTO.class
                );

                return new CreateContactResponseDTO(response.getBody());
            } catch (HttpClientErrorException e) {
                if(e.getStatusCode().value() == 401) throw new UnauthorizedException("Unauthorized: " + e.getMessage());

                handleRateLimit(e);
            }
        }

        throw new RuntimeException("Rate limit exceeded, try again later.");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        return headers;
    }

    private void handleRateLimit(HttpClientErrorException e) {
        int retryAfter = e.getResponseHeaders().getOrDefault("Retry-After", Collections.singletonList("5"))
                .stream()
                .map(Integer::parseInt)
                .findFirst()
                .orElse(5);

        try {
            Thread.sleep(retryAfter * 1000L);
        } catch (InterruptedException ignored) {}
    }
}