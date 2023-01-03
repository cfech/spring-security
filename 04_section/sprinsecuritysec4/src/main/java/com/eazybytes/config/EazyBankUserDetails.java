package com.eazybytes.config;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


// creating our own userDetailsService,
// userDetailsService is responsible for CRUD operations on the user
// should be called by the authentication manager/user details manager

// must create as a bean
@Service
public class EazyBankUserDetails implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    // overriding loadByUsername, which is called by spring when accessing userDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String userName = null;
        String password = null;

        List<GrantedAuthority> authorities = null;
        List<Customer> customer = customerRepository.findByEmail(username);

        // if not customers throw exception
        if (customer.isEmpty()) {
            throw new UsernameNotFoundException("User details not found for the user : " + username);
        } else{
            userName = customer.get(0).getEmail();
            password = customer.get(0).getPwd();
            authorities = new ArrayList<>();

            // simple granted authority implements GrantedAuthority
            authorities.add(new SimpleGrantedAuthority(customer.get(0).getRole()));
        }

        // returns back to spring security framework where authentication will happen
        return new User(userName,password,authorities);
    }

}
