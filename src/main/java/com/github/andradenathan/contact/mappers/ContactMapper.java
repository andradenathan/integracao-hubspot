package com.github.andradenathan.contact.mappers;

import com.github.andradenathan.contact.Contact;
import com.github.andradenathan.contact.dtos.ContactDTO;

public class ContactMapper {
    public static Contact toEntity(ContactDTO contactDTO) {
        return new Contact(
                contactDTO.firstname(),
                contactDTO.lastname(),
                contactDTO.email()
        );
    }
}
