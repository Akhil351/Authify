package com.akhil.authify.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @Email(message = "Enter valid email address")
    @NotNull(message = "Email Should not be empty")
    private String email;
    @Size(min = 6,message = "Password must be atLeast 6 characters")
    private String password;
}
