package com.eazybytes.filter;

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// will be injected after the basic authentication filter
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {


    // must override  doFilterInternal instead of doFilter because this is a oncePerRequestFilter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //
        if (null != authentication) {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

            // build the JWT based on the USER in the security context
            // can save any type of info, dont save the password
            String jwt = Jwts.builder().setIssuer("Eazy Bank").setSubject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    //issue date and expiration
                    .setIssuedAt(new Date())
                    // set for 30 mins here
                    .setExpiration(new Date((new Date()).getTime() + 1800000))
                    .signWith(key).compact();
            // set in the response and send to the frontend
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        //pass to next chain
        filterChain.doFilter(request, response);
    }

    // only apply this filter on /user path, ie: only generate when logging in the first time
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user");
    }

    /**
     *
     * @param collection collection og granted authorizes ad returns a string of the authorities
     * @return
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

}