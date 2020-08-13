package com.javasolution.app.mentoring.security;

public class SecurityConstants {

    public static final String HEADER_AUTHORIZATION="Authorization";
    public static final String TOKEN_PREFIX="Bearer ";
    public static final long EXPIRATION_TIME=864000000; //10 days
    public static final String SECRET_KEY = "secret";
    public static final String H2_CONSOLE="/h2-console/**";
}
