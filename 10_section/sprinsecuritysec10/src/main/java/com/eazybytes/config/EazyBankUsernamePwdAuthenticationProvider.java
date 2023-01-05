package com.eazybytes.config;

import com.eazybytes.model.Authority;
import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


// have to implement AuthenticationProvider to create our own
@Component
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    //allow us to get user from db
    @Autowired
    private CustomerRepository customerRepository;


    //right now this is the BcryptPasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;


    // called by org.springframework.security.authentication.ProviderManager
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // load user info from authentication object
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();


        // find me the user by their email (which is their username)
        // at this point authorities are already mapped thanks to spring data jpa (done in the customer model on database query)
        List<Customer> listOfMatchingCustomers = customerRepository.findByEmail(username);
        if (!listOfMatchingCustomers.isEmpty()) {

            // use pwd encoder to compare against what is in the db
            if (passwordEncoder.matches(pwd, listOfMatchingCustomers.get(0).getPwd())) {

                // create a UsernamePasswordAuthenticationToken, which in the constructor, sets the authenticated property to true
                // returns this to the authentication manager
                return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(listOfMatchingCustomers.get(0).getAuthorities()));
            } else {

                // password does not match what is in the db
                throw new BadCredentialsException("Invalid password!");
            }
        }else {

            // no user with that email
            throw new BadCredentialsException("No user registered with this details!");
        }
    }


    // will convert our authority into a granted authority
    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }



    // can tell what we ant to support here
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
