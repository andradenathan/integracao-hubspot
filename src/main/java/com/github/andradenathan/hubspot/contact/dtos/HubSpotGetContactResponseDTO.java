package com.github.andradenathan.hubspot.contact.dtos;

import com.github.andradenathan.contact.dtos.ContactDTO;

public record HubSpotGetContactResponseDTO(
        String id,
        ContactDTO properties,
        String createdAt,
        String updatedAt,
        boolean archived
) {}
