package com.smartcontact.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import com.smartcontact.smartcontactmanager.Dao.ContactRepo;
import com.smartcontact.smartcontactmanager.Dao.UserRepo;
import com.smartcontact.smartcontactmanager.models.Contact;
import com.smartcontact.smartcontactmanager.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Search {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(Principal p, @PathVariable("query") String query) {

        User user = userRepo.getUserByUserName(p.getName());
        List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
}
