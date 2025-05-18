package com.akhil.authify.controller;

import com.akhil.authify.exception.ApiException;
import com.akhil.authify.request.OtpRequest;
import com.akhil.authify.request.ResetPasswordRequest;
import com.akhil.authify.response.ApiResponse;
import com.akhil.authify.request.AuthRequest;
import com.akhil.authify.response.AuthResponse;
import com.akhil.authify.service.UserService;
import com.akhil.authify.service.impl.AppUserDetailsService;
import com.akhil.authify.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try{
            authenticate(authRequest.getEmail(),authRequest.getPassword());
            UserDetails userDetails=appUserDetailsService.loadUserByUsername(authRequest.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie=ResponseCookie.from("jwt",jwt)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofMinutes(10))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(ApiResponse.builder().data(AuthResponse.builder().email(userDetails.getUsername()).token(jwt).build()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().status("failed").error(e.getMessage()).build());
        }
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<ApiResponse> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email) {
         return ResponseEntity.ok(ApiResponse.builder().data(email!=null).build());
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    @PostMapping("/send-reset-otp")
    public ResponseEntity<ApiResponse> sendResetOtp(@RequestParam String email){
        try{
            userService.sendResetOtp(email);
            return ResponseEntity.ok().body(ApiResponse.builder().status("success").data("OTP has been sent successfully").build());
        } catch(Exception ex){
            throw new ApiException("unable to send otp");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse>  resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        userService.resetPassword(resetPasswordRequest.getEmail(),resetPasswordRequest.getOtp(),resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok().body(ApiResponse.builder().status("success").data("Your password has been reset successfully.").build());
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try{
            userService.sendOtp(email);
            return ResponseEntity.ok().body(ApiResponse.builder().status("success").data("OTP has been sent successfully").build());
        } catch(Exception ex){
            throw new ApiException("unable to send otp");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse>  verifyEmail(@RequestBody OtpRequest request,
                             @CurrentSecurityContext(expression = "authentication?.name") String email  ) {
        if (request.getOtp()==null){
            throw new ApiException("otp required");
        }
        userService.verifyOtp(email,request.getOtp());
        return ResponseEntity.ok().body(ApiResponse.builder().status("success").data("Email verified successfully!").build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response){
        ResponseCookie cookie=ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(ApiResponse.builder().data("Logged out successfully").build());
    }
}


//🧱 Let's explain each line:
//        ✅ ResponseCookie.from("jwt", jwt)
//Creates a cookie with:
//
//Name: "jwt"
//
//Value: the JWT token string
//
//✅ .httpOnly(true)
//Important for security
//
//Makes the cookie inaccessible to JavaScript
//
//Prevents XSS attacks (so document.cookie can't read it)
//
//        ✅ .path("/")
//The cookie will be sent with all requests to your site (since it matches the root path /).
//
//        ✅ .maxAge(Duration.ofMinutes(10))
//Sets the lifetime of the cookie to 10 minutes
//
//After 10 minutes, it will expire automatically
//
//✅ .sameSite("Strict")
//Prevents the cookie from being sent on cross-site requests
//
//Helps protect against CSRF attacks
//
//        With Strict, the cookie will not be sent if the request comes from another origin (like a 3rd party site or form)
