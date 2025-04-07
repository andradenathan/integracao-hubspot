package com.github.andradenathan.contact.controllers;

import com.github.andradenathan.base.BaseResponse;
import com.github.andradenathan.contact.services.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getContacts() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new BaseResponse(contactService.findAll(), "Contacts retrieved successfully", "success")
        );
    }
}
