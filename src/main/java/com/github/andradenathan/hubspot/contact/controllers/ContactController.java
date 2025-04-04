package com.github.andradenathan.hubspot.contact.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactRequestDTO;
import com.github.andradenathan.hubspot.contact.dtos.CreateContactResponseDTO;
import com.github.andradenathan.hubspot.contact.services.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> createContact(@RequestBody CreateContactRequestDTO createContactDTO) {
        CreateContactResponseDTO createContactResponseDTO = contactService.create(createContactDTO);

        return ResponseEntity.ok(
                new BaseResponse(createContactResponseDTO.contact(), "Contact created successfully", "success")
        );
    }
}
