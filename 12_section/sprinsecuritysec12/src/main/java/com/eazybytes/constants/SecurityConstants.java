package com.eazybytes.constants;

import org.springframework.beans.factory.annotation.Value;

public interface SecurityConstants {

    // secret key value for  create JWTS, this would be injected by DSO at build time in deployment, should not be hardcoded in the project
    public static final String JWT_KEY = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";

}

