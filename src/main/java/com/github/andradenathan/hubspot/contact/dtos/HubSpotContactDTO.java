package com.github.andradenathan.hubspot.contact.dtos;

import java.util.Map;

public record HubSpotContactDTO(Map<String, String> properties) {
    public static HubSpotContactDTO from(CreateContactRequestDTO createContactRequestDTO) {
        return new HubSpotContactDTO(Map.of(
                "firstname", createContactRequestDTO.firstName(),
                "lastname", createContactRequestDTO.lastName(),
                "email", createContactRequestDTO.email()
        ));
    }
}
