package com.eazybytes.controller;


import com.eazybytes.model.Contact;
import com.eazybytes.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/contact")
//    @PreFilter("filterObject.contactName != 'Test'")// don't accept any contact requests with name of test
    @PostFilter("filterObject.contactName != 'Test'")// don't return anything to the ui with a name of "Test"
    public List<Contact> saveContactInquiryDetails(@RequestBody List<Contact> contacts) {
        //get first contact in the list
        Contact contact = contacts.get(0);
        contact.setContactId(getServiceReqNumber());
        contact.setCreateDt(new Date(System.currentTimeMillis()));
        contactRepository.save(contact);

        List<Contact> contactListToReturn = new ArrayList<>();
        contactListToReturn.add(contact);

        return contactListToReturn;

    }

    //for random service request number
    public String getServiceReqNumber() {
        Random random = new Random();
        int ranNum = random.nextInt(9999999 - 9999) + 9999;
        return "SR"+ranNum;
    }

}
