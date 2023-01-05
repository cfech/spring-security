package com.eazybytes.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


// have to implement filter
public class RequestValidationBeforeFilter  implements Filter {

    // our auth scheme
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";


    private final Charset credentialsCharset = StandardCharsets.UTF_8;


    //default method that is called in each filter
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //extract the req and res
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //extract the Authorization header
        String header = req.getHeader(AUTHORIZATION);


        if (header != null) {
            header = header.trim();

            // if it starts with "Basic", which is being appended through requests on backend and frontend
            if (StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {

                //strip off Basic and decode the base 63 string
                byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                byte[] decoded;
                try {
                    decoded = Base64.getDecoder().decode(base64Token);

                    String token = new String(decoded, credentialsCharset);

                    //make sure this strign is in the right format of username:password
                    int delim = token.indexOf(":");
                    if (delim == -1) {
                        throw new BadCredentialsException("Invalid basic authentication token");
                    }

                    //strip out the email and check it contents, if it has the word "test" then deny the request
                    String email = token.substring(0, delim);
                    if (email.toLowerCase().contains("test")) {
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                    //if cant parse the token the then token is not valid
                } catch (IllegalArgumentException e) {
                    throw new BadCredentialsException("Failed to decode basic authentication token");
                }
            }
        }
        // pass to next filter
        chain.doFilter(request, response);
    }
}