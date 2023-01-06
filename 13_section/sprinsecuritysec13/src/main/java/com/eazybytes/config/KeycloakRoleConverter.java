package com.eazybytes.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// is going to convert the JWT into a list of granted authorities
public class KeycloakRoleConverter  implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        //get the client data inside the key "realm access"
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        // return none if real is empty
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }

        // return a List of Granted Authorities
        return ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}