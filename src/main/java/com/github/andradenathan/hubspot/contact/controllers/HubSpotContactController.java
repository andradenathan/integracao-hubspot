package com.github.andradenathan.hubspot.contact.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotCreateContactRequestDTO;
import com.github.andradenathan.hubspot.contact.dtos.HubSpotCreateContactResponseDTO;
import com.github.andradenathan.hubspot.contact.services.HubSpotContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hubspot")
public class HubSpotContactController {
    private final HubSpotContactService hubSpotContactService;

    public HubSpotContactController(HubSpotContactService hubSpotContactService) {
        this.hubSpotContactService = hubSpotContactService;
    }

    @PostMapping("contact")
    public ResponseEntity<BaseResponse> createContact(@RequestBody HubSpotCreateContactRequestDTO createContactDTO) {
        HubSpotCreateContactResponseDTO hubSpotCreateContactResponseDTO = hubSpotContactService.create(createContactDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BaseResponse(
                        hubSpotCreateContactResponseDTO,
                        "Contact created successfully", "success")
        );
    }
}
