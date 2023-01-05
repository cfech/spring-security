package com.eazybytes.controller;


import com.eazybytes.model.Contact;
import com.eazybytes.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Date;
import java.util.Random;

@RestController
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/contact")
    public Contact saveContactInquiryDetails(@RequestBody Contact contact) {
        contact.setContactId(getServiceReqNumber());
        contact.setCreateDt(new Date(System.currentTimeMillis()));
        return contactRepository.save(contact);
    }

    //for random service request number
    public String getServiceReqNumber() {
        Random random = new Random();
        int ranNum = random.nextInt(9999999 - 9999) + 9999;
        return "SR"+ranNum;
    }

}
