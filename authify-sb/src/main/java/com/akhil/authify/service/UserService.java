package com.akhil.authify.service;

import com.akhil.authify.request.UserRequest;
import com.akhil.authify.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email,String otp,String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email,String otp);

    String getLoggedInUserId(String email);

}
