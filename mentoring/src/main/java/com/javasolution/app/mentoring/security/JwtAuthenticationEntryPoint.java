package com.javasolution.app.mentoring.security;

import com.google.gson.Gson;
import com.javasolution.app.mentoring.responses.InvalidLoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException, ServletException {

        final InvalidLoginResponse loginResponse = new InvalidLoginResponse();
        final String jsonLoginResponse = new Gson().toJson(loginResponse);

        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().print(jsonLoginResponse);
    }
}
