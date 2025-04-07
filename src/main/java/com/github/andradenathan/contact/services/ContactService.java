package com.github.andradenathan.contact.services;

import com.github.andradenathan.contact.Contact;
import com.github.andradenathan.contact.dtos.ContactDTO;
import com.github.andradenathan.contact.mappers.ContactMapper;
import com.github.andradenathan.contact.repositories.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    private ContactRepository contactRepository;
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public void save(ContactDTO contactDTO) {
        Contact contact = ContactMapper.toEntity(contactDTO);
        contactRepository.save(contact);
    }
}
