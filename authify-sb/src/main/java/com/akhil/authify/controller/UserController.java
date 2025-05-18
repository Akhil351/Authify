package com.akhil.authify.controller;

import com.akhil.authify.response.ApiResponse;
import com.akhil.authify.request.UserRequest;
import com.akhil.authify.response.UserResponse;
import com.akhil.authify.service.UserService;
import com.akhil.authify.service.impl.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody  UserRequest userRequest) {
        UserResponse response=userService.createUser(userRequest);
        // send welcome email
        emailService.sendWelcomeEmail(response.getEmail(),response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().data(response).build());
    }

    @GetMapping("/test")
    public String test(){
      return "Auth is working";
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        UserResponse response=userService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.builder().data(response).build());
    }



}

//✅ What it does in simple English:
//It gets the email/username of the currently logged-in user from the Spring Security context, and directly injects it into the method parameter.
//
//        🔍 How it works:
//@CurrentSecurityContext accesses the SecurityContext.
//
//        expression = "authentication?.name" fetches the name field from the Authentication object (usually the email or username).
