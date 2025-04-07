package com.github.andradenathan.contact.repositories;

import com.github.andradenathan.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {}