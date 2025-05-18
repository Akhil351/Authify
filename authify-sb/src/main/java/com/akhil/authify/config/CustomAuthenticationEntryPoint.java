package com.akhil.authify.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
response.setContentType("application/json");
response.getWriter().write("{\"error\":\"" + "User is not authenticated" + "\"}");
    }
}


//The CustomAuthenticationEntryPoint implements AuthenticationEntryPoint in Spring Security, and its main job is to handle what happens when an unauthenticated user tries to access a protected resource.
//
//✅ Simple Explanation:
//When a user is not logged in (i.e. no valid JWT or session) and tries to access a secured endpoint, this commence() method is triggered.
//
//
